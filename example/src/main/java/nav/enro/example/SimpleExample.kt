package nav.enro.example

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.android.parcel.Parcelize
import nav.enro.annotations.NavigationDestination
import nav.enro.core.*

@Parcelize
data class SimpleExampleKey(
    val name: String,
    val launchedFrom: String,
    val backstack: List<String> = emptyList()
) : NavigationKey

@NavigationDestination(SimpleExampleKey::class)
class SimpleExampleFragment() : Fragment() {

    private val navigation by navigationHandle<SimpleExampleKey>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_simple_example, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val currentDestination =  view.findViewById<TextView>(R.id.currentDestination)
        val launchedFrom =  view.findViewById<TextView>(R.id.launchedFrom)
        val currentStack =  view.findViewById<TextView>(R.id.currentStack)
        val forwardButton =  view.findViewById<View>(R.id.forwardButton)
        val replaceButton =  view.findViewById<View>(R.id.replaceButton)
        val replaceRootButton =  view.findViewById<View>(R.id.replaceRootButton)

        currentDestination.text = navigation.key.name
        launchedFrom.text = navigation.key.launchedFrom
        currentStack.text = (navigation.key.backstack +  navigation.key.name).joinToString(" -> ")

        forwardButton.setOnClickListener {
            val next = SimpleExampleKey(
                name = navigation.key.getNextDestinationName(),
                launchedFrom = navigation.key.name,
                backstack = navigation.key.backstack + navigation.key.name
            )
            navigation.forward(next)
        }

        replaceButton.setOnClickListener {
            val next = SimpleExampleKey(
                name = navigation.key.getNextDestinationName(),
                launchedFrom = navigation.key.name,
                backstack = navigation.key.backstack
            )
            navigation.replace(next)
        }

        replaceRootButton.setOnClickListener {
            val next = SimpleExampleKey(
                name = navigation.key.getNextDestinationName(),
                launchedFrom = navigation.key.name,
                backstack = emptyList()
            )
            navigation.replaceRoot(next)
        }
    }
}

private fun SimpleExampleKey.getNextDestinationName(): String {
    if(name.length != 1) return "A"
    return (name[0] + 1).toString()
}