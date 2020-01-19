package com.daijun.asm_plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author Army
 * @version V_1.0.0
 * @date 2020-01-19
 * @description
 */
class AsmPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        def android = project.extensions.getByType(AppExtension)
        android.registerTransform(new AsmTransform())
    }
}
