package com.daijun.injecttool

import android.app.Activity

/**
 * @author Army
 * @version V_1.0.0
 * @date 2020-01-10
 * @description
 */
interface ViewBinder<T: Activity> {
    fun bind(activity: T)
}