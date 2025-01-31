package dev.enro.processor

import com.google.auto.service.AutoService
import com.squareup.javapoet.*
import dev.enro.annotations.*
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.AGGREGATING)
@AutoService(Processor::class)
class NavigationComponentProcessor : BaseProcessor() {

    private val components = mutableListOf<Element>()
    private val bindings = mutableListOf<Element>()

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(
            NavigationComponent::class.java.name,
            GeneratedNavigationBinding::class.java.name
        )
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment
    ): Boolean {
        components += roundEnv.getElementsAnnotatedWith(NavigationComponent::class.java)
        bindings += roundEnv.getElementsAnnotatedWith(GeneratedNavigationBinding::class.java)
        if (roundEnv.processingOver()) {
            val generatedModule = generateModule(
                components,
                bindings
            )
            components.forEach { generateComponent(it, generatedModule) }
        }
        return true
    }

    private fun generateComponent(component: Element, generatedModuleName: String?) {
        val destinations = processingEnv.elementUtils
            .getPackageElement(EnroProcessor.GENERATED_PACKAGE)
            .runCatching {
                enclosedElements
            }
            .getOrNull()
            .orEmpty()
            .apply {
                if(isEmpty()) {
                    processingEnv.messager.printMessage(Diagnostic.Kind.WARNING, "Created a NavigationComponent but found no navigation destinations. This can indicate that the dependencies which define the @NavigationDestination annotated classes are not on the compile classpath for this module, or that you have forgotten to apply the enro-processor annotation processor to the modules that define the @NavigationDestination annotated classes.")
                }
            }
            .mapNotNull {
                val annotation = it.getAnnotation(GeneratedNavigationBinding::class.java)
                    ?: return@mapNotNull null

                NavigationDestinationArguments(
                    generatedBinding = it,
                    destination = annotation.destination,
                    navigationKey = annotation.navigationKey
                )
            }

        val modules =  processingEnv.elementUtils
            .getPackageElement(EnroProcessor.GENERATED_PACKAGE)
            .runCatching {
                enclosedElements
            }
            .getOrNull()
            .orEmpty()
            .mapNotNull {
                it.getAnnotation(GeneratedNavigationModule::class.java)
                    ?: return@mapNotNull null
                it.getElementName() + ".class"
            }
            .let {
                if(generatedModuleName != null) {
                    it + "$generatedModuleName.class"
                } else it
            }
            .joinToString(separator = ",\n")

        val generatedName = "${component.simpleName}Navigation"
        val classBuilder = TypeSpec.classBuilder(generatedName)
            .addOriginatingElement(component)
            .addOriginatingElement(
                processingEnv.elementUtils
                    .getPackageElement(EnroProcessor.GENERATED_PACKAGE)
            )
            .apply {
                destinations.forEach {
                    addOriginatingElement(it.generatedBinding)
                }
            }
            .addGeneratedAnnotation()
            .addAnnotation(
                AnnotationSpec.builder(GeneratedNavigationComponent::class.java)
                    .addMember("bindings", "{\n${destinations.joinToString(separator = ",\n") { it.generatedBinding.toString() + ".class" }}\n}")
                    .addMember("modules", "{\n$modules\n}")
                    .build()
            )
            .addModifiers(Modifier.PUBLIC)
            .addSuperinterface(ClassNames.navigationComponentBuilderCommand)
            .addMethod(
                MethodSpec.methodBuilder("execute")
                    .addAnnotation(Override::class.java)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(
                        ParameterSpec
                            .builder(ClassNames.navigationComponentBuilder, "builder")
                            .build()
                    )
                    .apply {
                        destinations.forEach {
                            addStatement(CodeBlock.of("new $1T().execute(builder)", it.generatedBinding))
                        }
                    }
                    .build()
            )
            .build()

        JavaFile
            .builder(
                processingEnv.elementUtils.getPackageOf(component).toString(),
                classBuilder
            )
            .build()
            .writeTo(processingEnv.filer)
    }

    private fun generateModule(componentNames: List<Element>, bindings: List<Element>): String? {
        if(bindings.isEmpty()) return null
        val moduleIdElements = componentNames.ifEmpty { bindings }
        val moduleId = moduleIdElements.fold(0) { acc, it -> acc + it.getElementName().hashCode() }
            .toString()
            .replace("-", "")
            .padStart(10, '0')

        val generatedName = "_dev_enro_processor_ModuleSentinel_$moduleId"
        val classBuilder = TypeSpec.classBuilder(generatedName)
            .apply {
                bindings.forEach {
                    addOriginatingElement(it)
                }
            }
            .addGeneratedAnnotation()
            .addAnnotation(
                AnnotationSpec.builder(GeneratedNavigationModule::class.java)
                    .addMember("bindings", "{\n${bindings.joinToString(separator = ",\n") { it.simpleName.toString() + ".class" }}\n}")
                .build()
            )
            .addModifiers(Modifier.PUBLIC)
            .build()

        JavaFile
            .builder(
                EnroProcessor.GENERATED_PACKAGE,
                classBuilder
            )
            .build()
            .writeTo(processingEnv.filer)

        return "${EnroProcessor.GENERATED_PACKAGE}.$generatedName"
    }
}

internal data class NavigationDestinationArguments(
    val generatedBinding: Element,
    val destination: String,
    val navigationKey: String
)