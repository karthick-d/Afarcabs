package com.wings.mile.activity

import com.wings.mile.Utils.Pref_storage
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import android.widget.LinearLayout
import com.wings.mile.R

import android.os.Bundle
import android.os.Build
import com.wings.mile.activity.WelcomeActivity.MyViewPagerAdapter
import android.text.Html
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import com.wings.mile.activity.LoginActivity
import android.view.WindowManager
import androidx.viewpager.widget.PagerAdapter
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import com.wings.mile.Utils.BaseActivity
import com.wings.mile.Utils.PrefManager
import java.util.*

class WelcomeActivity : BaseActivity() {
    var pref_storage: Pref_storage? = null
    var btnskip: TextView? = null
    private var viewPager: ViewPager? = null
    private var dotsLayout: LinearLayout? = null
    private lateinit var layouts: IntArray

    //	viewpager change listener
    var viewPagerPageChangeListener: ViewPager.OnPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageSelected(position: Int) {
            addBottomDots(position)


            /*changing the next button text 'NEXT' / 'GOT IT'*/if (position == 2) {
                // last page. make button text to GOT IT
                // btnNext.setText(getString(R.string.start));
                btnskip!!.setText(R.string.gotiit)
            } else {
                // still pages are left
                //btnNext.setText(getString(R.string.next));
                //btnSkip.setVisibility(View.VISIBLE);
            }
        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {
            //Toast.makeText(WelcomeActivity.this, "sdd"+, Toast.LENGTH_SHORT).show();
        }

        override fun onPageScrollStateChanged(arg0: Int) {
            //Do Nothing
        }
    }
    private var prefManager: PrefManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pref_storage = Pref_storage()


        /* Checking for first time launch - before calling setContentView()*/
        prefManager = PrefManager(this)
        if (!prefManager!!.isFirstTimeLaunch) {
            launchHomeScreen()
            finish()
        }

        /* Making notification bar transparent*/if (Build.VERSION.SDK_INT >= 21) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        setContentView(R.layout.activity_welcome)
        viewPager = findViewById(R.id.view_pager)
        dotsLayout = findViewById(R.id.layoutDots)
        btnskip = findViewById(R.id.btn_next)


        /* layouts of all welcome sliders*/
        // add few more layouts if you want
        layouts = intArrayOf(
                R.layout.welcome_slide1,
                R.layout.welcome_slide2,
                R.layout.welcome_slide3)

        /* adding bottom dots*/addBottomDots(0)

        /* making notification bar transparent*/changeStatusBarColor()
        val myViewPagerAdapter = MyViewPagerAdapter()
        viewPager?.adapter = myViewPagerAdapter
        viewPager?.addOnPageChangeListener(viewPagerPageChangeListener)
        btnskip?.setOnClickListener(View.OnClickListener { launchHomeScreen() })
    }

    /* Bottomdot launch in layout*/
    private fun addBottomDots(currentPage: Int) {
        val dots = arrayOfNulls<TextView>(layouts.size)
        val colorsActive = resources.getIntArray(R.array.array_dot_active)
        val colorsInactive = resources.getIntArray(R.array.array_dot_inactive)
        dotsLayout!!.removeAllViews()
        for (i in dots.indices) {
            dots[i] = TextView(this)
            dots[i]!!.text = Html.fromHtml("&#8226;")
            dots[i]!!.textSize = 35f
            dots[i]!!.setTextColor(colorsInactive[currentPage])
            dotsLayout!!.addView(dots[i])
        }
        if (dots.size > 0) dots[currentPage]!!.setTextColor(colorsActive[currentPage])
    }

    private fun launchHomeScreen() {
        prefManager!!.isFirstTimeLaunch = false
        val i = Intent(this@WelcomeActivity, DriverPermissionActivity::class.java)
        startActivity(i)
        finish()
    }

    /**
     * Making notification bar transparent
     */
    private fun changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
    }

//    override fun handleIdleTimeout() {
//        // Do Nothing
//    }

    /**
     * View pager adapter
     */
    inner class MyViewPagerAdapter : PagerAdapter() {
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val layoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = layoutInflater.inflate(layouts[position], container, false)
            container.addView(view)
            return view
        }

        override fun getCount(): Int {
            return layouts.size
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view === obj
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val view = `object` as View
            container.removeView(view)
        }
    }


}