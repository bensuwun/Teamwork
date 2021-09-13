package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.tasks

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.DocumentReference
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.adapters.TaskAdapter
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao.TaskDAO
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentTasksBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.ParcelableDocumentReference
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Task
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.utils.UserPreferences
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class Tasks : Fragment() {

    lateinit var fragmentBinding: FragmentTasksBinding
    lateinit var taskList: ArrayList<Task>
    lateinit var taskAdapter: TaskAdapter

    private val broadcastReceiver = object: BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            Log.d("Tasks:broadcastReceiver.onReceieve", "Broadcast Intercepted")
            taskList = (intent?.extras!!["taskList"] as Array<Task>).toCollection(ArrayList())

            // Initialize the Adapter & ViewHolder
            taskAdapter = TaskAdapter(taskList, requireContext())
            fragmentBinding.taskRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            fragmentBinding.taskRecyclerView.adapter = taskAdapter

            if(taskList.size > 0) {
                fragmentBinding.emptyTasksGraphic.visibility = View.GONE
                fragmentBinding.taskRecyclerView.visibility = View.VISIBLE
            }

            // Initialize the SetOnClickListener of the Add Task FAB
            fragmentBinding.addTaskFab.setOnClickListener {
                findNavController().navigate(R.id.navigateToNewTask)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize the view binding
        fragmentBinding = FragmentTasksBinding.inflate(inflater, container, false)
        val view = fragmentBinding.root

        // Populate the Context Menu
        val filterMenuItems = listOf("All", "Completed", "Incomplete")
        val filterMenuAdapter = ArrayAdapter(this.requireActivity().applicationContext, R.layout.list_item_task_filter_dropdown, filterMenuItems)
        fragmentBinding.taskFilterSelectorDropdownAutocomplete.setAdapter(filterMenuAdapter)
        fragmentBinding.taskFilterSelectorDropdownAutocomplete.setText(filterMenuItems[0], false)

        // Fetch Task Data
        this.taskList = ArrayList()

        // Register the Broadcast Receiver
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver, IntentFilter("ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.all_user_tasks_loaded"))

        // Load the User's Tasks
        val taskDAO = TaskDAO(requireContext())
        UserPreferences(requireContext()).getLoggedInUser()?.let { taskDAO.getAllUserTasks(it.authUid) }

        return view
    }

    private fun generateDummyData(n: Int): ArrayList<Task> {
        Log.d("Tasks:generateDummyData", "Generating $n dummy tasks.")
        val dummyTasks = ArrayList<Task>()
        for (i in 0..n)
            dummyTasks.add(Task("test_task", "Test Task $i", "Help this task be happy.", "Hi, I am a sad task.", Date(), ArrayList<ParcelableDocumentReference>(),
                ArrayList<String>()
            ))
        return dummyTasks
    }

    override fun onStop() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)
        super.onStop()
    }
}