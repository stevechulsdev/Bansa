package com.stevechulsdev.bansa.main.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.stevechulsdev.bansa.R
import com.stevechulsdev.bansa.etc.Constants
import com.stevechulsdev.bansa.etc.ItemDecoration
import com.stevechulsdev.bansa.etc.LocalPreference
import com.stevechulsdev.bansa.firebase.DBManager
import com.stevechulsdev.bansa.login.view.LoginActivity
import com.stevechulsdev.bansa.main.AdapterMainFragment
import com.stevechulsdev.sclog.ScLog
import kotlinx.android.synthetic.main.fragment_main.view.*
import org.jetbrains.anko.startActivity

class JewelryFragment: Fragment() {

    private var xScroll = 0

    companion object {
        fun newInstance(): JewelryFragment = JewelryFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        getPostingData(this.context!!, view)
        getBookMarkList(view)
    }

    private fun getBookMarkList(view: View) {
        DBManager().getUserBookMarkData { bookMarkList ->
            var dataList = ArrayList<DocumentSnapshot>()

            for(postingId in bookMarkList) {
                DBManager().db.collection("PostingList")
                    .document(postingId)
                    .get()
                    .addOnSuccessListener {
                        dataList.add(it)

                        if(dataList.size == bookMarkList.size) {
                            view.recyclerView.addItemDecoration(ItemDecoration(this.context!!))
                            view.recyclerView.adapter = AdapterMainFragment(activity!!, this.context!!, dataList)
                            view.recyclerView.setHasFixedSize(true)
                        }
                    }
                    .addOnFailureListener {
                        it.printStackTrace()
                    }
            }
        }
    }

    private fun getPostingData(context: Context, view: View) {
        DBManager().db.collection("PostingList")
            .get()
            .addOnSuccessListener {
                val arrayList = ArrayList<DocumentSnapshot>()

                for (postingData in it.documents) {
                    arrayList.add(postingData)
                }

                view.recyclerView.addItemDecoration(ItemDecoration(this.context!!))
                view.recyclerView.adapter = AdapterMainFragment(activity!!, this.context!!, arrayList)
                view.recyclerView.setHasFixedSize(true)
                view.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        xScroll += dy

                        if(xScroll >= 2000) {
                            if(!LocalPreference.isLogin) {
                                context.startActivity<LoginActivity>()
                                view.recyclerView.smoothScrollToPosition(0)
                            }
                        }
                    }
                })
            }
            .addOnFailureListener { exception ->
                ScLog.e(Constants.IS_DEBUG, "getPostingList error : $exception")
            }
    }
}