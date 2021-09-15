package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentSettingsBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.authentication.MainActivity
import java.io.IOException
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.utils.UserPreferences
import java.util.*
import com.google.firebase.storage.UploadTask
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao.UserDAO
import java.io.ByteArrayOutputStream
import android.graphics.BitmapFactory
import android.widget.ImageView


/**
 * A simple [Fragment] subclass.
 */
class Settings : Fragment() {

    lateinit var binding: FragmentSettingsBinding
    private val firebaseStorage = FirebaseStorage.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater)

        binding.settingsLogout.setOnClickListener {
            Toast.makeText(this.activity?.applicationContext, "Logged Out!", Toast.LENGTH_SHORT).show()
            // Sign out from Firebase
            FirebaseAuth.getInstance().signOut()
            // Sign out from Google
            GoogleSignIn.getClient((this.activity as HomeActivity).applicationContext,GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN).build()).signOut()
            val mainActivityIntent = Intent(this.activity?.applicationContext, MainActivity::class.java)
            startActivity(mainActivityIntent)
            this.activity?.finish()
        }

        // Set OnClickListener for upload fab
        binding.fabEditProfileImage.setOnClickListener {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage(R.string.photo_upload_dialog_message)
                .setPositiveButton(R.string.photo_upload_dialog_camera,
                    DialogInterface.OnClickListener { dialog, id ->
                        // Open Camera
                        val REQUEST_IMAGE_CAPTURE = 1

                        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        try {
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                        } catch (e: ActivityNotFoundException) {
                            // display error state to the user
                            Log.d("Settings:fabEditProfileImage", e.localizedMessage)
                        }
                    })
                .setNeutralButton(R.string.photo_upload_dialog_cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        // Cancel
                    })
                .setNegativeButton(R.string.photo_upload_dialog_gallery,
                    DialogInterface.OnClickListener { dialog, id ->
                        // Open Gallery
                        val intent = Intent()
                        intent.type = "image/*"
                        intent.action = Intent.ACTION_GET_CONTENT
                        startActivityForResult(
                            Intent.createChooser(intent, "Select Picture"),
                            0
                        )
                    })
            // Create the AlertDialog object and return it
            builder.show()
        }

        // Set the username
        val user = UserPreferences(requireContext()).getLoggedInUser()!!
        binding.tvSettingsUsername.setText(user.username)
        resetProfileImage(binding.root)

        // Inflate the layout for this fragment
        return binding.root
    }

    fun resetProfileImage(v: View) {
        val user = UserPreferences(requireContext()).getLoggedInUser()!!
        val dlUrl = user.profileImage

        Log.d("Settings:resetProfileImage", dlUrl)
        if(dlUrl.isEmpty()) return

        val storageReference = this.firebaseStorage.getReference(dlUrl)
        storageReference.getBytes(1024*1024*15)
            .addOnSuccessListener {
                val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
                val image: ImageView = v.findViewById(R.id.siv_settings_user_dp) as ImageView
                image.setImageBitmap(
                    Bitmap.createScaledBitmap(
                        bmp,
                        image.width,
                        image.height,
                        false
                    )
                )
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    try {
                        val bitmap =
                            MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, data.data)

                        // Upload image to Firebase Storage
                        val storageReference = this.firebaseStorage.getReferenceFromUrl(resources.getString(R.string.firebase_storage_bucket_link))
                        val referencePath = "user_pictures/" + UserPreferences(requireContext()).getLoggedInUser()!!.authUid + "-" + Date().toString()
                        var newBitmapPath = storageReference.child(referencePath)

                        val baos = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        val data: ByteArray = baos.toByteArray()

                        val uploadTask: UploadTask = newBitmapPath.putBytes(data)
                        uploadTask
                            .addOnFailureListener {
                            // Handle unsuccessful uploads
                            }
                            .addOnSuccessListener { taskSnapshot -> // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                val user = UserPreferences(requireContext()).getLoggedInUser()!!
                                user.profileImage = referencePath

                                // Update user information
                                val userDAO = UserDAO()
                                userDAO.document = user
                                userDAO.updateDocument(user.authUid) {
                                    // If successful, update UserPreferences
                                    if(it) UserPreferences(requireContext()).saveLoggedInUser(user)
                                    else Toast.makeText(requireContext(), "Failed to update profile uri!", Toast.LENGTH_LONG).show()
                                }
                            }

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        } else if(requestCode == 1) {
            if(resultCode == Activity.RESULT_OK) {
                if(data != null) {
                    val imageBitmap = data.extras!!.get("data") as Bitmap

                }
            }
        }
    }

}