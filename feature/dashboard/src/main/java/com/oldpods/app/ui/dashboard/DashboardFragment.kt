package com.oldpods.app.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oldpods.app.common.PrefsManager
import com.oldpods.app.model.ApiMode
import com.oldpods.app.model.CategoryMapping
import com.oldpods.app.model.AppInfo
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private lateinit var viewModel: DashboardViewModel
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var appRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvCategoryTitle: TextView

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var appAdapter: DashboardAppAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
        viewModel.setApiMode(PrefsManager.getApiMode(requireContext()))

        categoryRecyclerView = view.findViewById(R.id.rv_categories)
        appRecyclerView = view.findViewById(R.id.rv_apps)
        progressBar = view.findViewById(R.id.progress_bar)
        tvCategoryTitle = view.findViewById(R.id.tv_category_title)

        // Category list (left side)
        categoryAdapter = CategoryAdapter { category ->
            tvCategoryTitle.text = category.name
            viewModel.loadCategoryApps(category)
        }
        categoryRecyclerView.layoutManager = LinearLayoutManager(context)
        categoryRecyclerView.adapter = categoryAdapter

        // App list (right side)
        appAdapter = DashboardAppAdapter()
        appRecyclerView.layoutManager = LinearLayoutManager(context)
        appRecyclerView.adapter = appAdapter

        // Load categories
        categoryAdapter.submitList(CategoryMapping.categories)

        // Observe
        viewModel.categoryApps.observe(viewLifecycleOwner) { list ->
            appAdapter.submitList(list)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        // Load first category by default
        if (CategoryMapping.categories.isNotEmpty()) {
            viewModel.loadCategoryApps(CategoryMapping.categories[0])
            tvCategoryTitle.text = CategoryMapping.categories[0].name
        }
    }
}
