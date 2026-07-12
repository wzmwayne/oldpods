package com.oldpods.app.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.oldpods.app.common.HtmlParser
import com.oldpods.app.model.AppInfo
import kotlinx.coroutines.launch

class AppDetailFragment : Fragment() {

    private lateinit var viewModel: AppDetailViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_app_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[AppDetailViewModel::class.java]

        val appId = arguments?.getString("app_id") ?: return
        viewModel.fetchAppDetail(appId)

        val ivIcon = view.findViewById<ImageView>(R.id.iv_icon)
        val tvName = view.findViewById<TextView>(R.id.tv_name)
        val tvInfo = view.findViewById<TextView>(R.id.tv_info)
        val tvDescription = view.findViewById<TextView>(R.id.tv_description)
        val btnDownload = view.findViewById<Button>(R.id.btn_download)
        val btnDownload2 = view.findViewById<Button>(R.id.btn_download2)
        val webView = view.findViewById<WebView>(R.id.webview_comments)
        val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)

        viewModel.appInfo.observe(viewLifecycleOwner) { app ->
            progressBar.visibility = View.GONE
            tvName.text = app.getTitleText()
            val html = app.getContentText()
            tvInfo.text = HtmlParser.parsePackageSize(html)
            tvDescription.text = HtmlParser.formatHtmlToPlainText(html).take(500)

            val downloadUrl = HtmlParser.parseDownloadUrl(html)
            val downloadUrl2 = HtmlParser.parseDownloadUrl2(html)

            btnDownload.setOnClickListener {
                if (downloadUrl.isNotEmpty()) {
                    // Start download
                }
            }
            btnDownload2.setOnClickListener {
                if (downloadUrl2.isNotEmpty()) {
                    // Start download
                }
            }
            btnDownload.visibility = if (downloadUrl.isNotEmpty()) View.VISIBLE else View.GONE
            btnDownload2.visibility = if (downloadUrl2.isNotEmpty()) View.VISIBLE else View.GONE

            // Load comments in WebView
            webView.settings.javaScriptEnabled = true
            webView.webViewClient = WebViewClient()
            webView.loadUrl("https://oldpods.cn/?p=${app.id}#comments")
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }
    }
}
