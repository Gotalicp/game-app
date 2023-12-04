import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.game_app.R
import com.example.game_app.data.LobbyInfo
import com.example.game_app.game.goFish.GoFishViewModel

class DialogFragmnetCreate : DialogFragment() {
    private val goFishViewMode: GoFishViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_fragment_create, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val items = arrayOf(3,4,5,6)
        var selectedItem: Int = 0
        val spinner: Spinner = view.findViewById(R.id.spinner)
        val name = view.findViewById<EditText>(R.id.name)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                selectedItem = items[position]
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        }
        view.findViewById<Button>(R.id.btn_host).setOnClickListener{
            goFishViewMode.createGame(LobbyInfo(name.text.toString(),"","", mutableListOf(),selectedItem,"GoFIsh", 1,""))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
        }
    }
}
