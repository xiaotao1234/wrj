package com.example.uav_client.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uav_client.R

class MapListAdapter(var datalist: List<String>) : RecyclerView.Adapter<MapListAdapter.viewholder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.map_list_item, parent, false)
        return viewholder(view)
    }

    override fun getItemCount(): Int {
        return if (datalist != null) {
            datalist.size
        } else {
            0
        }
    }

    fun setDataList(datalist: List<String>){
        this.datalist = datalist
    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {
        holder.textView.text = datalist!![position]
    }

    inner class viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView: TextView = itemView.findViewById(R.id.text)
        var linearlayout: LinearLayout = itemView.findViewById(R.id.data_layout)
    }
}