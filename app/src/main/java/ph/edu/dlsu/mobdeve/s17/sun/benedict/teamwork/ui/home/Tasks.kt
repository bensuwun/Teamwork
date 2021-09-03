package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentLoginBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentTasksBinding

/**
 * A simple [Fragment] subclass.
 */
class Tasks : Fragment() {

    lateinit var fragmentBinding: FragmentTasksBinding

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

        // Link the RecyclerView


        return view
    }

}