package com.daijun.inject.compiler

import com.daijun.inject.annotation.BindView
import com.daijun.inject.annotation.Method
import com.daijun.inject.annotation.Page
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.tools.Diagnostic

/**
 * @author Army
 * @version V_1.0.0
 * @date 2020-01-11
 * @description
 */
@AutoService(Processor::class)
class BindViewProcesser: AbstractProcessor() {

    private lateinit var elementUtils: Elements
    private lateinit var filer: Filer

    override fun init(processingEnvironment: ProcessingEnvironment?) {
        super.init(processingEnvironment)
        elementUtils = processingEnv.elementUtils
        filer = processingEnv.filer
        log("init")
    }

    override fun process(set: MutableSet<out TypeElement>?, roundEnvironment: RoundEnvironment?): Boolean {
        if (roundEnvironment?.processingOver() == true) {
            return false
        }
        log("start process")
        val targetMap = mutableMapOf<TypeElement, MutableList<FieldViewBinding>>()
        val elements = roundEnvironment?.getElementsAnnotatedWith(BindView::class.java) ?: mutableSetOf()
        for (element in elements) {
            val enclosingElement = element.enclosingElement as TypeElement
            var list = targetMap[enclosingElement]
            if (list == null) {
                list = mutableListOf()
                targetMap[enclosingElement] = list
            }
            val id = element.getAnnotation(BindView::class.java).value
            val simpleName = element.simpleName.toString()
            val typeMirror = element.asType()
            val fieldViewBinding = FieldViewBinding(simpleName, typeMirror, id)
            list.add(fieldViewBinding)
        }
        for (mutableEntry in targetMap) {
            val mutableList = mutableEntry.value
            if (mutableList.isNullOrEmpty()) {
                continue
            }
            val element = mutableEntry.key
            val packageName = getPackageName(element)
            log("packageName = $packageName")
            log("qualifiedName = ${element.qualifiedName}")
            val name = getClassName(packageName, element)
            log("className = $name")
            val className = ClassName.bestGuess(element.qualifiedName.toString())
            val viewBinder = ClassName.bestGuess("com.daijun.injecttool.ViewBinder")
            val typeBuilder = TypeSpec.classBuilder("${name}_ViewBinder")
                .addModifiers(KModifier.PUBLIC)
                .addSuperinterface(viewBinder.parameterizedBy(className))

            val methodParamterName = "activity"
            val funBuilder = FunSpec.builder("bind")
                .addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE)
                .addParameter(methodParamterName, className)

            for (fieldViewBinding in mutableList) {
                val typeName = fieldViewBinding.type.toString()
                val viewType = ClassName.bestGuess(typeName)
                funBuilder.addStatement("$methodParamterName.%L = $methodParamterName.findViewById<%T>(%L)",
                    fieldViewBinding.name, viewType, fieldViewBinding.resId)
            }

            typeBuilder.addFunction(funBuilder.build())

            val file = FileSpec.builder(packageName, "${name}_ViewBinder")
                .addComment("auto create, do not modify")
                .addType(typeBuilder.build())
                .build()

            file.writeTo(filer)
        }


        val elPages =
            roundEnvironment?.getElementsAnnotatedWith(Page::class.java) ?: mutableSetOf()
        for (elPage in elPages) {
            val typeElement = elPage as TypeElement
            log("typeElement.qualifiedName = ${typeElement.qualifiedName}")
        }

        val elMethods =
            roundEnvironment?.getElementsAnnotatedWith(Method::class.java) ?: mutableSetOf()
        for (elMethod in elMethods) {
            val executableElement = elMethod as ExecutableElement
            log("executableElement.simpleName = ${executableElement.simpleName}")
            val enclosingElement = elMethod.enclosingElement
            val typeElement = enclosingElement as TypeElement
            log("方法${elMethod.simpleName}在类${typeElement.qualifiedName}中")
            for (parameter in executableElement.parameters) {
                log("name: ${parameter.simpleName},type = ${parameter.asType().asTypeName()}")
            }
        }
        return false
    }

    private fun getPackageName(element: Element): String {
        return elementUtils.getPackageOf(element).qualifiedName.toString()
    }

    private fun getClassName(packageName: String, element: TypeElement): String {
        val length = packageName.length + 1
        return element.qualifiedName.substring(length).replace("\\.", "\\$")
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.RELEASE_8
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(BindView::class.java.canonicalName)
    }

    private fun log(string: String) {
        processingEnv.messager.printMessage(Diagnostic.Kind.WARNING, string)
    }
}