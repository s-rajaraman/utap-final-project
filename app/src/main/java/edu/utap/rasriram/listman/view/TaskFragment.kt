package edu.utap.rasriram.listman.view

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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import edu.utap.rasriram.listman.R
import edu.utap.rasriram.listman.adapter.TaskAdapter
import edu.utap.rasriram.listman.model.Project
import edu.utap.rasriram.listman.model.Task
import edu.utap.rasriram.listman.viewmodel.ProjectViewModel

class TaskFragment : Fragment(R.layout.task_view) {
    private val viewModel: ProjectViewModel by activityViewModels()
    private lateinit var adapter: TaskAdapter
    private var project = Project()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View =
            inflater.inflate(R.layout.task_view, container, false)

        adapter = TaskAdapter(viewModel)
        project.rowID = viewModel.getRowId()
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
        return view
    }

    private fun initList(view: View) {
        val rv = view.findViewById<RecyclerView>(R.id.list_view)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(context)

        val itemDecor = DividerItemDecoration(rv.context, LinearLayoutManager.VERTICAL)
        itemDecor.setDrawable(ContextCompat.getDrawable(rv.context, (R.drawable.divider))!!)
        rv.addItemDecoration(itemDecor)

        viewModel
            .observeTasks()
            .observe(viewLifecycleOwner, {
               adapter.submitList(
                    it.filter { it1 -> project.rowID == it1.projectId }
                )
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
        titleET.setOnFocusChangeListener { textView, b ->
            project.title = titleET.text.toString()
            viewModel.saveProject(project)
        }
    }

    private fun initTag(view: View) {
        val tagBadge = view.findViewById<TextView>(R.id.tag_badge)

        tagBadge.setOnClickListener { l ->
            val editText = view.findViewById<EditText>(R.id.tagsET)
            editText.visibility = View.VISIBLE
            editText.requestFocus()
            editText.setOnEditorActionListener { textView, i, keyEvent ->
                if (keyEvent.keyCode == 66) {
                    val tv = TextView(view.context)
                    tv.text = editText.text.toString()
                    val params = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                    params.setMargins(4, 0, 4, 0)
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15F)
                    tv.background = ContextCompat.getDrawable(view.context, R.drawable.tag_style)
                    tv.layoutParams = params

                    val linearLayout = view.findViewById<LinearLayout>(R.id.tagsLayout)

                    tv.setOnLongClickListener { l ->
                        linearLayout.removeView(l)
                        project.tags.remove(tv.text.toString())
                        viewModel.saveProject(project)
                        return@setOnLongClickListener true
                    }

                    if (tv.text.toString() != "") {
                        project.tags.add(tv.text.toString())
                        viewModel.saveProject(project)
                    }

                    linearLayout.addView(tv, linearLayout.childCount - 1)
                    editText.text.clear()

                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }
        }
    }
}