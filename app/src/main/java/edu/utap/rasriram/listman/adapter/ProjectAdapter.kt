package edu.utap.rasriram.listman.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import edu.utap.rasriram.listman.R
import edu.utap.rasriram.listman.model.Project
import edu.utap.rasriram.listman.viewmodel.ProjectViewModel
import java.util.*

class ProjectAdapter(private var viewModel: ProjectViewModel)
    : ListAdapter<Project, ProjectAdapter.VH>(Diff()) {

        class Diff : DiffUtil.ItemCallback<Project>() {
            override fun areItemsTheSame(oldItem: Project, newItem: Project): Boolean {
                return oldItem.rowID == newItem.rowID
            }

            override fun areContentsTheSame(oldItem: Project, newItem: Project): Boolean {
                return oldItem.rowID == newItem.rowID
                        &&  ((oldItem.tags == null && newItem.tags == null) || oldItem.tags?.equals(newItem.tags) == true)
                        && oldItem.title == newItem.title
            }
        }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var titleTv = itemView.findViewById<TextView>(R.id.tv)

        fun bind(item: Project?) {
            if (item != null) {
                titleTv.text = item.title
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_project, parent, false)
        return VH(itemView)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(holder.adapterPosition))
    }
}