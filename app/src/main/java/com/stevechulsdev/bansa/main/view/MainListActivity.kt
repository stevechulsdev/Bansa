package com.stevechulsdev.bansa.main.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.stevechulsdev.bansa.BR
import com.stevechulsdev.bansa.R
import com.stevechulsdev.bansa.main.ItemDecoration
import com.stevechulsdev.bansa.main.adapter.PostingAdapter
import com.stevechulsdev.bansa.main.viewmodel.MainListViewModel
import kotlinx.android.synthetic.main.activity_main_list.*

class MainListActivity : AppCompatActivity() {

    private var mainListViewModel = MainListViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_list)

        // Binding viewModel
        var mViewDataBinding = DataBindingUtil.setContentView<ViewDataBinding>(this, R.layout.activity_main_list)
        mViewDataBinding.setVariable(BR.vm, mainListViewModel)

        recyclerview.addItemDecoration(ItemDecoration(this))
        val postingAdpater = PostingAdapter(this)
        recyclerview.adapter = postingAdpater
    }
}
