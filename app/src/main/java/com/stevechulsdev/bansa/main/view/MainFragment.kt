package com.stevechulsdev.bansa.main.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.DocumentSnapshot
import com.stevechulsdev.bansa.R
import com.stevechulsdev.bansa.etc.Constants
import com.stevechulsdev.bansa.etc.ItemDecoration
import com.stevechulsdev.bansa.firebase.DBManager
import com.stevechulsdev.bansa.main.AdapterMainFragment
import com.stevechulsdev.sclog.ScLog
import kotlinx.android.synthetic.main.fragment_main.view.*

class MainFragment: Fragment() {

    companion object {
        fun newInstance(): MainFragment = MainFragment()
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

        getPostingData(this.context!!, view)
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
                view.recyclerView.adapter = AdapterMainFragment(this.context!!, arrayList)
            }
            .addOnFailureListener { exception ->
                ScLog.e(Constants.IS_DEBUG, "getPostingList error : $exception")
            }
    }
}