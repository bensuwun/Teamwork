package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.tasks

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.adapters.TaskAdapter
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao.TaskDAO
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentNewSubtaskBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentNewTaskBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.ParcelableDocumentReference
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Task
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.TimePickerFragment
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.utils.DatePickerFragment
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.utils.UserPreferences
import java.util.*

class AddSubtask: Fragment() {

    lateinit var fragmentBinding: FragmentNewSubtaskBinding


    private val broadcastReceiver = object: BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            Log.d("AddTask:broadcastReceiver.onReceieve", "Broadcast Intercepted")

            when(intent?.action) {
                "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.timepicker_new_time_set" -> {
                    // Set the time
                    val receivedBundle = intent.getBundleExtra("timeBundle")!!
                    minuteSet = receivedBundle.getInt("minute")
                    hourSet = receivedBundle.getInt("hour")

                    // Update the view
                    fragmentBinding.newTaskDueTimeSet.setText("${hourSet.toString().padStart(2, '0')}:${minuteSet.toString().padStart(2, '0')}")
                }
                "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.datepicker_new_date_set" -> {
                    // Set the date
                    val receivedBundle = intent.getBundleExtra("dateBundle")!!
                    daySet = receivedBundle.getInt("day")
                    monthSet = receivedBundle.getInt("month")
                    yearSet = receivedBundle.getInt("year")

                    // Update the view
                    fragmentBinding.newTaskDueDateSet.setText("${(1 + monthSet).toString().padStart(2, '0')}/${daySet.toString().padStart(2, '0')}/${yearSet.toString().padStart(4, '0')}")
                }
                TaskDAO.CREATE_SUBTASK_SUCCESS_INTENT -> {
                    // Show a Toast
                    Toast.makeText(requireContext(), "Subtask Created!", Toast.LENGTH_LONG).show()
                    parentFragmentManager.popBackStackImmediate()
                }
                TaskDAO.CREATE_SUBTASK_FAILURE_INTENT -> {
                    Toast.makeText(requireContext(), "Failed to create subtask.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    var minuteSet: Int = 0
    var hourSet: Int = 0
    var daySet: Int = 0
    var monthSet: Int = 0
    var yearSet: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        // Initialize the View
        this.fragmentBinding = FragmentNewSubtaskBinding.inflate(inflater, container, false)
        val v = fragmentBinding.root

        // Register broadcast receiver
        val intentFilter = IntentFilter()
        intentFilter.addAction("ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.timepicker_new_time_set")
        intentFilter.addAction("ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.datepicker_new_date_set")
        intentFilter.addAction("ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.created_user_task")
        intentFilter.addAction("ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.create_user_task_failed")
        intentFilter.addAction(TaskDAO.CREATE_SUBTASK_FAILURE_INTENT)
        intentFilter.addAction(TaskDAO.CREATE_SUBTASK_SUCCESS_INTENT)
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver, intentFilter)

        // Initialize OnClickListeners
        this.fragmentBinding.btnNewTaskSetTime.setOnClickListener {
            TimePickerFragment().show(this.childFragmentManager, "timePicker")
        }
        this.fragmentBinding.btnNewTaskSetDate.setOnClickListener {
            val newFragment = DatePickerFragment()
            newFragment.show(this.childFragmentManager, "datePicker")
        }
        this.fragmentBinding.btnCreateNewTask.setOnClickListener {
            // Ensure that there are no null fields
            if(
                fragmentBinding.etTaskName.text.toString().isEmpty() ||
                fragmentBinding.etTaskAbout.text.toString().isEmpty() ||
                fragmentBinding.etTaskDescription.text.toString().isEmpty() ||
                yearSet == 0
            ) {
                Toast.makeText(requireContext(), "One or more fields are blank.", Toast.LENGTH_LONG).show()
            }
            else {
                // Create an instance of a task via constructor
                val newTask = Task(
                    fragmentBinding.etTaskName.text.toString(),
                    fragmentBinding.etTaskDescription.text.toString(),
                    false,
                    fragmentBinding.etTaskAbout.text.toString(),
                    Date(yearSet, monthSet, daySet, hourSet, minuteSet),
                    true,
                    ArrayList<String>()
                )

                // Call the DAO
                val parentTask = arguments?.getParcelable<Task>("taskObject")!!
                val taskDAO = TaskDAO(requireContext())
                taskDAO.document = newTask
                UserPreferences(requireContext()).getLoggedInUser()?.let { taskDAO.createSubtask(it.authUid, parentTask.taskId) }
            }
        }

        return v
    }

    override fun onStop() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)
        super.onStop()
    }
}