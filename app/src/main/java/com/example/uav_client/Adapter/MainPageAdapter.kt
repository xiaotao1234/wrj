package com.example.uav_client.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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
        holder.textView.text = datalist!![position].majorString
        holder.linearlayout.setOnClickListener {
            var intent = Intent(mainActivity, MapActivity::class.java)
            mainActivity.startActivity(intent)
        }
    }

    inner class viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView: TextView = itemView.findViewById(R.id.text)
        var linearlayout: LinearLayout = itemView.findViewById(R.id.data_layout)
    }
}