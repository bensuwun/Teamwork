package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.guilds.posts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentViewPostBinding

/**
 * A simple [Fragment] subclass.
 */
class ViewPostFragment : Fragment() {
    private lateinit var binding : FragmentViewPostBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentViewPostBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

}