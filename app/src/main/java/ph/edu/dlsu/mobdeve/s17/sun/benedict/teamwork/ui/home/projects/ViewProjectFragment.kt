package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.projects

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentProjectsBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentViewProjectBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentViewTaskBinding

/**
 * A simple [Fragment] subclass.
 */
class ViewProjectFragment : Fragment() {

    lateinit var fragmentBinding: FragmentViewProjectBinding

    lateinit var projectName: String
    lateinit var aboutProject: String
    lateinit var projectDesc: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize the view binding
        fragmentBinding = FragmentViewProjectBinding.inflate(inflater, container, false)
        val view = fragmentBinding.root

        // Get the bundled data
        this.projectName = arguments?.getString("projectName").toString()
        this.aboutProject = arguments?.getString("aboutProject").toString()
        this.projectDesc = arguments?.getString("projectDesc").toString()

        // Apply to views
        fragmentBinding.viewProjectName.setText(this.projectName)
        fragmentBinding.viewProjectAbout.setText(this.aboutProject)
        fragmentBinding.viewProjectDesc.setText(this.projectDesc)
        (activity as AppCompatActivity).supportActionBar?.title = projectName

        return view
    }

}