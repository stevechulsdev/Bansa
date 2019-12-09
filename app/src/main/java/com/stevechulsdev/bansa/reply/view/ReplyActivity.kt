package com.stevechulsdev.bansa.reply.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.stevechulsdev.bansa.R
import com.stevechulsdev.bansa.firebase.DBManager
import kotlinx.android.synthetic.main.activity_item_detail.*
import kotlinx.android.synthetic.main.activity_reply.*
import kotlinx.android.synthetic.main.activity_reply.tv_reply_count
import java.util.*

class ReplyActivity : AppCompatActivity() {

    private val mPostingId: String
        get() = intent.getStringExtra("postingId")?: ""

    private var isChange = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reply)

        DBManager().getReplyList(mPostingId) {
            recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
            recyclerView.adapter = ReplyAdapter(it)

            tv_reply_count.text = "${it.size}"
        }

        btn_back.setOnClickListener {
            finish()
        }

        btn_insert_message.setOnClickListener {
            val inputMessage = edit_input_message.text.toString()

            edit_input_message.text.clear()

            if(inputMessage.isBlank()) return@setOnClickListener

            isChange = true

            DBManager().insertReply(mPostingId, inputMessage) {
                DBManager().getReplyList(mPostingId) {

                    tv_reply_count.text = "${it.size}"

                    recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
                    recyclerView.adapter = ReplyAdapter(it)
                }
            }
        }
    }

    override fun onBackPressed() {
        if(isChange) setResult(4343, Intent().putExtra("replyCount", tv_reply_count.text))
        super.onBackPressed()
    }
}
