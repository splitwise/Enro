package dev.enro.core.synthetic

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import dev.enro.core.NavigationContext
import dev.enro.core.NavigationInstruction
import dev.enro.core.NavigationKey
import dev.enro.core.getNavigationHandle
import dev.enro.core.result.EnroResult

abstract class SyntheticDestination<T : NavigationKey> {

    private var _navigationContext: NavigationContext<out Any>? = null
    val navigationContext get() = _navigationContext!!

    lateinit var key: T
        internal set

    lateinit var instruction: NavigationInstruction.Open
        internal set

    internal fun bind(
        navigationContext: NavigationContext<out Any>,
        instruction: NavigationInstruction.Open
    ) {
        this._navigationContext = navigationContext
        this.key = instruction.navigationKey as T
        this.instruction = instruction

        navigationContext.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if(event == Lifecycle.Event.ON_DESTROY) {
                    navigationContext.lifecycle.removeObserver(this)
                    _navigationContext = null
                }
            }
        })
    }

    abstract fun process()
}