package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.DocumentReference
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.adapters.TaskAdapter
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentLoginBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentTasksBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Task
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class Tasks : Fragment() {

    lateinit var fragmentBinding: FragmentTasksBinding
    lateinit var taskList: ArrayList<Task>
    lateinit var taskAdapter: TaskAdapter

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
        // Dummy data for now
        this.taskList = generateDummyData(7)

        // Initialize the Adapter & ViewHolder
        this.taskAdapter = TaskAdapter(this.taskList, this.requireContext())
        this.fragmentBinding.taskRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        this.fragmentBinding.taskRecyclerView.adapter = this.taskAdapter

        if(this.taskList.size > 0) {
            fragmentBinding.emptyTasksGraphic.visibility = View.GONE
            fragmentBinding.taskRecyclerView.visibility = View.VISIBLE
        }

        return view
    }

    private fun generateDummyData(n: Int): ArrayList<Task> {
        Log.d("Tasks:generateDummyData", "Generating $n dummy tasks.")
        val dummyTasks = ArrayList<Task>()
        for (i in 0..n)
            dummyTasks.add(Task("test_task", "Test Task $i", "Help this task be happy.", "Hi, I am a sad task.", "coverImageUri", Date(), ArrayList<DocumentReference>()))
        return dummyTasks
    }

}