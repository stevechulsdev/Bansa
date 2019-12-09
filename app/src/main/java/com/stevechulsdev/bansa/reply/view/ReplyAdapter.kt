package com.stevechulsdev.bansa.reply.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stevechulsdev.bansa.R
import com.stevechulsdev.bansa.firebase.ReplyData
import kotlinx.android.synthetic.main.cell_message.view.*

class ReplyAdapter(val replyList: ArrayList<HashMap<String, String>>): RecyclerView.Adapter<ReplyAdapter.ViewHolder>() {

    init {
        replyList.reverse()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cell_message, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return replyList.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            val nickName = replyList[position]["nickName"]
            val message = replyList[position]["message"]
            val timeStamp = replyList[position]["timeStamp"]

            itemView.tv_nickName.text = nickName
            itemView.tv_message.text = message
            itemView.tv_time.text = timeStamp
        }
    }
}