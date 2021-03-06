package com.daijun.inject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.daijun.inject.annotation.BindView
import com.daijun.inject.annotation.Method
import com.daijun.inject.annotation.Page
import com.daijun.injecttool.InjectView
import com.daijun.test.simplePrint

@Page
class MainActivity : AppCompatActivity() {

    @BindView(R.id.text)
    lateinit var text: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        InjectView.bind(this)
        println(text.text)
    }

    @Method
    fun textAnno(age: Int, name: String) {

    }
}
