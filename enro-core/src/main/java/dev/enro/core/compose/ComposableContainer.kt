package dev.enro.core.compose

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.enro.core.NavigationContext
import dev.enro.core.NavigationInstruction
import dev.enro.core.NavigationKey
import dev.enro.core.close
import dev.enro.core.internal.handle.NavigationHandleViewModel
import dev.enro.core.internal.handle.getNavigationHandleViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

internal class EnroDestinationStorage : ViewModel() {
    val destinations = mutableMapOf<String, MutableMap<String, ComposableDestinationContextReference>>()

    override fun onCleared() {
        destinations.values
            .flatMap { it.values }
            .forEach { it.viewModelStore.clear() }

        super.onCleared()
    }
}

sealed class EmptyBehavior {
    /**
     * When this container is about to become empty, allow this container to become empty
     */
    object AllowEmpty : EmptyBehavior()

    /**
     * When this container is about to become empty, do not close the NavigationDestination in the
     * container, but instead close the parent NavigationDestination (i.e. the owner of this container)
     */
    object CloseParent : EmptyBehavior()

    /**
     * When this container is about to become empty, execute an action. If the result of the action function is
     * "true", then the action is considered to have consumed the request to become empty, and the container
     * will not close the last navigation destination. When the action function returns "false", the default
     * behaviour will happen, and the container will become empty.
     */
    class Action(
        val onEmpty: () -> Boolean
    ) : EmptyBehavior()
}

@Composable
fun rememberEnroContainerController(
    initialState: List<NavigationInstruction.Open> = emptyList(),
    emptyBehavior: EmptyBehavior = EmptyBehavior.AllowEmpty,
    accept: (NavigationKey) -> Boolean = { true },
): EnroContainerController {
    val viewModelStoreOwner = LocalViewModelStoreOwner.current!!
    val destinationStorage = viewModel<EnroDestinationStorage>()

    val id = rememberSaveable {
        UUID.randomUUID().toString()
    }

    val saveableStateHolder = rememberSaveableStateHolder()
    val controller = remember {
        EnroContainerController(
            id = id,
            navigationHandle = viewModelStoreOwner.getNavigationHandleViewModel(),
            accept = accept,
            destinationStorage = destinationStorage,
            emptyBehavior = emptyBehavior,
            saveableStateHolder = saveableStateHolder
        )
    }

    val savedBackstack = rememberSaveable(
        key = id,
        saver = createEnroContainerBackstackStateSaver {
            controller.backstack.value
        }
    ) {
        EnroContainerBackstackState(
            backstackEntries = initialState.map { EnroContainerBackstackEntry(it, null) },
            exiting = null,
            exitingIndex = -1,
            lastInstruction = initialState.lastOrNull() ?: NavigationInstruction.Close,
            skipAnimations = true
        )
    }

    localComposableManager.registerState(controller)
    return remember {
        controller.setInitialBackstack(savedBackstack)
        controller
    }
}

class EnroContainerController internal constructor(
    val id: String,
    val accept: (NavigationKey) -> Boolean,
    internal val navigationHandle: NavigationHandleViewModel,
    private val destinationStorage: EnroDestinationStorage,
    private val emptyBehavior: EmptyBehavior,
    internal val saveableStateHolder: SaveableStateHolder,
) {
    private lateinit var mutableBackstack: MutableStateFlow<EnroContainerBackstackState>
    val backstack: StateFlow<EnroContainerBackstackState> get() = mutableBackstack

    internal val navigationContext: NavigationContext<*> get() = navigationHandle.navigationContext!!

    private val destinationContexts = destinationStorage.destinations.getOrPut(id) { mutableMapOf() }
    private val currentDestination get() = mutableBackstack.value.backstack
        .mapNotNull { destinationContexts[it.instructionId] }
        .lastOrNull {
            it.lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)
        }

    val activeContext: NavigationContext<*>? get() = currentDestination?.getNavigationHandleViewModel()?.navigationContext

    internal fun setInitialBackstack(initialBackstack: EnroContainerBackstackState) {
        if(::mutableBackstack.isInitialized) throw IllegalStateException()
        mutableBackstack = MutableStateFlow(initialBackstack)
    }

    fun push(instruction: NavigationInstruction.Open) {
        mutableBackstack.value = mutableBackstack.value.push(
            instruction,
            navigationContext.childComposableManager.activeContainer?.id
        )
        navigationContext.childComposableManager.setActiveContainerById(id)
    }

    fun close() {
        currentDestination ?: return
        val closedState = mutableBackstack.value.close()
        if(closedState.backstack.isEmpty()) {
            when(emptyBehavior) {
                EmptyBehavior.AllowEmpty -> {
                    /* If allow empty, pass through to default behavior */
                }
                EmptyBehavior.CloseParent -> {
                    navigationContext.childComposableManager.setActiveContainerById(null)
                    navigationHandle.close()
                    return
                }
                is EmptyBehavior.Action -> {
                    val consumed = emptyBehavior.onEmpty()
                    if (consumed) {
                        return
                    }
                }
            }
        }
        navigationContext.childComposableManager.setActiveContainerById(mutableBackstack.value.backstackEntries.lastOrNull()?.previouslyActiveContainerId)
        mutableBackstack.value = closedState
    }

    internal fun onInstructionDisposed(instruction: NavigationInstruction.Open) {
        if (mutableBackstack.value.exiting == instruction) {
            mutableBackstack.value = mutableBackstack.value.copy(
                exiting = null,
                exitingIndex = -1
            )
        }
    }

    internal fun getDestinationContext(instruction: NavigationInstruction.Open): ComposableDestinationContextReference {
        val destinationContextReference = destinationContexts.getOrPut(instruction.instructionId) {
            val controller = navigationContext.controller
            val composeKey = instruction.navigationKey
            val destination = controller.navigatorForKeyType(composeKey::class)!!.contextType.java
                .newInstance() as ComposableDestination

            return@getOrPut getComposableDestinationContext(
                instruction = instruction,
                destination = destination,
                parentContainer = this
            )
        }
        destinationContextReference.parentContainer = this@EnroContainerController
        return destinationContextReference
    }

    @SuppressLint("ComposableNaming")
    @Composable
    internal fun bindDestination(instruction: NavigationInstruction.Open) {
        DisposableEffect(true) {
            onDispose {
                if(!mutableBackstack.value.backstack.contains(instruction)) {
                    destinationContexts.remove(instruction.instructionId)
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class)
@Composable
fun EnroContainer(
    modifier: Modifier = Modifier,
    controller: EnroContainerController = rememberEnroContainerController(),
) {
    key(controller.id) {
        controller.saveableStateHolder.SaveableStateProvider(controller.id) {
            val backstackState by controller.backstack.collectAsState()

            Box(modifier = modifier) {
                backstackState.renderable.forEach {
                    key(it.instructionId) {
                        controller.getDestinationContext(it).Render()
                        controller.bindDestination(it)
                    }
                }
            }
        }
    }
}

