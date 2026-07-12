package com.oldpods.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.oldpods.app.common.PrefsManager

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var tvLoadMore: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var tvRandom: TextView

    private lateinit var adapter: HomeAppAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        searchView = view.findViewById(R.id.search_view)
        recyclerView = view.findViewById(R.id.recycler_view)
        swipeRefresh = view.findViewById(R.id.swipe_refresh)
        progressBar = view.findViewById(R.id.progress_bar)
        tvLoadMore = view.findViewById(R.id.progress_load_more)
        tvEmpty = view.findViewById(R.id.tv_empty)
        tvRandom = view.findViewById(R.id.tv_random)

        adapter = HomeAppAdapter { appInfo ->
            val bundle = Bundle().apply { putString("app_id", appInfo.id.toString()) }
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(rv, dx, dy)
                val lm = rv.layoutManager as LinearLayoutManager
                if (dy > 0 && lm.findLastVisibleItemPosition() >= lm.itemCount - 3) {
                    viewModel.loadMore()
                }
            }
        })

        swipeRefresh.setOnRefreshListener {
            viewModel.fetchAppList(1)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.search(it) }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean = false
        })

        tvRandom.setOnClickListener { viewModel.loadRandomPost() }

        viewModel.setApiMode(PrefsManager.getApiMode(requireContext()))

        viewModel.appList.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            tvEmpty.visibility = if (list.isEmpty() && viewModel.isLoading.value != true) View.VISIBLE else View.GONE
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            progressBar.visibility = if (loading && viewModel.appList.value.isNullOrEmpty()) View.VISIBLE else View.GONE
        }
        viewModel.isLoadingMore.observe(viewLifecycleOwner) { loading ->
            tvLoadMore.visibility = if (loading) View.VISIBLE else View.GONE
        }
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                tvEmpty.text = it
                tvEmpty.visibility = View.VISIBLE
            }
        }

        if (viewModel.appList.value.isNullOrEmpty()) {
            viewModel.fetchAppList(1)
        }
    }
}
