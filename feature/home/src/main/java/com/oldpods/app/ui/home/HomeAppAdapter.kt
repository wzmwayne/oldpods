package com.oldpods.app.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oldpods.app.ui.home.R
import com.oldpods.app.model.AppInfo

class HomeAppAdapter(
    private val onItemClick: (AppInfo) -> Unit
) : ListAdapter<AppInfo, HomeAppAdapter.ViewHolder>(DiffCallback) {

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
        holder.tvInfo.text = app.getExcerptText().take(80)

        if (app.imageUrl != null) {
            Glide.with(holder.itemView.context)
                .load(app.imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.ivIcon)
        }

        holder.itemView.setOnClickListener { onItemClick(app) }
    }
}
