package com.stevechulsdev.bansa.main

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.firestore.DocumentSnapshot
import com.stevechulsdev.bansa.R
import com.stevechulsdev.bansa.etc.AnimationUtils
import com.stevechulsdev.bansa.etc.Constants
import com.stevechulsdev.bansa.etc.LocalPreference
import com.stevechulsdev.bansa.login.view.LoginActivity
import com.stevechulsdev.bansa.main.view.ItemDetailActivity
import com.stevechulsdev.sclog.ScLog
import kotlinx.android.synthetic.main.cell_main.view.*
import org.jetbrains.anko.startActivity
import java.net.URL

class AdapterMainFragment(val mActivity: FragmentActivity, val context: Context, val arrayList: ArrayList<DocumentSnapshot>): RecyclerView.Adapter<AdapterMainFragment.ViewHolder>() {

    var myHeartId = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.cell_main, parent, false))
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position, context)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int, context: Context) {

            Thread(Runnable {
                val imagePath = arrayList[position].get("imagePath").toString()

                Handler(Looper.getMainLooper()).post {

                    // like
                    var myHeartId = ""
                    Glide.with(itemView.iv_like_small)
                        .load(R.drawable.btn_heart_small_off)
                        .into(itemView.iv_like_small)

                    val heartList = (arrayList[position].get("heartList") as ArrayList<String>)
                    for(myId in heartList) {
                        if(myId == LocalPreference.userUid) {
                            myHeartId = myId
                            Glide.with(itemView.iv_like_small)
                                .load(R.drawable.btn_heart_small_on)
                                .into(itemView.iv_like_small)
                        }
                    }

                    val heartCount = heartList.size
                    itemView.tv_like_count.text = "$heartCount"

                    // bookmark
                    var myBookMarkId = ""
                    Glide.with(itemView.iv_bookmark_small)
                        .load(R.drawable.btn_bookmark_small_off)
                        .into(itemView.iv_bookmark_small)

                    val bookmarkList = (arrayList[position].get("bookMarkList") as ArrayList<String>)
                    for(myId in bookmarkList) {
                        if(myId == LocalPreference.userUid) {
                            myBookMarkId = myId
                            Glide.with(itemView.iv_bookmark_small)
                                .load(R.drawable.btn_bookmark_small_on)
                                .into(itemView.iv_bookmark_small)
                        }
                    }

                    val bookmarkCount = bookmarkList.size
                    itemView.tv_bookmark_count.text = "$bookmarkCount"

                    val replyList = arrayList[position].get("replyList")?.let {
                        it as ArrayList<HashMap<String, String>>
                    }

                    val replyCount = replyList?.size?: 0
                    itemView.tv_reply_count.text = "$replyCount"

                    // main image
                    Glide.with(itemView.iv_img)
                        .load(imagePath)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.iv_img)

                    // click main
                    itemView.cl_main_layout.setOnClickListener {
                        if(LocalPreference.isLogin) {
                            val intent = Intent(context, ItemDetailActivity::class.java)
                            intent.putExtra("imagePath", arrayList[position].getString("imagePath"))
                            intent.putExtra("brand", "Tiffany & Co")
                            intent.putExtra("modelName", arrayList[position].getString("name"))
                            intent.putExtra("price", arrayList[position].getString("price"))
                            intent.putExtra("description", arrayList[position].getString("description"))
                            intent.putExtra("url", arrayList[position].getString("url"))
                            intent.putExtra("postingId", arrayList[position].getString("postingId"))

                            if(myHeartId.isNotBlank()) {
                                intent.putExtra("heartUserId", myHeartId)
                            }
                            intent.putExtra("heartCount", heartCount)

                            if(myBookMarkId.isNotBlank()) {
                                intent.putExtra("bookmarkId", myBookMarkId)
                            }
                            intent.putExtra("bookmarkCount", bookmarkCount)

                            intent.putExtra("replyCount", replyCount)

                            mActivity.startActivityForResult(intent, 5656)
                            AnimationUtils().animFadeInFadeOut(mActivity)
                        }
                        else {
                            context.startActivity<LoginActivity>()
                        }
                    }
                }
            }).start()
        }
    }
}