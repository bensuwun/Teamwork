package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.guilds

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.storage.FirebaseStorage
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentAddGuildBinding

/**
 * A simple [Fragment] subclass.
 */
class AddGuildFragment : Fragment() {
    private lateinit var binding : FragmentAddGuildBinding
    private val TAG = "AddGuildFragment"

    private val storage : FirebaseStorage = FirebaseStorage.getInstance()
    private var rootRef = storage.reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddGuildBinding.inflate(inflater, container, false)
        val view = binding.root

        setEventHandlers()

        return view
    }

    private fun setEventHandlers() {
        binding.btnCreateGuild.setOnClickListener {
            val name = binding.etGuildName.text.toString()
            var description = binding.etGuildDesc.text.toString()

            // Validation
            if(name.trim() == "") {
                Toast.makeText(requireContext(), "Please provide a guild name", Toast.LENGTH_SHORT).show()
            }
            else{
                if (description.trim() == "") {
                    description = "This guild has not provided a description"
                }
                // TODO: Add author User object

            }
        }

        binding.sivGuildDp.setOnClickListener {
            updateProfilePic()
        }

        binding.ivChangeGuildDpIcon.setOnClickListener {
            updateProfilePic()
        }

        binding.ivGuildHeader.setOnClickListener {
            updateHeaderPic()
        }

        binding.ivChangeGuildHeaderIcon.setOnClickListener {
            updateHeaderPic()
        }
    }


    private fun updateProfilePic() {
        // Check runtime permission
        // If OS is < Marshmallow
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Permission denied -> request again
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PROFILE_PERMISSION_CODE)
            }
            // Permission already granted
            else{
                pickImageFromGallery("profile")
            }
        }
        else{
            // OS < Marshmallow
            pickImageFromGallery("profile")
        }
    }

    private fun updateHeaderPic() {
        // Check runtime permission
        // If OS is < Marshmallow
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Permission denied -> request again
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), HEADER_PERMISSION_CODE)
            }
            // Permission already granted
            else{
                pickImageFromGallery("header")
            }
        }
        else{
            // OS < Marshmallow
            pickImageFromGallery("header")
        }
    }

    private fun pickImageFromGallery(pick : String) {
        // Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        when(pick) {
            "profile" -> startActivityForResult(intent, PROFILE_PICK_CODE)
            "header" -> startActivityForResult(intent, HEADER_PICK_CODE)
        }
    }

    companion object  {
        // image pick code
        private val PROFILE_PICK_CODE = 1000
        private val HEADER_PICK_CODE = 1001
        // permission code
        private val PROFILE_PERMISSION_CODE = 1002
        private val HEADER_PERMISSION_CODE = 1003
    }

    /**
     * Handles the response of the user to the permission prompt
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PROFILE_PERMISSION_CODE -> {
                // Runtime permission granted
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery("profile")
                }
                // Permission denied
                else {
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            HEADER_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery("header")
                }
                else {
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Handles the user's picked image result
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            Log.d(TAG, data?.data.toString())
            when(requestCode) {
                PROFILE_PICK_CODE -> binding.sivGuildDp.setImageURI(data?.data)
                HEADER_PICK_CODE -> binding.ivGuildHeader.setImageURI(data?.data)
            }

        }
    }
}