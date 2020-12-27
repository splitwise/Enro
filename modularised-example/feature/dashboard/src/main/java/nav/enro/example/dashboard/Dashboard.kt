package nav.enro.example.dashboard

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import kotlinx.android.synthetic.main.dashboard.*
import nav.enro.annotations.NavigationDestination
import nav.enro.core.close
import nav.enro.core.forward
import nav.enro.example.core.data.SimpleDataRepository
import nav.enro.example.core.navigation.*
import nav.enro.result.registerForNavigationResult
import nav.enro.viewmodel.asTyped
import nav.enro.viewmodel.enroViewModels
import nav.enro.viewmodel.navigationHandle

@NavigationDestination(DashboardKey::class)
class DashboardActivity : AppCompatActivity() {

    private val viewModel by enroViewModels<DashboardViewModel>()

    private val dialog: Dialog by lazy {
        AlertDialog.Builder(this)
            .setTitle("Exit")
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Exit") { _, _ -> viewModel.onCloseAccepted() }
            .setNegativeButton("Cancel") { _, _ -> viewModel.onCloseDismissed() }
            .setOnDismissListener { viewModel.onCloseDismissed() }
            .create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard)

        privateMessagesTitle.setOnClickListener { viewModel.onMyPrivateMessagesSelected() }
        publicMessagesTitle.setOnClickListener { viewModel.onMyPublicMessagesSelected() }
        otherMessagesTitle.setOnClickListener { viewModel.onOtherMessagesSelected() }
        allMessagesTitle.setOnClickListener { viewModel.onAllMessagesSelected() }
        userInfoButton.setOnClickListener { viewModel.onUserInfoSelected() }
        multiStackButton.setOnClickListener { viewModel.onMultiStackSelected() }

        viewModel.observableState.observe(this) {
            subtitle.text = "Welcome back, ${it.userId}"

            privateMessagesAmount.text = it.myPrivateMessageCount.toString()
            publicMessagesAmount.text = it.myPublicMessageCount.toString()
            otherMessagesAmount.text = it.otherPublicMessageCount.toString()
            allMessagesAmount.text = it.allMessageCount.toString()

            if (it.closeRequested && !dialog.isShowing) {
                dialog.show()
            }
        }
    }

    override fun onDestroy() {
        dialog.dismiss()
        super.onDestroy()
    }
}

data class DashboardState(
    val userId: String,
    val closeRequested: Boolean,
    val myPrivateMessageCount: Int,
    val myPublicMessageCount: Int,
    val otherPublicMessageCount: Int
) {
    val allMessageCount: Int get() = myPrivateMessageCount + myPublicMessageCount + otherPublicMessageCount
}

class DashboardViewModel(

) : nav.enro.example.core.base.SingleStateViewModel<DashboardState>() {

    private val repo = SimpleDataRepository()

    private val navigationHandle by navigationHandle().asTyped<DashboardKey>()
    private val key = navigationHandle.key

    private val viewDetail by registerForNavigationResult<Boolean>(navigationHandle) {
        state = state.copy(userId = "${state.userId} FIRST($it)")
    }

    private val viewDetail2 by registerForNavigationResult<Boolean>(navigationHandle) {
        state = state.copy(userId = "${state.userId} WOW($it)")
    }

    init {
        val userId = key.userId
        val data = repo.getList(userId)
        state = DashboardState(
            userId = key.userId,
            closeRequested = false,
            myPrivateMessageCount = data.count { !it.isPublic && it.ownerId == userId },
            myPublicMessageCount = data.count { it.isPublic && it.ownerId == userId },
            otherPublicMessageCount = data.count { it.isPublic && it.ownerId != userId }
        )

//        navigationHandle.onCloseRequested {
//            state = state.copy(closeRequested = true)
//        }
    }

    fun test(boolean: Boolean) {
        Log.e("ASDASD", "ASASDDAS")
    }

    fun onMyPrivateMessagesSelected() {
        viewDetail.open(
            ListKey(
                userId = key.userId,
                filter = ListFilterType.MY_PRIVATE
            )
        )
    }

    fun onMyPublicMessagesSelected() {
        viewDetail.open(
            ListKey(
                userId = key.userId,
                filter = ListFilterType.MY_PUBLIC
            )
        )
    }

    fun onOtherMessagesSelected() {
        viewDetail2.open(
            ListKey(
                userId = key.userId,
                filter = ListFilterType.NOT_MY_PUBLIC
            )
        )
    }

    fun onAllMessagesSelected() {
        navigationHandle.forward(
            MasterDetailKey(
                userId = key.userId,
                filter = ListFilterType.ALL
            )
        )
    }

    fun onUserInfoSelected() {
        navigationHandle.forward(
            UserKey(
                userId = key.userId
            )
        )
    }

    fun onMultiStackSelected() {
        navigationHandle.forward(MultiStackKey())
    }

    fun onCloseAccepted() {
        navigationHandle.close()
    }

    fun onCloseDismissed() {
        state = state.copy(closeRequested = false)
    }

}