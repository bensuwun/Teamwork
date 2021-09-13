package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.tasks

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentTasksBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentViewTaskBinding

class SpecificTaskView: Fragment() {

    lateinit var fragmentBinding: FragmentViewTaskBinding

    lateinit var taskName: String
    lateinit var aboutTask: String
    lateinit var taskDesc: String

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
        this.taskName = arguments?.getString("taskName").toString()
        this.aboutTask = arguments?.getString("aboutTask").toString()
        this.taskDesc = arguments?.getString("description").toString()

        // Apply to views
        fragmentBinding.taskViewAbout.setText(aboutTask)
        fragmentBinding.taskViewDesc.setText(taskDesc)
        (activity as AppCompatActivity).supportActionBar?.title = taskName

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.view_task_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}