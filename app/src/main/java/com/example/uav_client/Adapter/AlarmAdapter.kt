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
    private var list: MutableList<MutableList<String>> = ArrayList()

    fun setlist(list: MutableList<MutableList<String>>){
        this.list = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        return viewholder(LayoutInflater.from(parent.context).inflate(R.layout.alarm_list, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {

        holder.time.text = list[position][0]
        holder.id.text = list[position][1]
        holder.pl.text = list[position][2]
        holder.lon.text = "经度："+list[position][4]
        holder.lat.text = "纬度："+list[position][5]
//        holder.textView.setText(list[position].latitude.toString())
    }

    inner class viewholder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        var id: TextView = itemview.findViewById(R.id.id)
        var time: TextView = itemview.findViewById(R.id.time)
        var pl: TextView = itemview.findViewById(R.id.pl)
        var lon: TextView = itemview.findViewById(R.id.lon)
        var lat: TextView = itemview.findViewById(R.id.lat)
    }
}