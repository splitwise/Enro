package nav.enro.core.context

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import nav.enro.core.NavigationDirection
import nav.enro.core.NavigationInstruction
import nav.enro.core.NavigationKey
import nav.enro.core.controller.NavigationController
import nav.enro.core.controller.navigationController
import nav.enro.core.navigator.ActivityNavigator
import nav.enro.core.navigator.FragmentNavigator
import nav.enro.core.navigator.Navigator
import nav.enro.core.readOpenInstruction

data class ChildContainer(
    @IdRes val containerId: Int,
    val accept: (NavigationKey) -> Boolean
)

sealed class NavigationContext<ContextType : Any, T : NavigationKey>(
    val contextReference: ContextType
) {
    abstract val controller: NavigationController
    abstract val lifecycle: Lifecycle
    abstract val childFragmentManager: FragmentManager
    abstract val id: String
    protected abstract val arguments: Bundle?

    val instruction by lazy {
        arguments?.readOpenInstruction<T>() ?: defaultKey?.let {
            NavigationInstruction.Open(NavigationDirection.FORWARD, it)
        }
    }

    val key: T by lazy {
        instruction?.navigationKey as? T
            ?: defaultKey
            ?: throw IllegalStateException("Navigation Context's bound arguments did not contain a NavigationKey!")
    }

    internal val isEnroContext by lazy {
        val key = instruction?.navigationKey ?: defaultKey
        return@lazy key != null
    }

    internal val defaultKey: T? by lazy {
        controller.navigatorForContextType(contextReference::class)?.defaultKey as? T
    }

    internal open val navigator: Navigator<ContextType, T> by lazy {
        controller.navigatorForKeyType(key::class) as Navigator<ContextType, T>
    }

    internal val pendingKeys: List<NavigationKey> by lazy {
        instruction?.children.orEmpty()
    }

    internal val parentInstruction by lazy {
        instruction?.parentInstruction
    }

    internal var childContainers = listOf<ChildContainer>()
}

internal class ActivityContext<ContextType : FragmentActivity, T : NavigationKey>(
    contextReference: ContextType,
    override val id: String
) : NavigationContext<ContextType, T>(contextReference) {
    override val controller get() = contextReference.application.navigationController
    override val lifecycle get() = contextReference.lifecycle
    override val arguments get() = contextReference.intent.extras
    override val navigator get() = super.navigator as ActivityNavigator<ContextType, T>
    override val childFragmentManager get() = contextReference.supportFragmentManager
}

internal class FragmentContext<ContextType : Fragment, T : NavigationKey>(
    contextReference: ContextType,
    override val id: String
) : NavigationContext<ContextType, T>(contextReference) {
    override val controller get() = contextReference.requireActivity().application.navigationController
    override val lifecycle get() = contextReference.lifecycle
    override val arguments get() = contextReference.arguments
    override val navigator get() = super.navigator as FragmentNavigator<ContextType, T>
    override val childFragmentManager get() = contextReference.childFragmentManager
}


