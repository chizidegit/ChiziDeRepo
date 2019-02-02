package com.chizi.fresco

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
    }

    private fun initViews() {
        button.setOnClickListener {
            val uri = "https://www.baidu.com/img/bd_logo1.png?where=super"
            FrescoLoader.with(it.context)
                    .load(uri)
                    .into(image)
        }
    }

}
