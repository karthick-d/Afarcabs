package com.wings.mile.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wings.mile.DashboardActivity
import com.wings.mile.R
import com.wings.mile.Utils.BaseActivity
import com.wings.mile.databinding.ActivityTerminateBinding

class TerminateActivity : BaseActivity() {

    private lateinit var binding: ActivityTerminateBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terminate)
        binding = ActivityTerminateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarLayout.toolbarTitle.text = "Terminate"


        binding.submit.setOnClickListener {
            if (binding.radiogroup.getCheckedRadioButtonId() == -1) {
                // no radio buttons are checked
                Toast.makeText(this, "Termination Reason Mandatory", Toast.LENGTH_LONG).show()

            } else if (binding.editTextdescription.text.toString().isNullOrEmpty()) {

                Toast.makeText(this, "Description Mandatory", Toast.LENGTH_LONG).show()
            } else if (binding.editTextdescription.text.toString().length < 10) {

                Toast.makeText(this, "Description length more than 10 characters", Toast.LENGTH_LONG).show()
            } else {
                showdialog()
            }

        }
    }

    /*Exit Application alert functionality*/
    private fun showdialog() {
        AlertDialog.Builder(this)
            .setTitle("Alert")
            .setMessage("You are about to terminate the services of this driver. Do you wish to continue?") // Specifying a listener allows you to take an action before dismissing the dialog.
            // The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton(android.R.string.yes) { dialog: DialogInterface?, which: Int ->
                val intent= Intent(this, DashboardActivity::class.java)
                startActivity(intent)
            } // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton(android.R.string.no) { dialog: DialogInterface, which: Int ->
                // Continue with delete operation
                dialog.dismiss()
            }
            .show()
    }
}