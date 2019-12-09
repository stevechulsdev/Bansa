package com.stevechulsdev.bansa.main.view

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
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
import com.stevechulsdev.bansa.etc.LocalPreference
import com.stevechulsdev.bansa.etc.Utils
import com.stevechulsdev.bansa.firebase.DBManager
import com.stevechulsdev.bansa.kakao.KakaoManager
import com.stevechulsdev.bansa.reply.view.ReplyActivity
import com.stevechulsdev.scdisplayutils.ScDisplayUtils
import com.stevechulsdev.sclog.ScLog
import kotlinx.android.synthetic.main.activity_item_detail.*
import kotlinx.android.synthetic.main.cell_main.view.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivity
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

    private val mPostingId: String
        get() = intent.getStringExtra("postingId")?: ""

    private val mHeartUserId: String
        get() = intent.getStringExtra("heartUserId")?: ""

    private val mHeartCount: Int
        get() = intent.getIntExtra("heartCount", 0)

    private val mBookMarkId: String
        get() = intent.getStringExtra("bookmarkId")?: ""

    private val mBookMarkCount: Int
        get() = intent.getIntExtra("bookmarkCount", 0)

    private val mReplyCount: Int
        get() = intent.getIntExtra("replyCount", 0)

    private var isChange = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setStatusColor(this, "#ffffff")
        setContentView(R.layout.activity_item_detail)

        Thread(Runnable {
            Handler(Looper.getMainLooper()).post {

                Glide.with(iv_img)
                    .load(mImagePath)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(iv_img)

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
        tv_like_count.text = "$mHeartCount"
        tv_bookmark_count.text = "$mBookMarkCount"
        tv_reply_count.text = "$mReplyCount"

        if(mHeartUserId.isNotBlank()) {
            iv_heart.setImageResource(R.drawable.btn_heart_big_on)
        }
        else {
            iv_heart.setImageResource(R.drawable.btn_heart_big_off)
        }

        if(mBookMarkId.isNotBlank()) {
            iv_bookMark.setImageResource(R.drawable.btn_bookmark_small_on)
        }
        else {
            iv_bookMark.setImageResource(R.drawable.btn_bookmark_big_off)
        }

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

        ll_bookMark.setOnClickListener {
            DBManager().setBookMark(mPostingId) { isBookMark ->
                isChange = true

                if(isBookMark) {
                    DBManager().deleteBookMark(mPostingId) {
                        DBManager().offBookMark(mPostingId) {
                            iv_bookMark.setImageResource(R.drawable.btn_bookmark_big_off)
                            tv_bookmark_count.text = (tv_bookmark_count.text.toString().toInt() - 1).toString()
                        }
                    }
                }
                else {
                    DBManager().insertBookMark(mPostingId) {
                        DBManager().onBookMark(mPostingId) {
                            iv_bookMark.setImageResource(R.drawable.btn_bookmark_small_on)
                            tv_bookmark_count.text = (tv_bookmark_count.text.toString().toInt() + 1).toString()
                        }
                    }
                }
            }
        }

        ll_like_layout.setOnClickListener {
            DBManager().setLike(mPostingId) { isLike ->
                isChange = true

                if(isLike) {
                    DBManager().offLike(mPostingId) {
                        iv_heart.setImageResource(R.drawable.btn_heart_big_off)
                        tv_like_count.text = (tv_like_count.text.toString().toInt() - 1).toString()
                    }
                }
                else {
                    DBManager().onLike(mPostingId) {
                        iv_heart.setImageResource(R.drawable.btn_heart_big_on)
                        tv_like_count.text = (tv_like_count.text.toString().toInt() + 1).toString()
                    }
                }
            }
        }

        ll_reply_layout.setOnClickListener {
            startActivityForResult(intentFor<ReplyActivity>(
                "postingId" to mPostingId
            ), 3434)
        }

    }

    override fun onBackPressed() {
        if(isChange) setResult(6565)

        super.onBackPressed()
        AnimationUtils().animOutLeftToRight(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {
            3434 -> {
                if(resultCode == 4343) {
                    data?.let {
                        it.extras?.let {
                            isChange = true
                            tv_reply_count.text = it.getString("replyCount")
                        }
                    }
                }
            }
        }
    }
}
