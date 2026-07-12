package com.oldpods.app.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.oldpods.app.ui.dashboard.R
import com.oldpods.app.model.CategoryMapping

class CategoryAdapter(
    private val onItemClick: (CategoryMapping.CategoryDef) -> Unit
) : ListAdapter<CategoryMapping.CategoryDef, CategoryAdapter.ViewHolder>(DiffCallback) {

    private var selectedPosition = 0

    object DiffCallback : DiffUtil.ItemCallback<CategoryMapping.CategoryDef>() {
        override fun areItemsTheSame(old: CategoryMapping.CategoryDef, new: CategoryMapping.CategoryDef) = old.id == new.id
        override fun areContentsTheSame(old: CategoryMapping.CategoryDef, new: CategoryMapping.CategoryDef) = old == new
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tv_category_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = getItem(position)
        holder.tvName.text = category.name

        val isSelected = position == selectedPosition
        holder.tvName.setTextColor(
            holder.itemView.context.getColor(
                if (isSelected) android.R.color.holo_green_light
                else android.R.color.black
            )
        )
        holder.tvName.setBackgroundColor(
            holder.itemView.context.getColor(
                if (isSelected) android.R.color.darker_gray
                else android.R.color.transparent
            )
        )

        holder.itemView.setOnClickListener {
            val oldPos = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(oldPos)
            notifyItemChanged(selectedPosition)
            onItemClick(category)
        }
    }
}
