package com.stevechulsdev.bansa.reply.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stevechulsdev.bansa.R
import com.stevechulsdev.bansa.firebase.ReplyData
import kotlinx.android.synthetic.main.cell_message.view.*

class ReplyAdapter(val replyList: ArrayList<HashMap<String, String>>): RecyclerView.Adapter<ReplyAdapter.ViewHolder>() {

    private val SEC = 60
    private val MIN = 60
    private val HOUR = 24
    private val DAY = 30
    private val MONTH = 12

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
            val timeStamp = formatTimeString(replyList[position]["timeStamp"]?.toLong())

            itemView.tv_nickName.text = nickName
            itemView.tv_message.text = message
            itemView.tv_time.text = timeStamp
        }
    }

    private fun formatTimeString(regTime: Long?): String {
        if(regTime == null) return ""

        val curTime = System.currentTimeMillis()
        var diffTime = (curTime - regTime) / 1000
        var msg: String = ""
        if (diffTime < SEC) {
            msg = "방금 전"
        } else if ((diffTime / SEC).apply { diffTime = this } < MIN) {
            msg = diffTime.toString() + "분 전"
        } else if ((diffTime / MIN).apply { diffTime = this } < HOUR) {
            msg = diffTime.toString() + "시간 전"
        } else if ((diffTime / HOUR).apply { diffTime = this } < DAY) {
            msg = diffTime.toString() + "일 전"
        } else if ((diffTime / DAY).apply { diffTime = this } < MONTH) {
            msg = diffTime.toString() + "달 전"
        } else {
            msg = diffTime.toString() + "년 전"
        }
        return msg
    }
}