package dev.enro.core

import android.content.res.Resources
import android.os.Parcelable
import dev.enro.core.compose.AbstractComposeFragmentHost
import dev.enro.core.compose.AbstractComposeFragmentHostKey
import dev.enro.core.controller.navigationController
import dev.enro.core.fragment.internal.AbstractSingleFragmentActivity
import dev.enro.core.fragment.internal.AbstractSingleFragmentKey
import dev.enro.core.internal.getAttributeResourceId
import kotlinx.parcelize.Parcelize

sealed class AnimationPair : Parcelable {
    abstract val enter: Int
    abstract val exit: Int

    @Parcelize
    class Resource(
        override val enter: Int,
        override val exit: Int
    ) : AnimationPair()

    @Parcelize
    class Attr(
        override val enter: Int,
        override val exit: Int
    ) : AnimationPair()

    fun asResource(theme: Resources.Theme) = when (this) {
        is Resource -> this
        is Attr -> Resource(
            theme.getAttributeResourceId(enter),
            theme.getAttributeResourceId(exit)
        )
    }
}

object DefaultAnimations {
    val forward = AnimationPair.Attr(
        enter = android.R.attr.activityOpenEnterAnimation,
        exit = android.R.attr.activityOpenExitAnimation
    )

    val replace = AnimationPair.Attr(
        enter = android.R.attr.activityOpenEnterAnimation,
        exit = android.R.attr.activityOpenExitAnimation
    )

    val replaceRoot = AnimationPair.Attr(
        enter = android.R.attr.taskOpenEnterAnimation,
        exit = android.R.attr.taskOpenExitAnimation
    )

    val close = AnimationPair.Attr(
        enter = android.R.attr.activityCloseEnterAnimation,
        exit = android.R.attr.activityCloseExitAnimation
    )

    val none = AnimationPair.Resource(
        enter = 0,
        exit = R.anim.enro_no_op_animation
    )
}

fun animationsFor(
    context: NavigationContext<*>,
    navigationInstruction: NavigationInstruction
): AnimationPair.Resource {
    if (navigationInstruction is NavigationInstruction.Open && navigationInstruction.children.isNotEmpty()) {
        return AnimationPair.Resource(0, 0)
    }

    if (navigationInstruction is NavigationInstruction.Open && context.contextReference is AbstractSingleFragmentActivity) {
        val singleFragmentKey = context.getNavigationHandleViewModel().key as AbstractSingleFragmentKey
        if (navigationInstruction.instructionId == singleFragmentKey.instruction.instructionId) {
            return AnimationPair.Resource(0, 0)
        }
    }

    if (navigationInstruction is NavigationInstruction.Open && context.contextReference is AbstractComposeFragmentHost) {
        val composeHostKey = context.getNavigationHandleViewModel().key as AbstractComposeFragmentHostKey
        if (navigationInstruction.instructionId == composeHostKey.instruction.instructionId) {
            return AnimationPair.Resource(0, 0)
        }
    }

    return when (navigationInstruction) {
        is NavigationInstruction.Open -> animationsForOpen(context, navigationInstruction)
        is NavigationInstruction.Close -> animationsForClose(context)
        is NavigationInstruction.RequestClose -> animationsForClose(context)
    }
}

private fun animationsForOpen(
    context: NavigationContext<*>,
    navigationInstruction: NavigationInstruction.Open
): AnimationPair.Resource {
    val theme = context.activity.theme

    val instructionForAnimation =  when (
        val navigationKey = navigationInstruction.navigationKey
    ) {
        is AbstractComposeFragmentHostKey -> navigationKey.instruction
        else -> navigationInstruction
    }

    val executor = context.activity.application.navigationController.executorForOpen(
        context,
        instructionForAnimation
    )
    return executor.executor.animation(navigationInstruction).asResource(theme)
}

private fun animationsForClose(
    context: NavigationContext<*>
): AnimationPair.Resource {
    val theme = context.activity.theme

    val contextForAnimation = when (context.contextReference) {
        is AbstractComposeFragmentHost -> {
            context.childComposableManager.containers
                .firstOrNull()
                ?.activeContext
                ?: context
        }
        else -> context
    }

    val executor = context.activity.application.navigationController.executorForClose(contextForAnimation)
    return executor.closeAnimation(context).asResource(theme)
}