package edu.utap.rasriram.listman.adapter

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.utap.rasriram.listman.R
import edu.utap.rasriram.listman.model.Task
import edu.utap.rasriram.listman.viewmodel.ProjectViewModel

class TaskAdapter(private var viewModel: ProjectViewModel) :
    ListAdapter<Task, TaskAdapter.VH>(Diff()) {

    class Diff : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.rowID == newItem.rowID
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.rowID == newItem.rowID && oldItem.tags == newItem.tags && oldItem.title == newItem.title
        }
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var titleET = itemView.findViewById<EditText>(R.id.taskNameET)


        fun bind(item: Task?) {
            if (item != null) {
                //initTag(itemView, item)
                titleET.setText(item.title)

                /*val linearLayout = itemView.findViewById<LinearLayout>(R.id.taskTagsLayout)
                for (t in item.tags) {
                    val tv = createTagBadge(itemView, titleET, item)

                    if(tv.text.toString() != "") {
                        linearLayout.addView(tv, linearLayout.childCount - 1)
                    }
                }*/
            }

        }

        private fun createTagBadge(view: View, editText: EditText, task: Task?): TextView {
            val tv = TextView(view.context)
            tv.text = editText.text.toString()
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(4, 0, 4, 0)
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15F)
            tv.background = ContextCompat.getDrawable(view.context, R.drawable.tag_style)
            tv.layoutParams = params

            val linearLayout = view.findViewById<LinearLayout>(R.id.taskTagsLayout)

            tv.setOnLongClickListener { l ->
                linearLayout.removeView(l)
                task?.let {
                    it.tags.remove(tv.text.toString())
                    viewModel.saveTask(it)
                }
                return@setOnLongClickListener true
            }
            return tv
        }

        private fun initTag(view: View, task: Task?) {
            val tagBadge = view.findViewById<TextView>(R.id.tag_badge)

            tagBadge.setOnClickListener { l ->
                val editText = view.findViewById<EditText>(R.id.taskTagsET)
                editText.visibility = View.VISIBLE
                editText.requestFocus()
                editText.setOnEditorActionListener { textView, i, keyEvent ->

                    if (keyEvent.keyCode == 66) {

                        val tv = createTagBadge(view, editText, task)
                        if (tv.text.toString() != "") {
                            task?.let {
                                it.tags.add(tv.text.toString())
                                viewModel.saveTask(it)
                            }
                            val linearLayout = view.findViewById<LinearLayout>(R.id.taskTagsLayout)
                            linearLayout.addView(tv, linearLayout.childCount - 1)
                            editText.setText("")
                        }


                        return@setOnEditorActionListener true
                    }
                    return@setOnEditorActionListener false
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_task, parent, false)
        return VH(itemView)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(holder.adapterPosition))
    }

}
