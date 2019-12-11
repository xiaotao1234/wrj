package com.example.uav_client.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uav_client.R
import java.util.ArrayList

class LogAdapter: RecyclerView.Adapter<LogAdapter.viewholder>() {
    private var list: MutableList<MutableList<String>> = ArrayList()

    fun setlist(list: MutableList<MutableList<String>>){
        this.list = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        return viewholder(LayoutInflater.from(parent.context).inflate(R.layout.alarm_list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {

        holder.textView.setText(list[position][0])
        holder.username.setText(list[position][1])
        holder.operate.setText(list[position][2])
    }

    inner class viewholder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        var textView: TextView = itemview.findViewById(R.id.alarm_text1)
        var username: TextView = itemview.findViewById(R.id.username)
        var operate: TextView = itemview.findViewById(R.id.operate)
    }
}