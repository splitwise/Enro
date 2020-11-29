package nav.enro.example

import android.app.AlertDialog
import kotlinx.android.parcel.Parcelize
import nav.enro.annotations.NavigationDestination
import nav.enro.core.NavigationInstruction
import nav.enro.core.NavigationKey
import nav.enro.core.context.NavigationContext
import nav.enro.core.context.activity
import nav.enro.core.getNavigationHandle
import nav.enro.core.navigator.SyntheticDestination

@Parcelize
data class SimpleMessage(
    val title: String,
    val message: String,
    val positiveActionInstruction: NavigationInstruction.Open? = null
) : NavigationKey

@NavigationDestination(SimpleMessage::class)
class SimpleMessageDestination : SyntheticDestination<SimpleMessage> {
    override fun process(
        navigationContext: NavigationContext<out Any, out NavigationKey>,
        instruction: NavigationInstruction.Open
    ) {
        val activity = navigationContext.activity
        val key = instruction.navigationKey as SimpleMessage
        AlertDialog.Builder(activity).apply {
            setTitle(key.title)
            setMessage(key.message)
            setNegativeButton("Close") { _, _ -> }

            if(key.positiveActionInstruction != null) {
                setPositiveButton("Launch") {_, _ ->
                    navigationContext.activity
                        .getNavigationHandle<Nothing>()
                        .executeInstruction(key.positiveActionInstruction)
                }
            }

            show()
        }
    }
}