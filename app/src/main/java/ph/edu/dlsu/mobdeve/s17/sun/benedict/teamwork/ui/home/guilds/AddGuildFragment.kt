package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.guilds

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao.GuildDAO
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao.GuildMemberDAO
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentAddGuildBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Guild
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Post
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.User
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.utils.UserPreferences
import java.io.ByteArrayOutputStream
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class AddGuildFragment : Fragment() {
    private lateinit var binding : FragmentAddGuildBinding
    private val TAG = "AddGuildFragment"
    private lateinit var guildDAO : GuildDAO

    // New Guild
    private var newGuild = Guild()
    private var tempProfileUri = ""
    private var tempHeaderUri= ""

    private val storage : FirebaseStorage = FirebaseStorage.getInstance()
    private var rootRef = storage.reference
    private lateinit var userPreferences : UserPreferences

    private lateinit var progressDialog : ProgressDialog

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action) {
                intentGuildCreated -> {
                    progressDialog.dismiss()
                    Toast.makeText(context, "Successfully created guild", Toast.LENGTH_SHORT).show()
                    guildDAO.incrementGuildMemberCount(newGuild.name)
                    UserPreferences.getUserAuthUid()?.let {
                        GuildMemberDAO(requireContext()).joinGuild(newGuild.name,
                            it
                        )
                    }
                    // Navigate to guild dashboard
                    val bundle = Bundle()
                    bundle.putParcelable("guild", newGuild)
                    findNavController().navigate(R.id.fromCreateGuildNavigateToDashboard, bundle)

                }
                intentGuildExists -> {
                    val exists = intent.extras?.getBoolean("exists")
                    if (exists == true) {
                        progressDialog.dismiss()
                        Toast.makeText(context, "Guild name already exists. Please try another one.", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        uploadToFirestore(newGuild, Uri.parse(tempProfileUri), Uri.parse(tempHeaderUri))
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddGuildBinding.inflate(inflater, container, false)
        val view = binding.root

        // Initialize progress dialog for guild creation
        progressDialog = ProgressDialog(activity)
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)

        userPreferences = UserPreferences(requireContext())

        guildDAO = GuildDAO(requireContext())

        // Initialize broadcast receiver for guild created
        val intentFilters = IntentFilter()
        intentFilters.addAction(intentGuildExists)
        intentFilters.addAction(intentGuildCreated)
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver, intentFilters)

        setEventHandlers()

        return view
    }

    private fun setEventHandlers() {
        binding.btnCreateGuild.setOnClickListener {
            var name = binding.etGuildName.text.toString()
            var description = binding.etGuildDesc.text.toString()

            // Validation
            if(name.trim() == "") {
                Toast.makeText(requireContext(), "Please provide a guild name", Toast.LENGTH_SHORT).show()
            }
            else{
                if (description.trim() == "") {
                    description = "This guild has not provided a description"
                }

                val master = userPreferences.getLoggedInUser()
                Log.d(TAG, "Username: ${master?.username}")

                Log.d(TAG, binding.sivGuildDp.tag.toString())
                tempProfileUri = binding.sivGuildDp.tag.toString()
                tempHeaderUri = binding.ivGuildHeader.tag.toString()

                if (tempProfileUri != "") {
                    if(tempHeaderUri != "") {
                        newGuild = master?.let { it1 -> Guild(name, it1, description, 0) }!!
                        if (newGuild != null) {
                            // Check if guild exists -> if not, broadcast call to upload to firestore

                            // https://stackoverflow.com/questions/10446125/how-to-show-progress-dialog-in-android
                            progressDialog.show()
                            guildDAO.guildExists(name)
                        }
                    }
                    else{
                        Toast.makeText(requireContext(), "Please provide a header image for your guild", Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    // Require a guild image
                    Toast.makeText(requireContext(), "Please provide a profile picture for your guild", Toast.LENGTH_SHORT).show()
                }
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

    private fun uploadToFirestore(newGuild: Guild, profileUri : Uri, headerUri : Uri) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference

        // References to new files (FILENAMES ARE BASED ON GUILD NAME)
        val guildProfileRef = storageRef.child("guild_profile_pictures/${newGuild.name}.jpg")
        val headerProfileRef = storageRef.child("guild_headers/${newGuild.name}.jpg")

        // Download URLs after upload
        var profileDownloadUri : String
        var headerDownloadUri : String

        // Upload guild profile image (https://stackoverflow.com/questions/61610024/how-to-upload-an-image-to-firebase-storage-using-kotlin-in-android-q)
        guildProfileRef.putFile(profileUri)
            .addOnSuccessListener() {
                // Get download URL
                guildProfileRef.downloadUrl.addOnCompleteListener {
                    profileDownloadUri = it.result.toString()
                    Log.d(TAG, "Uploaded guild profile picture: $profileDownloadUri")

                    // Upload header
                    headerProfileRef.putFile(headerUri)
                        .addOnSuccessListener {
                            // Get download URL
                            headerProfileRef.downloadUrl.addOnCompleteListener {
                                headerDownloadUri = it.result.toString()
                                Log.d(TAG, "Uploaded guild header image: $headerDownloadUri")

                                newGuild.profileImage = profileDownloadUri
                                newGuild.headerImage = headerDownloadUri

                                // Store to Firestore
                                GuildDAO(requireContext()).createGuild(newGuild)
                            }
                        }
                        .addOnFailureListener() {
                            Log.e(TAG, it.message.toString())
                        }
                }
            }
            .addOnFailureListener() {
                Log.e(TAG, it.message.toString())
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

        val intentGuildCreated = "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.guild_created"
        val intentGuildExists = "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.guild_exists"
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
                PROFILE_PICK_CODE -> {
                    binding.sivGuildDp.setImageURI(data?.data)
                    binding.sivGuildDp.tag = data?.data
                }
                HEADER_PICK_CODE -> {
                    binding.ivGuildHeader.setImageURI(data?.data)
                    binding.ivGuildHeader.tag = data?.data
                }
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)
    }
}