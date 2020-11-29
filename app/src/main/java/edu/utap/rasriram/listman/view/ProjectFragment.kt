package edu.utap.rasriram.listman.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import edu.utap.rasriram.listman.R
import edu.utap.rasriram.listman.adapter.ProjectAdapter
import edu.utap.rasriram.listman.model.Project
import edu.utap.rasriram.listman.viewmodel.ProjectViewModel


class ProjectFragment : Fragment(R.layout.fragment_project) {
    private val viewModel: ProjectViewModel by activityViewModels()
    private lateinit var adapter : ProjectAdapter
    private lateinit var projects : MutableLiveData<List<Project>>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        adapter = ProjectAdapter(viewModel)
        projects = viewModel.observeProjects()
        viewModel.readProjects()

        val view: View =
            inflater.inflate(R.layout.fragment_project, container, false)

        val listView = view.findViewById<RecyclerView>(R.id.list_view)
        listView.adapter = adapter

        projects.observe(viewLifecycleOwner, Observer {
            if(!it.isNullOrEmpty()) {
                view.findViewById<TextView>(R.id.infoTv).visibility = View.INVISIBLE
                adapter.submitList(it)
            }
        })

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)

        fab.setOnClickListener {
            fragmentManager?.let { _ ->
                context?.let { it2 ->

                    val dialog = inflater.inflate(R.layout.project_form, null)

                    val projectTile = dialog.findViewById<EditText>(R.id.projectTitle)

                    MaterialAlertDialogBuilder(
                        it2,
                        R.style.MaterialComponents_MaterialAlertDialog
                    ).setView(dialog)
                        .setPositiveButton(
                            R.string.ok
                        ) { _, _ ->
                            projectTile.text.toString()
                            viewModel.saveProject(Project(title = projectTile.text.toString()))

                        }
                        .setNegativeButton(
                            R.string.cancel
                        ) { _, _ ->

                        }
                        .show()
                }

            }
        }


        return view
    }
}