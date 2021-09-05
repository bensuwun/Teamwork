package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.adapters.ProjectAdapter
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentProjectsBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Project

/**
 * A simple [Fragment] subclass.
 */
class Projects : Fragment() {

    private lateinit var binding : FragmentProjectsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProjectsBinding.inflate(inflater, container, false)
        val view = binding.root

        // Populate recycler view
        binding.rvProjects.layoutManager = LinearLayoutManager(context)
        binding.rvProjects.adapter = context?.let { ProjectAdapter(Project.initSampleData(), it) }

        return view
    }

}