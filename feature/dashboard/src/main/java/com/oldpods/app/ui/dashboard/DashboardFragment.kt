package com.oldpods.app.ui.dashboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oldpods.app.common.AppNavigator
import com.oldpods.app.model.CategoryMapping

class DashboardFragment : Fragment() {

    private lateinit var viewModel: DashboardViewModel
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var appRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvCategoryTitle: TextView

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var appAdapter: DashboardAppAdapter
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
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[DashboardViewModel::class.java]

        categoryRecyclerView = view.findViewById(R.id.rv_categories)
        appRecyclerView = view.findViewById(R.id.rv_apps)
        progressBar = view.findViewById(R.id.progress_bar)
        tvCategoryTitle = view.findViewById(R.id.tv_category_title)

        categoryAdapter = CategoryAdapter { category ->
            tvCategoryTitle.text = category.name
            viewModel.loadCategoryApps(category)
        }
        categoryRecyclerView.layoutManager = LinearLayoutManager(context)
        categoryRecyclerView.adapter = categoryAdapter

        appAdapter = DashboardAppAdapter { appInfo ->
            navigator?.navigateToDetail(appInfo.id.toString())
                ?: Toast.makeText(context, appInfo.getTitleText(), Toast.LENGTH_SHORT).show()
        }
        appRecyclerView.layoutManager = LinearLayoutManager(context)
        appRecyclerView.adapter = appAdapter

        categoryAdapter.submitList(CategoryMapping.categories)

        viewModel.categoryApps.observe(viewLifecycleOwner) { list ->
            appAdapter.submitList(list)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }

        if (CategoryMapping.categories.isNotEmpty()) {
            viewModel.loadCategoryApps(CategoryMapping.categories[0])
            tvCategoryTitle.text = CategoryMapping.categories[0].name
        }
    }
}
