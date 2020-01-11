package com.daijun.injecttool

import android.app.Activity

/**
 * @author Army
 * @version V_1.0.0
 * @date 2020-01-10
 * @description
 */
class InjectView {
    companion object {
        fun bind(activity: Activity) {
            val clazz = Class.forName(activity.javaClass.name + "_ViewBinder")
            val viewBinder = clazz.newInstance() as ViewBinder<Activity>
            viewBinder.bind(activity)
        }
    }
}