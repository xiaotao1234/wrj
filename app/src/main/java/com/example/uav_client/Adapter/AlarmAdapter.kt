package com.example.uav_client.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.maps.model.LatLng
import com.example.uav_client.R
import java.util.ArrayList

class AlarmAdapter : RecyclerView.Adapter<AlarmAdapter.viewholder>() {
    private val list: MutableList<LatLng> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        return viewholder(LayoutInflater.from(parent.context).inflate(R.layout.alarm_list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {
        holder.textView.setText(list[position].latitude.toString())
        holder.textView.setText(list[position].latitude.toString())
    }

    inner class viewholder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        var textView: TextView = itemview.findViewById(R.id.alarm_text1)
        var textView1: TextView = itemview.findViewById(R.id.alarm_text2)
    }
}