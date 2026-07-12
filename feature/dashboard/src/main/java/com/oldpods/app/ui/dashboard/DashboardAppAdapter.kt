package com.oldpods.app.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oldpods.app.ui.dashboard.R
import com.oldpods.app.model.AppInfo

class DashboardAppAdapter : ListAdapter<AppInfo, DashboardAppAdapter.ViewHolder>(DiffCallback) {

    object DiffCallback : DiffUtil.ItemCallback<AppInfo>() {
        override fun areItemsTheSame(old: AppInfo, new: AppInfo) = old.id == new.id
        override fun areContentsTheSame(old: AppInfo, new: AppInfo) = old == new
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivIcon: ImageView = view.findViewById(R.id.iv_app_icon)
        val tvName: TextView = view.findViewById(R.id.tv_app_name)
        val tvInfo: TextView = view.findViewById(R.id.tv_app_info)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_app_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = getItem(position)
        holder.tvName.text = app.getTitleText()
        holder.tvInfo.text = app.appSize ?: app.getExcerptText().take(60)

        val iconUrl = app.imageUrl
        if (!iconUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(iconUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.ivIcon)
        }
    }
}
