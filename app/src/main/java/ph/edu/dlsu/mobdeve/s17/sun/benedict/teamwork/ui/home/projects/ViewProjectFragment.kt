package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.projects

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao.ProjectDAO
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao.UserDAO
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentProjectsBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentViewProjectBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentViewTaskBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Project
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.utils.UserPreferences
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class ViewProjectFragment : Fragment() {

    val firebaseStorage = FirebaseStorage.getInstance()

    lateinit var fragmentBinding: FragmentViewProjectBinding

    var editState = false

    lateinit var project: Project

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize the view binding
        fragmentBinding = FragmentViewProjectBinding.inflate(inflater, container, false)
        val view = fragmentBinding.root

        // Get the bundled data
        this.project = arguments?.getParcelable<Project>("project")!!

        // Apply to views
        fragmentBinding.viewProjectAbout.setText(this.project.about)
        fragmentBinding.viewProjectDesc.setText(this.project.description)
        fragmentBinding.viewProjectCompletionDate.setText("${this.project.completionDate.toString().subSequence(0, 19)}")
        (activity as AppCompatActivity).supportActionBar?.title = this.project.name
        resetProjectImage()

        // OnClickListeners
        fragmentBinding.btnEditProjectImage.setOnClickListener {
            // Open Gallery
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                0
            )
        }
        fragmentBinding.fabEditProject.setOnClickListener {
            if(!editState) {
                fragmentBinding.etViewProjectAbout.setText(fragmentBinding.viewProjectAbout.text.toString())
                fragmentBinding.etViewProjectDesc.setText(fragmentBinding.viewProjectDesc.text.toString())
                fragmentBinding.etViewProjectName.setText(project.name)

                fragmentBinding.etViewProjectName.visibility = View.VISIBLE
                fragmentBinding.etViewProjectDesc.visibility = View.VISIBLE
                fragmentBinding.etViewProjectAbout.visibility = View.VISIBLE
                fragmentBinding.btnEditProjectImage.visibility = View.VISIBLE

                fragmentBinding.viewProjectDesc.visibility = View.GONE
                fragmentBinding.viewProjectAbout.visibility = View.GONE
                fragmentBinding.fabEditProject.setImageResource(R.drawable.ic_baseline_done_24)
            } else {
                fragmentBinding.etViewProjectName.visibility = View.GONE
                fragmentBinding.etViewProjectDesc.visibility = View.GONE
                fragmentBinding.etViewProjectAbout.visibility = View.GONE
                fragmentBinding.btnEditProjectImage.visibility = View.GONE

                fragmentBinding.viewProjectDesc.visibility = View.VISIBLE
                fragmentBinding.viewProjectAbout.visibility = View.VISIBLE
                fragmentBinding.fabEditProject.setImageResource(R.drawable.ic_baseline_edit_24)

                // Commit changes
                this.project.name = fragmentBinding.etViewProjectName.text.toString()
                this.project.about = fragmentBinding.etViewProjectAbout.text.toString()
                this.project.description = fragmentBinding.etViewProjectDesc.text.toString()

                val projectDAO = ProjectDAO(requireContext())
                projectDAO.document = this.project
                UserPreferences(requireContext()).getLoggedInUser()?.let {
                    projectDAO.updateProjectCb(it.authUid) {
                        if(it) {
                            Toast.makeText(requireContext(), "Updated project information.", Toast.LENGTH_LONG).show()

                            fragmentBinding.viewProjectAbout.setText(this.project.about)
                            fragmentBinding.viewProjectDesc.setText(this.project.description)
                            fragmentBinding.viewProjectCompletionDate.setText(this.project.completionDate.toString())
                        } else {
                            Toast.makeText(requireContext(), "Failed to update project information.", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            editState = !editState
        }

        return view
    }

    fun resetProjectImage() {
        // Image processing
        val dlUrl = project.coverImage

        if(!dlUrl.isEmpty()) {
            val storageReference = this.firebaseStorage.getReference(dlUrl)
            storageReference.getBytes(1024*1024*15)
                .addOnSuccessListener {
                    val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
                    fragmentBinding.ivProjectImage.setImageBitmap(
                        Bitmap.createScaledBitmap(
                            bmp,
                            fragmentBinding.ivProjectImage.width,
                            fragmentBinding.ivProjectImage.height,
                            false
                        )
                    )
                }
                .addOnFailureListener {
                    Toast.makeText(context, it.localizedMessage, Toast.LENGTH_LONG).show()
                }
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
                        val referencePath = "project_images/" + UserPreferences(requireContext()).getLoggedInUser()!!.authUid + "-" + Date().toString()
                        var newBitmapPath = storageReference.child(referencePath)

                        val baos = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        val data: ByteArray = baos.toByteArray()

                        val uploadTask: UploadTask = newBitmapPath.putBytes(data)
                        uploadTask
                            .addOnFailureListener {
                                // Handle unsuccessful uploads
                                Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_LONG).show()
                            }
                            .addOnSuccessListener { taskSnapshot -> // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                this.project.coverImage = referencePath
                                val user = UserPreferences(requireContext()).getLoggedInUser()!!

                                // Update project information
                                val projectDAO = ProjectDAO(requireContext())
                                projectDAO.document = project
                                projectDAO.updateProjectCb(user.authUid) {
                                    // If successful, update UserPreferences
                                    Toast.makeText(requireContext(), "Successfully uploaded new project image!", Toast.LENGTH_SHORT).show()
                                    if(!it) { Toast.makeText(requireContext(), "Failed to update project image uri!", Toast.LENGTH_LONG).show() }
                                    resetProjectImage()
                                }
                            }

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

}