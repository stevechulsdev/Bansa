package com.stevechulsdev.bansa.main.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stevechulsdev.bansa.databinding.CellMainlistBinding
import com.stevechulsdev.bansa.main.model.Posting
import kotlinx.android.synthetic.main.cell_mainlist.view.*
import java.net.URI
import java.net.URL

class PostingAdapter(val context: Context): RecyclerView.Adapter<PostingAdapter.ViewHodler>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHodler {
        val binding = CellMainlistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHodler(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHodler, position: Int) {
        holder.bind(context)
    }

    override fun getItemCount(): Int {
        return 10
    }

    class ViewHodler(view: View): RecyclerView.ViewHolder(view) {

        var mBitmap: Bitmap? = null
        var handler: Handler? = null

        val ringImageView = view.iv_ring_image
        val ringTagTextView = view.tv_ring_tag
        val ringMoveShopTextView = view.tv_ring_move_shop

        val ringLikeImageButton = view.ib_like
        val ringReplayImageButton = view.ib_replay
        val ringBookMarkImageButton = view.ib_bookmark

        val ringNickNameTextView = view.tv_nickName
        val ringReplayTextView = view.tv_replay

        val ringDescription = view.tv_description

        fun bind(context: Context) {
            handler = object : Handler() {
                override fun handleMessage(msg: Message) {
                    Handler(Looper.getMainLooper()).post {
                        ringImageView.setImageBitmap(mBitmap)
                    }
                }
            }

            Thread(Runnable {
                val url = URL(Posting.imagePath)
                mBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())

                Handler(Looper.getMainLooper()).post {
                    ringImageView.setImageBitmap(mBitmap)
                }
            }).start()

            ringDescription.text = Posting.description
            ringTagTextView.text = "#${Posting.tagList[0]}"
            ringMoveShopTextView.text = "Move to Shop"
            ringMoveShopTextView.setOnClickListener {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Posting.link)))
            }
        }
    }
}