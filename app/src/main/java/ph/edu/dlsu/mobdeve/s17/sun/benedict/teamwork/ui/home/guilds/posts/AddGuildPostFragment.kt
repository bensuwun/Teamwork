package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.guilds.posts

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentAddGuildPostBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Post
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Tags

/**
 * A simple [Fragment] subclass.
 */
class AddGuildPostFragment : Fragment() {
    private lateinit var binding : FragmentAddGuildPostBinding
    private val TAG = "AddGuildPostFragment"

    private lateinit var guildId : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddGuildPostBinding.inflate(inflater, container, false)
        val view = binding.root

        guildId = arguments?.getString("guildId").toString()

        // Set add post handler
        setEventHandlers()

        return view
    }

    private fun setEventHandlers() {
        binding.btnAddPost.setOnClickListener {
            // Validation
            val title = binding.etPostTitle.text.toString()
            val description = binding.etPostDescription.text.toString()
            if (title.trim() != "") {
                if (description.trim() != "") {
                    // Add post to firestore
                    // TODO: Get author username and profile picture

                    val post = Post()
                    post.title = title
                    post.description = description
                    post.likes = 0
                    post.comments = 0

                    // TODO: Get current timestamp

                    val tags = Tags()
                    tags.challenge = binding.chipChallenge.isChecked
                    tags.support = binding.chipSupport.isChecked
                    tags.social = binding.chipSocial.isChecked
                    post.tags = tags
                }
                else{
                    Toast.makeText(requireContext(), "Please provide a description", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(requireContext(), "Please provide a title", Toast.LENGTH_SHORT).show()
            }
        }
    }

}