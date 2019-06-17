package com.bytedance.chizi.constraint.layout

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main2.*

class Main2Activity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn -> {
                if (group.visibility == View.VISIBLE) {
                    group.visibility = View.INVISIBLE
                    group.updatePreLayout(root)
                } else {
                    group.visibility = View.VISIBLE
                    group.updatePreLayout(root)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        btn.setOnClickListener(this)
    }
}
