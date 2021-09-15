package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.tasks

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.adapters.TaskAdapter
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao.GuildDAO
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao.GuildMemberDAO
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao.TaskDAO
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentTasksBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentViewTaskBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Task
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.utils.UserPreferences
import java.util.*

class SpecificTaskView: Fragment() {

    lateinit var fragmentBinding: FragmentViewTaskBinding

    lateinit var task: Task

    private val broadcastReceiver = object: BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            when(intent?.action) {
                "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.delete_user_task" -> {
                    // Show deleted toast then pop backstack
                    Toast.makeText(requireContext(), "Task has been deleted.", Toast.LENGTH_LONG).show()
                    parentFragmentManager.popBackStackImmediate()
                }
                "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.delete_user_task_failed" -> {
                    // Show failed to delete message
                    Toast.makeText(requireContext(), "Failed to delete task.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize Options Menu
        setHasOptionsMenu(true)

        // Initialize the view binding
        fragmentBinding = FragmentViewTaskBinding.inflate(inflater, container, false)
        val view = fragmentBinding.root

        // Get the bundled data
        this.task = arguments?.getParcelable<Task>("taskObject")!!

        // Apply to views
        fragmentBinding.taskViewAbout.setText(task.about)
        fragmentBinding.taskViewDesc.setText(task.description)
        fragmentBinding.taskCheckboxIsDone.isChecked = task.isCompleted
        fragmentBinding.viewTaskDueDateTime.setText("${this.task.dueDate.toString().subSequence(0, 19)}")
        (activity as AppCompatActivity).supportActionBar?.title = task.name

        // Check if the task is overdue
        if(task.dueDate.before(Date())) {
            // Set colors to red
            fragmentBinding.viewTaskDueDateTime.setTextColor(requireContext().resources.getColor(R.color.warning_red))
        }

        // Register the Broadcast Receiver
        val intentFilter = IntentFilter()
        intentFilter.addAction("ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.delete_user_task")
        intentFilter.addAction("ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.delete_user_task_failed")
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver, intentFilter)

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.view_task_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.option_delete_task -> {
                // Instantiate a DAO
                val taskDAO = TaskDAO(requireContext())
                taskDAO.document = task
                UserPreferences(requireContext()).getLoggedInUser()?.let { taskDAO.deleteTask(it.authUid) }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}