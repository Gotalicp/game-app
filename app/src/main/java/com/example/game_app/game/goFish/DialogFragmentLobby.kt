import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.game_app.R
import com.example.game_app.data.LobbyInfo
import com.example.game_app.game.goFish.GoFishViewModel

class DialogFragmentLobby : DialogFragment() {
    private val goFishViewMode: GoFishViewModel by activityViewModels()

    companion object {
        private const val ARG_LOBBY_INFO = "lobbyInfo"
        fun newInstance(lobbyInfo: LobbyInfo): DialogFragmentLobby {
            val args = Bundle()
            args.putSerializable(ARG_LOBBY_INFO, lobbyInfo)

            val fragment = DialogFragmentLobby()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_fragment_lobby, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val lobbyInfo = arguments?.getSerializable(ARG_LOBBY_INFO,LobbyInfo::class.java)
        view.findViewById<TextView>(R.id.lobbyName).text = lobbyInfo!!.lobbyName
        view.findViewById<Button>(R.id.btn_start).setOnClickListener {
            goFishViewMode.createGame(lobbyInfo)
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
        }
    }
}
