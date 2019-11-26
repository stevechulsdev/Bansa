package com.stevechulsdev.bansa.main.view

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.stevechulsdev.bansa.R
import com.stevechulsdev.bansa.etc.Constants
import com.stevechulsdev.scdisplayutils.ScDisplayUtils
import kotlinx.android.synthetic.main.activity_item_detail.*
import java.net.URL
import java.text.NumberFormat
import java.util.*

class ItemDetailActivity : AppCompatActivity() {

    private val mBrand: String
        get() = intent.getStringExtra("brand")?: ""

    private val mImagePath: String
        get() = intent.getStringExtra("imagePath")?: ""

    private val mModelName: String
        get() = intent.getStringExtra("modelName")?: ""

    private val mPrice: String
        get() = intent.getStringExtra("price")?: ""

    private val mDescription: String
        get() = intent.getStringExtra("description")?: ""

    private val mUrl: String
        get() = intent.getStringExtra("url")?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)

        Thread(Runnable {
            val url = URL(mImagePath)
            val mBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())

            Handler(Looper.getMainLooper()).post {
                iv_img.setImageBitmap(mBitmap)

                btn_link.setOnClickListener {
                    ScDisplayUtils.showProgressBar(this, false, Color.parseColor("#f447a8"))

                    val ad = InterstitialAd(this)
                    ad?.let {
                        it.adUnitId = Constants.ADMOB_AD_ID
                        it.loadAd(AdRequest.Builder().build())
                        it.adListener = object : AdListener() {
                            override fun onAdFailedToLoad(p0: Int) {
                                ScDisplayUtils.hideProgressBar()
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(mUrl)))
                            }

                            override fun onAdClosed() {
                                ScDisplayUtils.hideProgressBar()
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(mUrl)))
                            }

                            override fun onAdLoaded() {
                                ScDisplayUtils.hideProgressBar()
                                it.show()
                            }
                        }
                    }
                }
            }
        }).start()

        tv_brand.text = mBrand
        tv_model.text = mModelName
        tv_price.text = NumberFormat.getCurrencyInstance(Locale.KOREA).format(mPrice.toLong())
        tv_contents.text = mDescription
    }
}
