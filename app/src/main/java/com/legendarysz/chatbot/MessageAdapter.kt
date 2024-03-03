package com.legendarysz.chatbot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView

//MessageAdapter.kt
class MessageAdapter(private val list: List<Message>) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val leftConstraintLayout: ConstraintLayout = itemView.findViewById(R.id.left_chat_view)
        val rightConstraintLayout: ConstraintLayout = itemView.findViewById(R.id.right_chat_view)
        val leftTv: TextView = itemView.findViewById(R.id.left_chat_text_view)
        val rightTv: TextView = itemView.findViewById(R.id.right_chat_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = list[position]
        if (message.sentBy == Message.SENT_BY_ME) {
            holder.leftConstraintLayout.isVisible = false
            holder.rightConstraintLayout.isVisible = true
            holder.rightTv.text = message.message
        } else {
            holder.rightConstraintLayout.isVisible = false
            holder.leftConstraintLayout.isVisible = true
            holder.leftTv.text = message.message
        }
    }

}