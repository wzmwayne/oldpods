package com.oldpods.app.ui.detail

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oldpods.app.common.HtmlParser
import com.oldpods.app.model.DownloadLink

class AppDetailFragment : Fragment() {

    private lateinit var viewModel: AppDetailViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_app_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[AppDetailViewModel::class.java]

        val appId = arguments?.getString("app_id") ?: return

        val ivBanner = view.findViewById<ImageView>(R.id.iv_banner)
        val ivIcon = view.findViewById<ImageView>(R.id.iv_icon)
        val tvName = view.findViewById<TextView>(R.id.tv_name)
        val tvVersion = view.findViewById<TextView>(R.id.tv_version)
        val tvSize = view.findViewById<TextView>(R.id.tv_size)
        val tvDeveloper = view.findViewById<TextView>(R.id.tv_developer)
        val tvUpdateDate = view.findViewById<TextView>(R.id.tv_update_date)
        val tvDescription = view.findViewById<TextView>(R.id.tv_description)
        val downloadContainer = view.findViewById<LinearLayout>(R.id.download_container)
        val rvScreenshots = view.findViewById<RecyclerView>(R.id.rv_screenshots)
        val webView = view.findViewById<WebView>(R.id.webview_comments)
        val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)

        val screenshotAdapter = ScreenshotAdapter()
        rvScreenshots.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvScreenshots.adapter = screenshotAdapter

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                view?.evaluateJavascript("""
                    (function() {
                        document.querySelectorAll('.pay-box, .pay-boxs, .single-related-posts, .post-author, .post-copyright, .post-navigation, .post-tags, .post-share, .comments-area, #respond, .page-header, .breadcrumb, .sidebar, .toc, .dahengfu').forEach(function(e){e.remove();});
                        document.querySelectorAll('style').forEach(function(e){if(e.textContent.includes('pay-box'))e.remove();});
                        return 'done';
                    })();
                """.trimIndent(), null)
            }
        }
        webView.loadUrl("https://oldpods.cn/?p=${appId}#comments")

        viewModel.appInfo.observe(viewLifecycleOwner) { app ->
            if (app == null) return@observe
            progressBar.visibility = View.GONE

            tvName.text = app.getTitleText()

            val html = app.getContentText()
            val version = HtmlParser.parseCurrentVersion(html)
            val size = HtmlParser.parsePackageSize(html)
            val developer = HtmlParser.parseDeveloper(html)
            val updateDate = HtmlParser.parseUpdateDate(html)

            tvVersion.text = if (version.isNotEmpty()) "版本: $version" else ""
            tvSize.text = if (size.isNotEmpty()) "大小: $size" else ""
            tvDeveloper.text = if (developer.isNotEmpty()) "开发者: $developer" else ""
            tvUpdateDate.text = if (updateDate.isNotEmpty()) "更新: $updateDate" else ""

            if (!app.imageUrl.isNullOrEmpty()) {
                Glide.with(requireContext())
                    .load(app.imageUrl)
                    .centerCrop()
                    .into(ivBanner)
                ivIcon.visibility = View.GONE
            }

            val screenshots = HtmlParser.parseScreenshots(html)
            if (screenshots.isNotEmpty()) {
                screenshotAdapter.submitList(screenshots)
                rvScreenshots.visibility = View.VISIBLE
            } else {
                rvScreenshots.visibility = View.GONE
            }

            tvDescription.text = HtmlParser.parseDescription(html)

            downloadContainer.removeAllViews()
            val links = HtmlParser.parseDownloadLinks(html)
            for (link in links) {
                val btn = Button(requireContext()).apply {
                    text = link.name
                    setOnClickListener {
                        when (link.type) {
                            DownloadLink.LinkType.GOLINK -> {
                                val decoded = HtmlParser.decodeGolink(link.url)
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(decoded))
                                    startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "链接无效: $decoded", Toast.LENGTH_SHORT).show()
                                }
                            }
                            else -> {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link.url))
                                    startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "链接无效", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
                downloadContainer.addView(btn)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.fetchAppDetail(appId)
    }

    class ScreenshotAdapter : RecyclerView.Adapter<ScreenshotAdapter.VH>() {
        private var items = emptyList<String>()

        fun submitList(list: List<String>) {
            items = list
            notifyDataSetChanged()
        }

        class VH(view: View) : RecyclerView.ViewHolder(view) {
            val iv: ImageView = view.findViewById(R.id.iv_screenshot)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_screenshot, parent, false)
            return VH(view)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            Glide.with(holder.itemView.context)
                .load(items[position])
                .into(holder.iv)
        }

        override fun getItemCount() = items.size
    }
}
