package edu.utap.rasriram.listman.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.utap.rasriram.listman.R
import edu.utap.rasriram.listman.adapter.SearchAdapter
import edu.utap.rasriram.listman.viewmodel.ProjectViewModel

class SearchFragment : Fragment(R.layout.fragment_project) {
    private val viewModel: ProjectViewModel by activityViewModels()
    private lateinit var adapter: SearchAdapter;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View =
            inflater.inflate(R.layout.search_view, container, false)

        adapter = SearchAdapter(viewModel, parentFragmentManager)

        val search = view.findViewById<EditText>(R.id.searchTerm)


        search.addTextChangedListener {
            viewModel.updateSearch(search.text.toString())
        }

        viewModel.observeSearchResults().observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
                adapter.notifyDataSetChanged()
            }
        })

        val rv = view.findViewById<RecyclerView>(R.id.searchView)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(context)
        val itemDecor = DividerItemDecoration(rv.context, LinearLayoutManager.VERTICAL)
        itemDecor.setDrawable(ContextCompat.getDrawable(rv.context, (R.drawable.divider))!!)
        rv.addItemDecoration(itemDecor)

        return view

    }
}