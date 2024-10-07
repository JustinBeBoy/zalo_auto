package com.example.quick_reply

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AppListAdapter(
    private val appsList: List<AppInfo>
) : RecyclerView.Adapter<AppListAdapter.ViewHolder>(){

    override fun getItemCount(): Int {
        return appsList.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = appsList[position]

        holder.itemName.text = item.appName
        holder.itemImage.setImageDrawable(item.appIcon)
        holder.itemCheck.isChecked = item.isChecked
        holder.itemCheck.setOnCheckedChangeListener { _, isChecked ->
            item.isChecked = isChecked
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val itemName = view.findViewById(R.id.app_name) as TextView
        val itemImage = view.findViewById(R.id.app_icon) as ImageView
        val itemCheck = view.findViewById(R.id.checkbox) as CheckBox
    }
}