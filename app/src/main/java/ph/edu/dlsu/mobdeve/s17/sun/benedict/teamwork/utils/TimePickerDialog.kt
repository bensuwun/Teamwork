package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.util.*

class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    lateinit var broadcastManager: LocalBroadcastManager
    private val intentBroadcastNewTime = "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.timepicker_new_time_set"

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        // Create a new instance of TimePickerDialog and return it
        this.broadcastManager = LocalBroadcastManager.getInstance(requireContext())
        return TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        // Do something with the time chosen by the user
        // Broadcast it
        val timeBundle = bundleOf(
            "hour" to hourOfDay,
            "minute" to minute
        )
        val broadcastIntent = Intent(intentBroadcastNewTime)
        broadcastIntent.putExtra("timeBundle", timeBundle)
        this.broadcastManager.sendBroadcast(broadcastIntent)
    }
}