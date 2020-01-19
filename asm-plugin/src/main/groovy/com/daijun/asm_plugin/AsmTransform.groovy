package com.daijun.asm_plugin

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

/**
 * @author Army* @version V_1.0.0* @date 2020-01-19
 * @description
 */
class AsmTransform extends Transform {

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        def inputs = transformInvocation.inputs
        def outputProvider = transformInvocation.outputProvider
        if (outputProvider != null) {
            outputProvider.deleteAll()
        }
        inputs.each {
            it.directoryInputs.each { directoryInput ->
                handleDirectoryInput(directoryInput, outputProvider)
            }
            it.jarInputs.each { jarInput ->
                handleJarInput(jarInput, outputProvider)
            }
        }
    }

    static def handleDirectoryInput(DirectoryInput directoryInput, TransformOutputProvider outputProvider) {
        if (directoryInput.file.isDirectory()) {
            directoryInput.file.eachFileRecurse {
                if (checkClass(it.name)) {
                    def classReader = new ClassReader(it.bytes)
                    def classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    def classVisitor = new AsmClassVisitor(classWriter)
                    classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)
                    def fos = new FileOutputStream(it.parentFile.absolutePath + File.separator + it.name)
                    fos.write(classWriter.toByteArray())
                    fos.close()
                }
            }
        }
        // 处理完输入文件后，要把输出给下一个任务
        def dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes,
                directoryInput.scopes, Format.DIRECTORY)
        println "directoryInput.file = ${directoryInput.file.absolutePath}, " +
                "dest = ${dest.absolutePath}"
        FileUtils.copyDirectory(directoryInput.file, dest)
    }

    static def handleJarInput(JarInput jarInput, TransformOutputProvider outputProvider) {
        if (jarInput.file.absolutePath.endsWith(".jar")) {
            def jarName = jarInput.name
            def md5Name = DigestUtils.md5(jarInput.file.absolutePath)
            if (jarName.endsWith(".jar")) {
                jarName = jarName.substring(0, jarName.length() - 4)
            }
            def tempFile = new File(jarInput.file.parentFile, "classes_temp.jar")
            if (tempFile.exists()) {
                tempFile.delete()
            }
            def jarOutputStream = new JarOutputStream(new FileOutputStream(tempFile))
            def jarFile = new JarFile(jarInput.file)
            def entries = jarFile.entries()
            while (entries.hasMoreElements()) {
                def jarEntry = entries.nextElement()
                def name = jarEntry.name
                def zipEntry = new ZipEntry(name)
                def inputStream = jarFile.getInputStream(jarEntry)
                if (checkClass(name)) {
                    jarOutputStream.putNextEntry(zipEntry)
                    def classReader = new ClassReader(IOUtils.toByteArray(inputStream))
                    def classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    def classVisitor = new AsmClassVisitor(classWriter)
                    classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)
                    jarOutputStream.write(classWriter.toByteArray())
                } else {
                    jarOutputStream.putNextEntry(zipEntry)
                    jarOutputStream.write(IOUtils.toByteArray(inputStream))
                }
                jarOutputStream.closeEntry()
            }
            jarOutputStream.close()
            jarFile.close()
            def dest = outputProvider.getContentLocation(jarName + md5Name,
                    jarInput.contentTypes, jarInput.scopes, Format.JAR)
            FileUtils.copyFile(tempFile, dest)
            tempFile.delete()
        }
    }

    static def checkClass(String name) {
        return "MainActivity.class" == name
    }

    @Override
    String getName() {
        return "AsmTransform"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_JARS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }
}
