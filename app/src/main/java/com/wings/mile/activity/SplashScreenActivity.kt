package com.wings.mile.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Point
import android.icu.util.Calendar
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.wings.mile.R
import com.wings.mile.Utils.Pref_storage
import com.wings.mile.databinding.ActivitySplashScreenBinding
import java.util.*


class SplashScreenActivity : AppCompatActivity() {
    var English: ToggleButton? = null
    var Hindi: ToggleButton? = null
    var rl: RelativeLayout? = null
    private val handler = Handler()
    lateinit var dataBinding: ActivitySplashScreenBinding

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen)


        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        val height = size.y
        Pref_storage.setDetail(this, "width", width.toString())
        Pref_storage.setDetail(this, "height", height.toString())

        /*Toggle on check listener*/
        dataBinding.English.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->
            dataBinding.Hindi.isChecked = false
            dataBinding.English.setTextColor(getColor(R.color.white))
            dataBinding.Hindi.setTextColor(getColor(R.color.white))
            dataBinding.English.setBackgroundDrawable(getDrawable(R.drawable.button_background1))
            Toast.makeText(this@SplashScreenActivity, R.string.selectlanguage, Toast.LENGTH_SHORT)
                .show()
            Pref_storage.setDetail(this@SplashScreenActivity, "language", "English")
            startActivity(Intent(this@SplashScreenActivity, WelcomeActivity::class.java))
            finish()
        })
        dataBinding.Hindi.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->
            dataBinding.English.isChecked = false
            dataBinding.Hindi.setTextColor(getColor(R.color.white))
            dataBinding.English.setTextColor(getColor(R.color.white))
            dataBinding.Hindi.setBackgroundDrawable(getDrawable(R.drawable.button_background1))
            Toast.makeText(this@SplashScreenActivity, R.string.slang, Toast.LENGTH_SHORT).show()
            Pref_storage.setDetail(this@SplashScreenActivity, "language", "Tamil")
            startActivity(Intent(this@SplashScreenActivity, WelcomeActivity::class.java))
            finish()
        })
        if (Pref_storage.getDetail(this, "language") == null) {
            dataBinding.Hindi.visibility = View.VISIBLE
            dataBinding.English.visibility = View.VISIBLE
        } else {
            dataBinding.Hindi.visibility = View.GONE
            dataBinding.English.visibility = View.GONE
            handler.postDelayed({ // Checking for first time launch - before calling setContentView()
                startActivity(Intent(this@SplashScreenActivity, WelcomeActivity::class.java))
                finish()
            }, 3000)
        }
        val year = Calendar.getInstance()[Calendar.YEAR]
        Log.d("currentyesar", "-------->$year")
        val myanim: Animation = AnimationUtils.loadAnimation(this, R.anim.transition)
        dataBinding.img.startAnimation(myanim)
        //        copyrights.setText(year + "" + getString(R.string.copyrights_txt));
    }

}