package com.daijun.inject.annotation

/**
 * @author Army
 * @version V_1.0.0
 * @date 2020-01-10
 * @description
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class BindView(val value: Int)