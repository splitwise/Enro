package nav.enro.example

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import kotlinx.android.parcel.Parcelize
import nav.enro.annotations.NavigationDestination
import nav.enro.core.*

@Parcelize
class ExampleDialogKey(val number: Int = 1) : NavigationKey

@NavigationDestination(ExampleDialogKey::class)
class ExampleDialogFragment : DialogFragment() {

    private val navigation by navigationHandle<ExampleDialogKey>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_example_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val exampleDialogNumber = view.findViewById<TextView>(R.id.exampleDialogNumber)
        val exampleDialogForward = view.findViewById<View>(R.id.exampleDialogForward)
        val exampleDialogReplace = view.findViewById<View>(R.id.exampleDialogReplace)
        val exampleDialogClose = view.findViewById<View>(R.id.exampleDialogClose)

        exampleDialogNumber.text = navigation.key.number.toString()

        exampleDialogForward.setOnClickListener {
            navigation.forward(ExampleDialogKey(navigation.key.number + 1))
        }

        exampleDialogReplace.setOnClickListener {
            navigation.replace(ResultExampleKey())
        }

        exampleDialogClose.setOnClickListener {
            navigation.close()
        }
    }
}