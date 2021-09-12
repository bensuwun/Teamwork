package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentNewTaskBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.TimePickerFragment
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.utils.DatePickerFragment

class AddTask: Fragment() {

    lateinit var fragmentBinding: FragmentNewTaskBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        // Initialize the View
        this.fragmentBinding = FragmentNewTaskBinding.inflate(inflater, container, false)
        val v = fragmentBinding.root

        // Initialize OnClickListeners
        this.fragmentBinding.btnNewTaskSetTime.setOnClickListener {
            TimePickerFragment().show(this.childFragmentManager, "timePicker")
        }
        this.fragmentBinding.btnNewTaskSetDate.setOnClickListener {
            val newFragment = DatePickerFragment()
            newFragment.show(this.childFragmentManager, "datePicker")
        }

        return v
    }
}