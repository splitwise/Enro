package nav.enro.core

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
enum class NavigationDirection : Parcelable {
    FORWARD,
    REPLACE,
    REPLACE_ROOT
}

internal const val OPEN_ARG = "nav.enro.core.OPEN_ARG"
internal const val CONTEXT_ID_ARG = "nav.enro.core.CONTEXT_ID"

sealed class NavigationInstruction {

    @Parcelize
    data class Open(
        val navigationDirection: NavigationDirection,
        val navigationKey: NavigationKey,
        val children: List<NavigationKey> = emptyList(),
        val parentInstruction: Open? = null,
        val animations: NavigationAnimations? = null,
        val additionalData: Bundle = Bundle(),
        val instructionId: String = UUID.randomUUID().toString()
    ) : NavigationInstruction(), Parcelable

    object Close : NavigationInstruction()
}


fun Intent.addOpenInstruction(instruction: NavigationInstruction.Open): Intent {
    putExtra(OPEN_ARG, instruction)
    return this
}

fun Bundle.addOpenInstruction(instruction: NavigationInstruction.Open): Bundle {
    putParcelable(OPEN_ARG, instruction)
    return this
}

fun Fragment.addOpenInstruction(instruction: NavigationInstruction.Open): Fragment {
    arguments = (arguments ?: Bundle()).apply {
        putParcelable(OPEN_ARG, instruction)
    }
    return this
}

fun <T : NavigationKey> Bundle.readOpenInstruction(): NavigationInstruction.Open? {
    return getParcelable(OPEN_ARG)
}