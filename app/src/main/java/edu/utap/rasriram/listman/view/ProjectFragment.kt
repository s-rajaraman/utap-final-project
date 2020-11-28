package edu.utap.rasriram.listman.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import edu.utap.rasriram.listman.R
import edu.utap.rasriram.listman.model.Project
import edu.utap.rasriram.listman.viewmodel.ProjectViewModel


class ProjectFragment : Fragment(R.layout.fragment_project) {
    private val viewModel: ProjectViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View =
            inflater.inflate(R.layout.fragment_project, container, false)

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