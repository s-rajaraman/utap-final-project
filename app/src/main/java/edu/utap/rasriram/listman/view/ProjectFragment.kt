package edu.utap.rasriram.listman.view

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
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
import edu.utap.rasriram.listman.adapter.ProjectAdapter
import edu.utap.rasriram.listman.model.Project
import edu.utap.rasriram.listman.viewmodel.ProjectViewModel


class ProjectFragment : Fragment(R.layout.fragment_project) {
    private val viewModel: ProjectViewModel by activityViewModels()
    private lateinit var adapter: ProjectAdapter
    private lateinit var projects: MutableLiveData<List<Project>>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View =
            inflater.inflate(R.layout.fragment_project, container, false)

        initRecyclerView(view)
        initFAB(view)
        initInbox(view)

        projects = viewModel.observeProjects()
        projects.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it.filter { x -> !x.isDefault })
                adapter.notifyDataSetChanged()
            }
        })

        return view
    }




    private fun initInbox(view: View) {
        val inboxLayout = view.findViewById<LinearLayout>(R.id.inboxLayout)

        inboxLayout.setOnClickListener { l ->

            val projectList = projects.value

            val project = if (projectList != null && projectList.firstOrNull { it.isDefault } != null)  {
                projectList.first { it.isDefault }
            } else {
                val p = Project(title = "Inbox", rowID = viewModel.getRowId(), isDefault = true)
                viewModel.saveProject(p)
                p
            }


            parentFragmentManager
                .beginTransaction()
                .replace(
                    R.id.main_content,
                    TaskFragment(project)
                )
                .addToBackStack("project")
                .commit()
        }
    }

    private fun initFAB(view: View) {
        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.main_content, TaskFragment(Project()))
                .addToBackStack("project")
                .commit()
        }
    }

    private fun initRecyclerView(root: View) {
        adapter = ProjectAdapter(parentFragmentManager)
        projects = viewModel.observeProjects()
        viewModel.readProjects()

        val rv = root.findViewById<RecyclerView>(R.id.list_view)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(context)
        val itemDecor = DividerItemDecoration(rv.context, LinearLayoutManager.VERTICAL)
        itemDecor.setDrawable(ContextCompat.getDrawable(rv.context, (R.drawable.divider))!!)
        rv.addItemDecoration(itemDecor)

        val itemTouchHelper = ItemTouchHelper(SwipeToDelete())
        itemTouchHelper.attachToRecyclerView(rv)
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
            val proj = projects.value
            proj?.let {
                val pos = viewHolder.adapterPosition
                val project = it[pos]
                viewModel.removeProject(project)
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

                val background = ColorDrawable(Color.RED)
                background.setBounds(
                    itemView.right + dX.toInt(),
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )

                background.draw(c)

                val y =   (itemView.top - itemView.bottom)/2 + itemView.bottom.toFloat() + 10

                val paint = Paint()
                paint.color = Color.WHITE
                paint.textSize = 40f
                c.drawText("Delete", itemView.right + dX, y, paint)

            }

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }

    }
}

