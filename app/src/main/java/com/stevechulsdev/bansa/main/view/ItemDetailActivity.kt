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
import com.kakao.kakaolink.v2.KakaoLinkResponse
import com.kakao.kakaolink.v2.KakaoLinkService
import com.kakao.kakaolink.v2.network.KakaoLinkCore
import com.kakao.message.template.*
import com.kakao.network.ErrorResult
import com.kakao.network.callback.ResponseCallback
import com.stevechulsdev.bansa.R
import com.stevechulsdev.bansa.etc.AnimationUtils
import com.stevechulsdev.bansa.etc.Constants
import com.stevechulsdev.bansa.etc.Utils
import com.stevechulsdev.scdisplayutils.ScDisplayUtils
import com.stevechulsdev.sclog.ScLog
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
        Utils.setStatusColor(this, "#ffffff")
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

        iv_back.setOnClickListener {
            onBackPressed()
        }

        ll_share_layout.setOnClickListener {
            val params = FeedTemplate.newBuilder(ContentObject.newBuilder(
                "$mBrand $mModelName",
                mImagePath,
                LinkObject.newBuilder().setWebUrl(mUrl).build())
                .setDescrption(mDescription)
                .build())
                .setSocial(
                    SocialObject.newBuilder()
                        .setLikeCount(10)
                        .setCommentCount(20)
                        .build())
                .addButton(
                    ButtonObject("앱에서 보기",
                    LinkObject.newBuilder()
                        .setMobileWebUrl("")
                        .build()))
                .build()

            KakaoLinkService.getInstance().sendDefault(this, params, object : ResponseCallback<KakaoLinkResponse>() {
                override fun onSuccess(result: KakaoLinkResponse?) {
                    ScLog.e(Constants.IS_DEBUG, result.toString())
                }

                override fun onFailure(errorResult: ErrorResult) {
                    ScLog.e(Constants.IS_DEBUG, errorResult.errorMessage)
                }
            })
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        AnimationUtils().animOutLeftToRight(this)
    }
}
