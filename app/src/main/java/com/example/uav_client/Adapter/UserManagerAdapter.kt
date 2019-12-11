package com.example.uav_client.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uav_client.Data.Common.User
import com.example.uav_client.R

class UserManagerAdapter(var userList:List<User>) : RecyclerView.Adapter<UserManagerAdapter.viewholder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        var view:View = LayoutInflater.from(parent.context).inflate(R.layout.user_item,parent,false)
        return viewholder(view)
    }

    fun setList(userlist:List<User>){
        this.userList = userlist
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {
        holder.textView.text = userList[position].name
        holder.typetextView.text = if(userList[position].type == User.NORMAL_USER) "普通用户" else "管理员"
        holder.relativeLayout.setBackgroundColor(if(userList[position].type == User.NORMAL_USER)
            Color.parseColor("#0B000000") else Color.parseColor("#00000000"))
        holder.userIma.setImageResource(if(userList[position].type == User.NORMAL_USER) R.drawable.normal_user else R.drawable.manager_user)
        holder.relativeLayout.setOnClickListener {
            layoutClickListener!!.callback(position)
        }
    }

    inner class viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView: TextView = itemView.findViewById(R.id.user_name)
        var typetextView:TextView = itemView.findViewById(R.id.user_type)
        var relativeLayout:RelativeLayout = itemView.findViewById(R.id.user_lin)
        var userIma:ImageView = itemView.findViewById(R.id.user_img)
    }

    interface LayoutClickListener{
        fun callback(position:Int)
    }
    private var layoutClickListener:LayoutClickListener?=null
    fun setLayoutClickListener(layoutClickListener: LayoutClickListener){
        this.layoutClickListener = layoutClickListener
    }
}