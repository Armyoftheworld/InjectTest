package com.daijun.inject.compiler

import javax.lang.model.type.TypeMirror

/**
 * @author Army
 * @version V_1.0.0
 * @date 2020-01-11
 * @description
 *
 *  @BindView(R.id.textview)
 *  private lateinit var textView: TextView
 *
 *  name  -> textView
 *  type  -> TextView
 *  resId -> R.id.textview
 */
data class FieldViewBinding(val name: String, val type: TypeMirror, val resId: Int)