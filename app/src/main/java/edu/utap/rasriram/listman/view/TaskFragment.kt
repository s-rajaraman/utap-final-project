package edu.utap.rasriram.listman.view

import android.app.AlertDialog
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import edu.utap.rasriram.listman.R
import edu.utap.rasriram.listman.adapter.TaskAdapter
import edu.utap.rasriram.listman.model.Project
import edu.utap.rasriram.listman.model.Task
import edu.utap.rasriram.listman.viewmodel.ProjectViewModel

class TaskFragment(private var project: Project) : Fragment(R.layout.task_view) {
    private val viewModel: ProjectViewModel by activityViewModels()
    private lateinit var adapter: TaskAdapter
    private lateinit var tasks: MutableLiveData<List<Task>>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View =
            inflater.inflate(R.layout.task_view, container, false)

        adapter = TaskAdapter(viewModel)
        if (project.rowID.isEmpty()) {
            project.rowID = viewModel.getRowId()
        }
        tasks = viewModel.observeTasks()
        initTag(view)
        initTitle(view)
        initFAB(view)
        initList(view)


        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.popBackStackImmediate()
                }
            })
        viewModel.readTasks()
        return view
    }

    private fun initList(view: View) {
        val rv = view.findViewById<RecyclerView>(R.id.list_view)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(context)

        val itemDecor = DividerItemDecoration(rv.context, LinearLayoutManager.VERTICAL)
        itemDecor.setDrawable(ContextCompat.getDrawable(rv.context, (R.drawable.divider))!!)
        rv.addItemDecoration(itemDecor)

        val itemTouchHelper = ItemTouchHelper(SwipeToDelete())
        itemTouchHelper.attachToRecyclerView(rv)

        val swipeToMove = ItemTouchHelper(SwipeToMove())
        swipeToMove.attachToRecyclerView(rv)

        viewModel
            .observeTasks()
            .observe(viewLifecycleOwner, {
                val tasks = it.filter { it1 -> project.rowID == it1.projectId }
                adapter.submitList(tasks)
                adapter.notifyDataSetChanged()
            })
    }

    private fun initFAB(view: View) {
        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        val list = view.findViewById<RecyclerView>(R.id.list_view)
        list.adapter = adapter

        fab.setOnClickListener { l ->
            val task = Task(rowID = viewModel.getRowId(), projectId = project.rowID)
            viewModel.saveTask(task)

        }
    }

    private fun initTitle(view: View) {
        val titleET = view.findViewById<EditText>(R.id.titleET)
        titleET.setText(project.title)
        if (project.isDefault) {
            titleET.isEnabled = false
        } else {

            titleET.setOnFocusChangeListener { textView, b ->
                project.title = titleET.text.toString()
                viewModel.saveProject(project)
            }

        }
    }

    private fun initTag(view: View) {
        val tagBadge = view.findViewById<TextView>(R.id.tag_badge)

        for (t in project.tags) {
            val tv = TextView(view.context)
            if (t.length > 8) {
                tv.text = t.take(6) + "..."
            } else {

                tv.text = t
            }
            val params = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            params.setMargins(4, 0, 4, 0)
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15F)
            tv.background = ContextCompat.getDrawable(view.context, R.drawable.tag_style)
            tv.layoutParams = params

            val linearLayout = view.findViewById<LinearLayout>(R.id.tagsLayout)
            linearLayout.addView(tv, linearLayout.childCount - 1)

            tv.setOnLongClickListener { l ->
                linearLayout.removeView(l)
                project.tags.remove(t)
                viewModel.saveProject(project)
                return@setOnLongClickListener true
            }

        }

        tagBadge.setOnClickListener { l ->
            val editText = view.findViewById<EditText>(R.id.tagsET)
            editText.visibility = View.VISIBLE
            editText.requestFocus()
            editText.setOnEditorActionListener { textView, i, keyEvent ->
                if (keyEvent.keyCode == 66) {
                    val enteredText = editText.text.toString()
                    val tag = when (enteredText.length) {
                        0 -> return@setOnEditorActionListener false
                        1 -> " $enteredText "
                        2 -> "$enteredText "
                        else -> enteredText

                    }
                    val tv = TextView(view.context)
                    if (tag.length > 8) {
                        tv.text = tag.take(6) + ".."
                    } else {
                        tv.text = tag
                    }
                    val params = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                    params.setMargins(4, 0, 4, 0)
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15F)
                    tv.background = ContextCompat.getDrawable(view.context, R.drawable.tag_style)
                    tv.layoutParams = params

                    val linearLayout = view.findViewById<LinearLayout>(R.id.tagsLayout)

                    tv.setOnLongClickListener { l ->
                        linearLayout.removeView(l)
                        project.tags.remove(tag)
                        viewModel.saveProject(project)
                        return@setOnLongClickListener true
                    }

                    if (tv.text.toString() != "" && project.tags.count() < 6) {
                        project.tags.add(tag)
                        viewModel.saveProject(project)
                        linearLayout.addView(tv, linearLayout.childCount - 1)
                    }

                    editText.text.clear()

                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }
        }
    }

    inner class SwipeToMove : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val taskList = tasks.value
            val projects = viewModel.observeProjects().value

            taskList?.let {

                var task = it[viewHolder.adapterPosition]

                projects?.let { p ->
                    val alertDialog = AlertDialog.Builder(activity)
                        .setItems(p.map { it1 -> it1.title }.toTypedArray())
                        { dialog, which ->

                            task.projectId = p[which].rowID
                            viewModel.saveTask(task)

                            adapter.notifyDataSetChanged()
                        }
                        .setCancelable(false)
                        .create()

                    alertDialog.show()
                }
            }

        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            if (isCurrentlyActive) {
                val itemView = viewHolder.itemView

                val color = Color.parseColor("#89cff0")
                val background = ColorDrawable(color)
                background.setBounds(
                    itemView.left,
                    itemView.top,
                    itemView.left + dX.toInt(),
                    itemView.bottom
                )

                background.draw(c)

                val y = (itemView.top - itemView.bottom) / 2 + itemView.bottom.toFloat() + 10

                val paint = Paint()
                paint.color = Color.WHITE
                paint.textSize = 40f
                c.drawText("Move", itemView.left.toFloat(), y, paint)
            }

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    inner class SwipeToDelete : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val task = tasks.value
            task?.let {
                val pos = viewHolder.adapterPosition
                viewModel.removeTask(it[pos])
            }

        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            val itemView = viewHolder.itemView

            val background = ColorDrawable(Color.RED)
            background.setBounds(
                itemView.right + dX.toInt(),
                itemView.top,
                itemView.right,
                itemView.bottom
            )

            background.draw(c)

            val y = (itemView.top - itemView.bottom) / 2 + itemView.bottom.toFloat() + 10

            val paint = Paint()
            paint.color = Color.WHITE
            paint.textSize = 40f
            c.drawText("Delete", itemView.right + dX, y, paint)

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }
}