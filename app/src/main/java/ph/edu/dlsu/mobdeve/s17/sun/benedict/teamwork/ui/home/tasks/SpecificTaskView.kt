package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.tasks

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
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

    var editMode: Boolean = false

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
                "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.update_user_task" -> {
                    Toast.makeText(requireContext(), "Successfully updated the task.", Toast.LENGTH_LONG).show()
                }
                "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.update_user_task_failed" -> {
                    Toast.makeText(requireContext(), "Failed to update task.", Toast.LENGTH_LONG).show()
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
        Log.d("SpecificTaskView:onCreateView", task.isCompleted.toString())
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
        intentFilter.addAction("ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.update_user_task")
        intentFilter.addAction("ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.update_user_task_failed")
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver, intentFilter)

        // Initialize OnClickListener for the FAB
        this.fragmentBinding.fabEditTask.setOnClickListener { v ->
            if(editMode) {
                // Set the icon back to the pencil
                // Commit changes
                fragmentBinding.taskViewAbout.visibility = View.VISIBLE
                fragmentBinding.taskViewDesc.visibility = View.VISIBLE
                fragmentBinding.viewTaskDueDateTime.visibility = View.VISIBLE
                fragmentBinding.etTaskDescription.visibility = View.GONE
                fragmentBinding.etTaskViewAbout.visibility = View.GONE
                fragmentBinding.etTaskViewName.visibility = View.GONE
                fragmentBinding.etTaskViewDue.visibility = View.GONE
                fragmentBinding.fabEditTask.setImageResource(R.drawable.ic_baseline_edit_24)

                // Commit to the task object then call the DAO
                task.name = fragmentBinding.etTaskViewName.text.toString()
                task.about = fragmentBinding.etTaskViewAbout.text.toString()
                task.description = fragmentBinding.etTaskDescription.text.toString()
                (activity as AppCompatActivity).supportActionBar?.title = task.name
                fragmentBinding.taskViewAbout.setText(task.about)
                fragmentBinding.taskViewDesc.setText(task.description)
                fragmentBinding.etTaskViewDue.setText(task.dueDate.toString().subSequence(0, 19))

                val taskDAO = TaskDAO(requireContext())
                taskDAO.document = task
                UserPreferences(requireContext()).getLoggedInUser()?.let { taskDAO.updateTask(it.authUid) }

            } else {
                // Migrate Values
                fragmentBinding.etTaskViewName.setText(task.name)
                fragmentBinding.etTaskViewAbout.setText(task.about)
                fragmentBinding.etTaskDescription.setText(task.description)
                fragmentBinding.etTaskViewDue.setText(fragmentBinding.viewTaskDueDateTime.text.toString())

                // Set edit texts to visible, hide text views
                fragmentBinding.taskViewAbout.visibility = View.GONE
                fragmentBinding.taskViewDesc.visibility = View.GONE
                fragmentBinding.viewTaskDueDateTime.visibility = View.GONE
                fragmentBinding.etTaskDescription.visibility = View.VISIBLE
                fragmentBinding.etTaskViewAbout.visibility = View.VISIBLE
                fragmentBinding.etTaskViewName.visibility = View.VISIBLE
                fragmentBinding.etTaskViewDue.visibility = View.VISIBLE
                fragmentBinding.fabEditTask.setImageResource(R.drawable.ic_baseline_done_24)
            }

            // Flip the edit mode
            editMode = !editMode
        }

        this.fragmentBinding.fabNewSubtask.setOnClickListener { v ->
            // Pass the current Task object through a bundle
            val taskBundle = bundleOf(
                "taskObject" to this.task as Parcelable
            )
             findNavController().navigate(R.id.fromTaskViewToNewSubtask, taskBundle)
        }

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

    override fun onStop() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)
        super.onStop()
    }
}