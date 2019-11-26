package com.example.uav_client.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uav_client.Application.SysApplication
import com.example.uav_client.Data.Main.MainDataInfo
import com.example.uav_client.MainActivity
import com.example.uav_client.MapActivity
import com.example.uav_client.R

class MainPageAdapter(var datalist: List<MainDataInfo>, var mainActivity: MainActivity) : RecyclerView.Adapter<MainPageAdapter.viewholder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_list_item, parent, false)
        return viewholder(view)
    }

    override fun getItemCount(): Int {
        return if (datalist != null) {
            datalist.size
        } else {
            0
        }
    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {
        holder.textView.text = datalist!![position].id
        holder.start.text = "开始时间:"+datalist!![position].Starttime
        holder.end.text = "结束时间:"+datalist!![position].endTiem
        holder.linearlayout.setOnClickListener {
            var intent = Intent(mainActivity, MapActivity::class.java)
            intent.putExtra("id",position)
            mainActivity.startActivity(intent)
        }
    }

    inner class viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView: TextView = itemView.findViewById(R.id.text)
        var start: TextView = itemView.findViewById(R.id.start_time)
        var end: TextView = itemView.findViewById(R.id.end_time)
        var linearlayout: LinearLayout = itemView.findViewById(R.id.data_layout)
    }
}