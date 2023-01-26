package com.wings.mile.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wings.mile.R
import com.wings.mile.Utils.BaseActivity
import com.wings.mile.databinding.ActivityDriverdetailsBinding

class FeedbackActivity : BaseActivity() {

    private lateinit var binding: ActivityDriverdetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driverdetails)
        binding = ActivityDriverdetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarLayout.toolbarTitle.text=getString(R.string.header)


        binding.terminate.setOnClickListener {
            val intent= Intent(this, TerminateActivity::class.java)
            startActivity(intent)

        }
        binding.next.setOnClickListener {
            val intent= Intent(this, TerminateActivity::class.java)
            startActivity(intent)

        }
    }

}