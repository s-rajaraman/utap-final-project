package edu.utap.rasriram.listman.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import edu.utap.rasriram.listman.R
import edu.utap.rasriram.listman.R.id.tags
import edu.utap.rasriram.listman.adapter.ProjectAdapter
import edu.utap.rasriram.listman.model.Project
import edu.utap.rasriram.listman.model.Task
import edu.utap.rasriram.listman.viewmodel.ProjectViewModel
import kotlin.Array as Array1


class ProjectFragment : Fragment(R.layout.fragment_project) {
    private val viewModel: ProjectViewModel by activityViewModels()
    private lateinit var adapter : ProjectAdapter
    private lateinit var projects : MutableLiveData<List<Project>>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View =
            inflater.inflate(R.layout.fragment_project, container, false)

        initRecyclerView(view)
        initFAB(view, inflater)

        projects.observe(viewLifecycleOwner, {
            if(!it.isNullOrEmpty()) {
                view.findViewById<TextView>(R.id.infoTv).visibility = View.INVISIBLE
                adapter.submitList(it)
            }
        })

        return view
    }

    private fun initFAB(view: View, inflater: LayoutInflater) {
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
                viewModel.removeProject(it[pos])
            }


        }

    }
}

