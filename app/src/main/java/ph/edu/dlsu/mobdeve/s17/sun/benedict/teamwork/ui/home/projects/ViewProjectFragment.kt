package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.projects

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R

/**
 * A simple [Fragment] subclass.
 */
class ViewProjectFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_project, container, false)
    }

}