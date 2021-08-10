package dev.enro.example

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.enro.annotations.NavigationDestination
import dev.enro.core.*
import dev.enro.example.databinding.FragmentSimpleExampleBinding
import kotlinx.parcelize.Parcelize

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
        var embeddedFragment = childFragmentManager.findFragmentById(R.id.embeddedFragment)
        if (embeddedFragment == null) {
            val key = EmbeddedKey(navigation.key.name, navigation.key.launchedFrom, navigation.key.backstack)
            val instruction = NavigationInstruction.Replace(navigationKey = key)
            embeddedFragment = EmbeddedFragment()
            embeddedFragment.addOpenInstruction(instruction)
            childFragmentManager.beginTransaction()
                .add(R.id.embeddedFragment, embeddedFragment, tag)
                .commit()
        }

        FragmentSimpleExampleBinding.bind(view).apply {
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
}

private fun SimpleExampleKey.getNextDestinationName(): String {
    if(name.length != 1) return "A"
    return (name[0] + 1).toString()
}