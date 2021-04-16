package dev.enro.processor

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeSpec
import javax.annotation.Generated
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind

abstract class BaseProcessor : AbstractProcessor() {

    internal fun Element.getElementName(): String {
        return processingEnv.elementUtils.getPackageOf(this).toString()+"."+this.simpleName
    }

    internal fun Element.extends(className: ClassName): Boolean {
        if (kind != ElementKind.CLASS) return false
        val typeMirror = className.asElement().asType()
        return processingEnv.typeUtils.isAssignable(asType(), typeMirror)
    }

    internal fun Element.implements(className: ClassName): Boolean {
        val typeMirror = processingEnv.typeUtils.erasure(className.asElement().asType())
        return processingEnv.typeUtils.isAssignable(asType(), typeMirror)
    }

    internal fun ClassName.asElement() = processingEnv.elementUtils.getTypeElement(canonicalName())

    internal fun TypeSpec.Builder.addGeneratedAnnotation(): TypeSpec.Builder {
        addAnnotation(
            AnnotationSpec.builder(Generated::class.java)
                .addMember("value", "\"${this@BaseProcessor::class.java.name}\"")
                .build()
        )
        return this
    }
}