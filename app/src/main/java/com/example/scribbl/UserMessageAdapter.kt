package com.example.scribbl

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserMessageAdapter(private val userMessage: ArrayList<UserMessageModel>) : RecyclerView.Adapter<UserMessageAdapter.UserMessageViewHolder>() {

    class UserMessageViewHolder(view: View): RecyclerView.ViewHolder(view){
        var name: TextView = view.findViewById(R.id.user_message_name)
        var message: TextView = view.findViewById(R.id.user_message_text)
        var infoMessage: TextView = view.findViewById(R.id.info_message)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserMessageViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_message, parent, false)
        return UserMessageViewHolder(itemView)
    }

    override fun getItemCount() = userMessage.size

    override fun onBindViewHolder(holder: UserMessageViewHolder, position: Int) {
        if(userMessage[position].isInfo){
            holder.name.visibility = View.GONE
            holder.message.visibility = View.GONE
            holder.infoMessage.visibility = View.VISIBLE
            holder.infoMessage.text = userMessage[position].message
        }else{
            holder.name.visibility = View.VISIBLE
            holder.message.visibility = View.VISIBLE
            holder.infoMessage.visibility = View.GONE
            holder.name.text = userMessage[position].name
            holder.message.text = userMessage[position].message
        }
    }
}