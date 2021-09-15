package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.utils

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R

class PhotoUploadDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.photo_upload_dialog_message)
                .setPositiveButton(R.string.photo_upload_dialog_camera,
                    DialogInterface.OnClickListener { dialog, id ->
                        // Open Camera
                        Toast.makeText(requireContext(), "Open Camera", Toast.LENGTH_LONG).show()
                    })
                .setNeutralButton(R.string.photo_upload_dialog_gallery,
                    DialogInterface.OnClickListener { dialog, id ->
                        // Open Gallery
                        Toast.makeText(requireContext(), "Open Camera", Toast.LENGTH_LONG).show()
                    })
                .setNegativeButton(R.string.photo_upload_dialog_cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        // Cancel
                        Toast.makeText(requireContext(), "Open Camera", Toast.LENGTH_LONG).show()
                    })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}