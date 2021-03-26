package dev.enro.core

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import dev.enro.core.activity.ActivityNavigator
import dev.enro.core.compose.ComposableDestination
import dev.enro.core.compose.ComposeFragmentHost
import dev.enro.core.controller.NavigationController
import dev.enro.core.controller.navigationController
import dev.enro.core.fragment.FragmentNavigator
import dev.enro.core.internal.handle.NavigationHandleViewModel
import dev.enro.core.internal.handle.getNavigationHandleViewModel

sealed class NavigationContext<ContextType : Any>(
    val contextReference: ContextType
) {
    abstract val controller: NavigationController
    abstract val lifecycle: Lifecycle
    abstract val childFragmentManager: FragmentManager
    abstract val arguments: Bundle

    internal open val navigator: Navigator<*, ContextType>? by lazy {
        controller.navigatorForContextType(contextReference::class) as? Navigator<*, ContextType>
    }
}

internal class ActivityContext<ContextType : FragmentActivity>(
    contextReference: ContextType,
) : NavigationContext<ContextType>(contextReference) {
    override val controller get() = contextReference.application.navigationController
    override val lifecycle get() = contextReference.lifecycle
    override val navigator get() = super.navigator as? ActivityNavigator<*, ContextType>
    override val childFragmentManager get() = contextReference.supportFragmentManager
    override val arguments: Bundle by lazy { contextReference.intent.extras ?: Bundle() }
}

internal class FragmentContext<ContextType : Fragment>(
    contextReference: ContextType,
) : NavigationContext<ContextType>(contextReference) {
    override val controller get() = contextReference.requireActivity().application.navigationController
    override val lifecycle get() = contextReference.lifecycle
    override val navigator get() = super.navigator as? FragmentNavigator<*, ContextType>
    override val childFragmentManager get() = contextReference.childFragmentManager
    override val arguments: Bundle by lazy { contextReference.arguments ?: Bundle() }
}

internal class ComposeContext<ContextType : ComposableDestination>(
    contextReference: ContextType
) : NavigationContext<ContextType>(contextReference) {
    override val controller: NavigationController get() = contextReference.activity.application.navigationController
    override val lifecycle: Lifecycle get() = contextReference.lifecycle
    override val childFragmentManager: FragmentManager get() = contextReference.activity.supportFragmentManager
    override val arguments: Bundle by lazy { bundleOf(OPEN_ARG to contextReference.instruction) }
}

val NavigationContext<out Fragment>.fragment get() = contextReference

val NavigationContext<*>.activity: FragmentActivity
    get() = when (contextReference) {
        is FragmentActivity -> contextReference
        is Fragment -> contextReference.requireActivity()
        is ComposableDestination -> contextReference.activity
        else -> throw IllegalStateException()
    }

@Suppress("UNCHECKED_CAST") // Higher level logic dictates this cast will pass
internal val <T : FragmentActivity> T.navigationContext: ActivityContext<T>
    get() = viewModels<NavigationHandleViewModel> { ViewModelProvider.NewInstanceFactory() } .value.navigationContext as ActivityContext<T>

@Suppress("UNCHECKED_CAST") // Higher level logic dictates this cast will pass
internal val <T : Fragment> T.navigationContext: FragmentContext<T>
    get() = viewModels<NavigationHandleViewModel> { ViewModelProvider.NewInstanceFactory() } .value.navigationContext as FragmentContext<T>

fun NavigationContext<*>.rootContext(): NavigationContext<*> {
    var parent = this
    while (true) {
        val currentContext = parent
        parent = parent.parentContext() ?: return currentContext
    }
}

fun NavigationContext<*>.parentContext(): NavigationContext<*>? {
    return when (this) {
        is ActivityContext -> null
        is FragmentContext<out Fragment> ->
            when (val parentFragment = fragment.parentFragment) {
                null -> fragment.requireActivity().navigationContext
                else -> parentFragment.navigationContext
            }
        is ComposeContext<out ComposableDestination> -> contextReference.parentContext
    }
}

fun NavigationContext<*>.leafContext(): NavigationContext<*> {
    if(contextReference is ComposeFragmentHost) {
        return contextReference.rootContainer.context?.leafContext() ?: this
    }
    return when(this) {
        is ActivityContext,
        is FragmentContext -> {
            val primaryNavigationFragment = childFragmentManager.primaryNavigationFragment ?: return this
            primaryNavigationFragment.view ?: return this
            primaryNavigationFragment.navigationContext.leafContext()
        }
        is ComposeContext<*> -> contextReference.childContext?.leafContext() ?: this
    }
}

internal fun NavigationContext<*>.getNavigationHandleViewModel(): NavigationHandleViewModel {
    return when (this) {
        is FragmentContext<out Fragment> -> fragment.getNavigationHandle()
        is ActivityContext<out FragmentActivity> -> activity.getNavigationHandle()
        is ComposeContext<out ComposableDestination> -> contextReference.getNavigationHandleViewModel()
    } as NavigationHandleViewModel
}