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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import edu.utap.rasriram.listman.R
import edu.utap.rasriram.listman.adapter.ProjectAdapter
import edu.utap.rasriram.listman.model.Project
import edu.utap.rasriram.listman.viewmodel.ProjectViewModel

class TaskFragment : Fragment(R.layout.task_view) {
    private val viewModel: ProjectViewModel by activityViewModels()
    private lateinit var adapter: ProjectAdapter
    private lateinit var projects: MutableLiveData<List<Project>>
    private var project = Project()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View =
            inflater.inflate(R.layout.task_view, container, false)

        initTag(view)
        return view
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
                        //TODO: Update DB
                        return@setOnLongClickListener true
                    }

                    //TODO: Update DB
                    linearLayout.addView(tv, linearLayout.childCount - 1)
                    editText.text.clear()

                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }
        }

    }
}