package com.oldpods.app.ui.detail

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oldpods.app.common.HtmlParser
import com.oldpods.app.model.ForumComment

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
        val rvComments = view.findViewById<RecyclerView>(R.id.rv_comments)
        val tvNoComments = view.findViewById<TextView>(R.id.tv_no_comments)
        val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)

        // Screenshots
        val screenshotAdapter = ScreenshotAdapter()
        rvScreenshots.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvScreenshots.adapter = screenshotAdapter

        // Comments
        val commentAdapter = CommentAdapter()
        rvComments.layoutManager = LinearLayoutManager(context)
        rvComments.adapter = commentAdapter
        rvComments.isNestedScrollingEnabled = false

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

            // Download links - already decoded with correct names by HtmlParser
            downloadContainer.removeAllViews()
            val links = HtmlParser.parseDownloadLinks(html)
            for (link in links) {
                val btn = Button(requireContext()).apply {
                    text = link.name
                    setOnClickListener {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link.url))
                            startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "链接无效", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                downloadContainer.addView(btn)
            }
        }

        viewModel.comments.observe(viewLifecycleOwner) { list ->
            if (list.isEmpty()) {
                tvNoComments.visibility = View.VISIBLE
                rvComments.visibility = View.GONE
            } else {
                tvNoComments.visibility = View.GONE
                rvComments.visibility = View.VISIBLE
                commentAdapter.submitList(list)
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

    // ========== Screenshot Adapter ==========

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

    // ========== Comment Adapter ==========

    class CommentAdapter : ListAdapter<ForumComment, CommentAdapter.VH>(DiffCallback) {

        object DiffCallback : DiffUtil.ItemCallback<ForumComment>() {
            override fun areItemsTheSame(old: ForumComment, new: ForumComment) = old.id == new.id
            override fun areContentsTheSame(old: ForumComment, new: ForumComment) = old == new
        }

        class VH(view: View) : RecyclerView.ViewHolder(view) {
            val tvAvatar: TextView = view.findViewById(R.id.tv_avatar)
            val tvAuthor: TextView = view.findViewById(R.id.tv_author)
            val tvDate: TextView = view.findViewById(R.id.tv_date)
            val tvContent: TextView = view.findViewById(R.id.tv_content)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
            return VH(view)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val comment = getItem(position)

            // Avatar: first letter with random color
            val name = comment.authorName ?: "匿"
            val firstChar = name.firstOrNull()?.toString() ?: "匿"
            holder.tvAvatar.text = firstChar
            val colors = intArrayOf(
                Color.parseColor("#4CAF50"), Color.parseColor("#2196F3"),
                Color.parseColor("#FF9800"), Color.parseColor("#E91E63"),
                Color.parseColor("#9C27B0"), Color.parseColor("#00BCD4")
            )
            val color = colors[comment.authorName.hashCode().let { if (it < 0) -it else it } % colors.size]
            val bg = holder.tvAvatar.background
            if (bg is GradientDrawable) {
                bg.setColor(color)
            } else {
                val drawable = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(color)
                }
                holder.tvAvatar.background = drawable
            }

            holder.tvAuthor.text = name
            holder.tvDate.text = formatDate(comment.date)

            val contentHtml = comment.getContentText()
            if (contentHtml.isNotEmpty()) {
                holder.tvContent.text = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    Html.fromHtml(contentHtml, Html.FROM_HTML_MODE_LEGACY)
                } else {
                    @Suppress("DEPRECATION")
                    Html.fromHtml(contentHtml)
                }
                holder.tvContent.visibility = View.VISIBLE
            } else {
                holder.tvContent.visibility = View.GONE
            }
        }

        private fun formatDate(dateStr: String?): String {
            if (dateStr == null) return ""
            return try {
                // "2026-07-12T11:38:00" -> "07-12 11:38"
                val parts = dateStr.split("T")
                if (parts.size >= 2) {
                    val datePart = parts[0] // "2026-07-12"
                    val timePart = parts[1].substringBeforeLast(":") // "11:38"
                    val md = datePart.substring(5) // "07-12"
                    "$md $timePart"
                } else {
                    dateStr
                }
            } catch (e: Exception) {
                dateStr
            }
        }
    }
}
