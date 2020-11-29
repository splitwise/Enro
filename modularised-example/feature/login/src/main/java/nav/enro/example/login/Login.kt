package nav.enro.example.login

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.observe
import nav.enro.annotations.NavigationDestination
import nav.enro.core.forward
import nav.enro.core.replaceRoot
import nav.enro.example.core.data.UserRepository
import nav.enro.example.core.navigation.DashboardKey
import nav.enro.example.core.navigation.LoginErrorKey
import nav.enro.example.core.navigation.LoginKey
import nav.enro.viewmodel.enroViewModels
import nav.enro.viewmodel.navigationHandle

@NavigationDestination(
    key = LoginKey::class,
    allowDefault = true
)
class LoginActivity : AppCompatActivity() {

    private val viewModel by enroViewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        val userInput = findViewById<EditText>(R.id.userInput)
        val loginButton = findViewById<Button>(R.id.loginButton)

        viewModel.observableState.observe(this) {
            if(userInput.text.toString() != it.username) {
                userInput.setTextKeepState(it.username)
            }
        }

        userInput.doOnTextChanged { text, _, _, _ ->
            viewModel.onUserNameUpdated(text?.toString() ?: "")
        }

        loginButton.setOnClickListener {
            viewModel.onLogin()
        }
    }
}

data class LoginState(
    val username: String = ""
)

class LoginViewModel : nav.enro.example.core.base.SingleStateViewModel<LoginState>() {

    private val navigationHandle by navigationHandle<Nothing>()

    private val userRepo = UserRepository.instance

    init {
        UserRepository.instance.activeUser = null
        state = LoginState()
    }

    fun onUserNameUpdated(username: String) {
        state = state.copy(
            username = username
        )
    }

    fun onLogin() {
        val user = userRepo.getUsers().firstOrNull {
            it.equals(state.username, ignoreCase = true)
        }
        when(user) {
            null -> navigationHandle.forward(
                LoginErrorKey(
                    state.username
                )
            )
            else -> {
                UserRepository.instance.activeUser = user
                navigationHandle.replaceRoot(
                    DashboardKey(user)
                )
            }
        }
    }
}