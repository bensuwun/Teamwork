package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.projects

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
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.adapters.ProjectAdapter
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.adapters.TaskAdapter
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao.ProjectDAO
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentProjectsBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Project
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Task
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.utils.UserPreferences

/**
 * A simple [Fragment] subclass.
 */
class Projects : Fragment() {

    private val broadcastReceiver = object: BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            if(intent != null) {
                when(intent.action) {
                    ProjectDAO.GET_USER_PROJECTS_SUCCESS_INTENT -> {
                        projects = (intent.extras!!["projectList"] as Array<Project>).toCollection(ArrayList())
                        viewAdapter = ProjectAdapter(projects, requireContext())
                        binding.rvProjects.adapter = viewAdapter
                    }
                }
            }

        }
    }

    lateinit var projects: ArrayList<Project>
    lateinit var viewAdapter: ProjectAdapter

    private lateinit var binding : FragmentProjectsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProjectsBinding.inflate(inflater, container, false)
        val view = binding.root

        // Register the broadcastReceiver
        val intentFilter = IntentFilter()
        intentFilter.addAction(ProjectDAO.GET_USER_PROJECTS_SUCCESS_INTENT)
        intentFilter.addAction(ProjectDAO.GET_USER_PROJECTS_FAILURE_INTENT)
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver, intentFilter)

        // Populate recycler view
        binding.rvProjects.layoutManager = LinearLayoutManager(context)

        // Set the OnClickListeners
        binding.fabAddProject.setOnClickListener {
            findNavController().navigate(R.id.navigateToCreateProject)
        }

        // Call for the data fetch
        this.projects = ArrayList()
        val projectDAO = ProjectDAO(requireContext())
        UserPreferences(requireContext()).getLoggedInUser()?.let { projectDAO.getAllUserProjects(it.authUid) }

        return view
    }

    override fun onStop() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)
        super.onStop()
    }

}