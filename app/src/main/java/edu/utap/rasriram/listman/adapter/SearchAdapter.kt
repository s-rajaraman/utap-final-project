package edu.utap.rasriram.listman.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.utap.rasriram.listman.R
import edu.utap.rasriram.listman.model.Project
import edu.utap.rasriram.listman.model.Task
import edu.utap.rasriram.listman.view.TaskFragment
import edu.utap.rasriram.listman.viewmodel.ProjectViewModel

class SearchAdapter(
    private var viewModel: ProjectViewModel,
    private val fragmentManger: FragmentManager
) :
    ListAdapter<Task, SearchAdapter.VH>(Diff()) {

    class Diff : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.rowID == newItem.rowID
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.rowID == newItem.rowID && oldItem.tags == newItem.tags && oldItem.title == newItem.title
        }
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var taskTitle = itemView.findViewById<TextView>(R.id.taskTitle)
        private var projectTitle = itemView.findViewById<TextView>(R.id.projectName)


        fun bind(item: Task?) {
            if (item != null) {
                taskTitle.text = item.title
                viewModel.observeProjects().value?.let {
                    val proj = it.first { p -> p.rowID == item.projectId }

                    projectTitle.text = proj.title

                    itemView.setOnClickListener {
                       it.setBackgroundColor(Color.parseColor("#CCFF0000"))
                        fragmentManger
                            .beginTransaction()
                            .replace(R.id.main_content, TaskFragment(Project(rowID = proj.rowID)))
                            .addToBackStack("project")
                            .commit()
                    }
                }

            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_row, parent, false)
        return VH(itemView)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(holder.adapterPosition))
    }


}
