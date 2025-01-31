package dev.enro.core.controller

import android.app.Application
import android.os.Bundle
import androidx.annotation.Keep
import dev.enro.core.*
import dev.enro.core.compose.ComposableDestination
import dev.enro.core.controller.container.ExecutorContainer
import dev.enro.core.controller.container.NavigatorContainer
import dev.enro.core.controller.container.PluginContainer
import dev.enro.core.controller.interceptor.InstructionInterceptorContainer
import dev.enro.core.controller.lifecycle.NavigationLifecycleController
import dev.enro.core.internal.handle.NavigationHandleViewModel
import kotlin.reflect.KClass

class NavigationController internal constructor() {
    internal var isInTest = false

    private val pluginContainer: PluginContainer = PluginContainer()
    private val navigatorContainer: NavigatorContainer = NavigatorContainer()
    private val executorContainer: ExecutorContainer = ExecutorContainer()
    private val interceptorContainer: InstructionInterceptorContainer = InstructionInterceptorContainer()
    private val contextController: NavigationLifecycleController = NavigationLifecycleController(executorContainer, pluginContainer)

    init {
        addComponent(defaultComponent)
    }

    fun addComponent(component: NavigationComponentBuilder) {
        pluginContainer.addPlugins(component.plugins)
        navigatorContainer.addNavigators(component.navigators)
        executorContainer.addOverrides(component.overrides)
        interceptorContainer.addInterceptors(component.interceptors)
    }

    internal fun open(
        navigationContext: NavigationContext<out Any>,
        instruction: NavigationInstruction.Open
    ) {
        val navigator = navigatorForKeyType(instruction.navigationKey::class)
            ?: throw EnroException.MissingNavigator("Attempted to execute $instruction but could not find a valid navigator for the key type on this instruction")

        val executor = executorContainer.executorForOpen(navigationContext, navigator)

        val processedInstruction = interceptorContainer.intercept(
            instruction, executor.context, navigator
        )

        if (processedInstruction.navigationKey::class != navigator.keyType) {
            open(navigationContext, processedInstruction)
            return
        }

        val args = ExecutorArgs(
            executor.context,
            navigator,
            processedInstruction.navigationKey,
            processedInstruction
        )

        executor.executor.preOpened(executor.context)
        executor.executor.open(args)
    }

    internal fun close(
        navigationContext: NavigationContext<out Any>
    ) {
        val executor = executorContainer.executorForClose(navigationContext)
        executor.preClosed(navigationContext)
        executor.close(navigationContext)
    }

    fun navigatorForContextType(
        contextType: KClass<*>
    ): Navigator<*, *>? {
        return navigatorContainer.navigatorForContextType(contextType)
    }

    fun navigatorForKeyType(
        keyType: KClass<out NavigationKey>
    ): Navigator<*, *>? {
        return navigatorContainer.navigatorForKeyType(keyType)
    }

    internal fun executorForOpen(
        fromContext: NavigationContext<*>,
        instruction: NavigationInstruction.Open
    ) = executorContainer.executorForOpen(
        fromContext,
        navigatorForKeyType(instruction.navigationKey::class) ?: throw IllegalStateException()
    )

    internal fun executorForClose(navigationContext: NavigationContext<*>) =
        executorContainer.executorForClose(navigationContext)

    fun addOverride(navigationExecutor: NavigationExecutor<*, *, *>) {
        executorContainer.addTemporaryOverride(navigationExecutor)
    }

    fun removeOverride(navigationExecutor: NavigationExecutor<*, *, *>) {
        executorContainer.removeTemporaryOverride(navigationExecutor)
    }

    fun install(application: Application) {
        navigationControllerBindings[application] = this
        contextController.install(application)
        pluginContainer.onAttached(this)
    }

    @Keep
    // This method is called reflectively by the test module to install/uninstall Enro from test applications
    private fun installForJvmTests() {
        pluginContainer.onAttached(this)
    }

    @Keep
    // This method is called reflectively by the test module to install/uninstall Enro from test applications
    private fun uninstall(application: Application) {
        navigationControllerBindings.remove(application)
        contextController.uninstall(application)
    }

    internal fun onComposeDestinationAttached(
        destination: ComposableDestination,
        savedInstanceState: Bundle?
    ): NavigationHandleViewModel {
        return contextController.onContextCreated(
            ComposeContext(destination),
            savedInstanceState
        )
    }

    internal fun onComposeContextSaved(destination: ComposableDestination, outState: Bundle) {
        contextController.onContextSaved(
            ComposeContext(destination),
            outState
        )
    }

    companion object {
        internal val navigationControllerBindings =
            mutableMapOf<Application, NavigationController>()
    }
}

val Application.navigationController: NavigationController
    get() {
        synchronized(this) {
            if (this is NavigationApplication) return navigationController
            val bound = NavigationController.navigationControllerBindings[this]
            if (bound == null) {
                val navigationController = NavigationController()
                NavigationController.navigationControllerBindings[this] = NavigationController()
                navigationController.install(this)
                return navigationController
            }
            return bound
        }
    }

internal val NavigationController.application: Application
    get() {
        return NavigationController.navigationControllerBindings.entries
            .firstOrNull {
                it.value == this
            }
            ?.key
            ?: throw EnroException.NavigationControllerIsNotAttached("NavigationController is not attached to an Application")
    }