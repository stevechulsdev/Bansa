package com.stevechulsdev.bansa.main

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.stevechulsdev.bansa.R
import com.stevechulsdev.bansa.etc.AnimationUtils
import com.stevechulsdev.bansa.main.view.ItemDetailActivity
import kotlinx.android.synthetic.main.cell_main.view.*
import java.net.URL

class AdapterMainFragment(val mActivity: FragmentActivity, val context: Context, val arrayList: ArrayList<DocumentSnapshot>): RecyclerView.Adapter<AdapterMainFragment.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.cell_main, parent, false))
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position, arrayList, context)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int, arrayList: ArrayList<DocumentSnapshot>, context: Context) {
            Thread(Runnable {
                val url = URL(arrayList[position].get("imagePath").toString())
                val mBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())

                Handler(Looper.getMainLooper()).post {
                    itemView.iv_img.setImageBitmap(mBitmap)

                    itemView.cl_main_layout.setOnClickListener {
                        val intent = Intent(context, ItemDetailActivity::class.java)
                        intent.putExtra("imagePath", arrayList[position].getString("imagePath"))
                        intent.putExtra("brand", "Tiffany")
                        intent.putExtra("modelName", arrayList[position].getString("name"))
                        intent.putExtra("price", arrayList[position].getString("price"))
                        intent.putExtra("description", arrayList[position].getString("description"))
                        intent.putExtra("url", arrayList[position].getString("url"))

                        context.startActivity(intent)
                        AnimationUtils().animFadeInFadeOut(mActivity)
                    }
                }
            }).start()
        }
    }
}