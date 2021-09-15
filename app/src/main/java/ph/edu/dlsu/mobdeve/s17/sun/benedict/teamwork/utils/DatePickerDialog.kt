package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.utils

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.DatePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.util.*

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    lateinit var broadcastManager: LocalBroadcastManager
    private val intentBroadcastNewDate = "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.datepicker_new_date_set"

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it
        this.broadcastManager = LocalBroadcastManager.getInstance(requireContext())
        return DatePickerDialog(requireContext(), this, year, month, day)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        // Do something with the date chosen by the user
        // Broadcast it
        val dateBundle = bundleOf(
            "year" to year,
            "month" to month,
            "day" to day
        )
        val broadcastIntent = Intent(intentBroadcastNewDate)
        broadcastIntent.putExtra("dateBundle", dateBundle)
        this.broadcastManager.sendBroadcast(broadcastIntent)
    }
}