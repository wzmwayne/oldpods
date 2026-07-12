package com.oldpods.app.ui.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oldpods.app.common.AppNavigator
import com.oldpods.app.common.PrefsManager
import com.oldpods.app.ui.home.HomeAppAdapter

class SearchFragment : Fragment() {

    private lateinit var viewModel: SearchViewModel
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var historyContainer: LinearLayout
    private lateinit var tvHistoryTitle: TextView
    private lateinit var tvClearHistory: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var adapter: HomeAppAdapter
    private var navigator: AppNavigator? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AppNavigator) navigator = context
    }

    override fun onDetach() {
        super.onDetach()
        navigator = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[SearchViewModel::class.java]

        searchView = view.findViewById(R.id.search_view)
        recyclerView = view.findViewById(R.id.rv_results)
        historyContainer = view.findViewById(R.id.history_container)
        tvHistoryTitle = view.findViewById(R.id.tv_history_title)
        tvClearHistory = view.findViewById(R.id.tv_clear_history)
        progressBar = view.findViewById(R.id.progress_bar)
        tvEmpty = view.findViewById(R.id.tv_empty)

        adapter = HomeAppAdapter { appInfo ->
            navigator?.navigateToDetail(appInfo.id.toString())
                ?: Toast.makeText(context, appInfo.getTitleText(), Toast.LENGTH_SHORT).show()
        }
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        loadHistory()

        val initialQuery = arguments?.getString("search_query")
        if (!initialQuery.isNullOrEmpty()) {
            searchView.setQuery(initialQuery, false)
            performSearch(initialQuery)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    PrefsManager.addSearchHistory(requireContext(), it)
                    performSearch(it)
                }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean = false
        })

        tvClearHistory.setOnClickListener {
            PrefsManager.clearSearchHistory(requireContext())
            historyContainer.visibility = View.GONE
        }

        viewModel.searchResults.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            tvEmpty.visibility = if (list.isEmpty() && viewModel.isLoading.value != true) View.VISIBLE else View.GONE
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }
    }

    private fun performSearch(query: String) {
        historyContainer.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        viewModel.search(query)
    }

    private fun loadHistory() {
        val history = PrefsManager.getSearchHistory(requireContext())
        if (history.isEmpty()) {
            historyContainer.visibility = View.GONE
            return
        }
        historyContainer.removeAllViews()
        tvHistoryTitle.visibility = View.VISIBLE
        tvClearHistory.visibility = View.VISIBLE

        for (item in history) {
            val tv = TextView(requireContext()).apply {
                text = item
                textSize = 14f
                setPadding(24, 16, 24, 16)
                setBackgroundResource(android.R.drawable.btn_dropdown)
                setOnClickListener {
                    searchView.setQuery(item, false)
                    performSearch(item)
                }
            }
            historyContainer.addView(tv)
        }
        historyContainer.visibility = View.VISIBLE
    }
}
