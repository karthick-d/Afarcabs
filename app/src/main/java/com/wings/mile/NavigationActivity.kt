package com.wings.mile

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView

import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.wings.mile.Utils.Pref_storage
import com.wings.mile.activity.LoginActivity
import com.wings.mile.activity.ProfileActivity
import com.wings.mile.adapter.NavigationListAdapter
import com.wings.mile.databinding.ActivityNavigationBinding
import com.wings.mile.model.Datamodel
import com.wings.mile.model.getdriver
import com.wings.mile.ui.aboutus.AboutusFragment
import com.wings.mile.ui.contactus.ContactusFragment
import com.wings.mile.ui.home.HomeFragment
import com.wings.mile.ui.refer.ReferFragment
import com.wings.mile.ui.usernotification.NotificationView

class NavigationActivity : AppCompatActivity(),View.OnClickListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityNavigationBinding
    lateinit var savedrivers: getdriver
    var actionBarDrawerToggle: ActionBarDrawerToggle? = null
    lateinit var drawerItem: Array<Datamodel?>
    var adapter: NavigationListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setSupportActionBar(binding.appBarNavigation.toolbar)

//        binding.appBarNavigation.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }

        val request =
            Pref_storage.getDetail(this, "getdriverdetails")

        savedrivers = Gson().fromJson(request.toString(), getdriver::class.java)
        binding.headerLayout.textname.text = savedrivers.name
        binding.headerLayout.textemail.text = savedrivers.email_id


        binding.headerLayout.linktext.paintFlags = binding.headerLayout.linktext.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.headerLayout.linktext.setOnClickListener { _: View? ->
            val i = Intent(this, ProfileActivity::class.java)
            startActivityForResult(i, 112)
        }
        binding.logout.setOnClickListener { _: View? ->
            showAlertDialog()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
//        val navView: NavigationView = binding.navView
//        val navController = findNavController(R.id.nav_host_fragment_content_navigation)
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.nav_dashboard,R.id.nav_ride, R.id.nav_payments, R.id.nav_refer,R.id.nav_support,R.id.nav_about
//            ), drawerLayout
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)
        updateDriverMenuAdapter()
        binding.headerLayout.close.setOnClickListener(View.OnClickListener { _: View? ->
            binding.drawerLayout.closeDrawer(GravityCompat.START) })
        binding.appBarNavigation.actionbarAccount.notify?.setOnClickListener(this)

        binding.appBarNavigation.actionbarAccount.icon?.setOnClickListener(View.OnClickListener { _: View? -> triggerDrawerLayout() })

        // triggerDrawerLayout()
        // triggerDrawerLayout()
        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle!!)
        actionBarDrawerToggle!!.syncState()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.navigation, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_navigation)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    private fun showAlertDialog() {
        AlertDialog.Builder(this)
            .setTitle("Signout")
            .setMessage(getString(R.string.signout)) // Specifying a listener allows you to take an action before dismissing the dialog.
            // The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton(android.R.string.yes) { dialog: DialogInterface?, which: Int ->
                dialog!!.dismiss()
                val i = Intent(this, LoginActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(i)
                finish()

            } // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton(android.R.string.no) { dialog: DialogInterface, which: Int ->
                // Continue with delete operation
                val i = Intent(this, NavigationActivity::class.java)
                startActivity(i)
                dialog.dismiss()
            }
            .show()
    }

    override fun onClick(v: View?) {
        if (v!!.id == R.id.notify) {
            val i = Intent(this@NavigationActivity, NotificationView::class.java)
            startActivityForResult(i, 230)
        }
    }

    fun triggerDrawerLayout() {
        if (!binding.drawerLayout.isOpen) {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        } else {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
    }
    fun updateDriverMenuAdapter() {
        drawerItem = arrayOfNulls(7)
        val layoutManager = LinearLayoutManager(this)
        binding.menulist.layoutManager = layoutManager
        drawerItem[0] = Datamodel(R.drawable.dashboard, getString(R.string.menu_dash), true)
        drawerItem[1] = Datamodel(R.drawable.payments, getString(R.string.menu_wallets), false)
        drawerItem[2] = Datamodel(R.drawable.passenger, getString(R.string.menu_history), false)
        drawerItem[3] = Datamodel(R.drawable.refer, getString(R.string.menu_refer), false)
        drawerItem[4] = Datamodel(R.drawable.payments, getString(R.string.menu_payments), false)
        drawerItem[5] = Datamodel(R.drawable.ic_baseline_support_agent_24, getString(R.string.menu_help), false)
        drawerItem[5] = Datamodel(R.drawable.ic_baseline_support_agent_24, getString(R.string.settings), false)
        drawerItem[6] = Datamodel(R.drawable.about, getString(R.string.menu_about), false)
        adapter = NavigationListAdapter(this, drawerItem!!)
        adapter!!.setClickListener {_: View?, position: Int ->
            var fragment: Fragment? = null
            val currFrag = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_navigation)
            if(position >1){
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }

            if (position == 0 && currFrag !is HomeFragment) {
                fragment = HomeFragment()
                replaceFragment(fragment)
                binding.appBarNavigation.actionbarAccount.toolbarTitle.text="Dashboard"
            }
            else if (position == 1) {
                fragment = ReferFragment()
                replaceFragment(fragment)
                binding.appBarNavigation.actionbarAccount.toolbarTitle.text="Wallets"
            } else if (position == 2) {
                fragment = ReferFragment()
                replaceFragment(fragment)
                binding.appBarNavigation.actionbarAccount.toolbarTitle.text="History"
            } else if (position == 3) {
                fragment = ReferFragment()
                replaceFragment(fragment)
                binding.appBarNavigation.actionbarAccount.toolbarTitle.text="Refer a Friend"
            } else if (position == 4) {

                fragment = ReferFragment()
                replaceFragment(fragment)
                binding.appBarNavigation.actionbarAccount.toolbarTitle.text="Payments"

            } else if (position == 5) {
                fragment = ContactusFragment()
                replaceFragment(fragment)
                binding.appBarNavigation.actionbarAccount.toolbarTitle.text="Settings"

            } else if (position == 6) {
                fragment = AboutusFragment()
                replaceFragment(fragment)
                binding.appBarNavigation.actionbarAccount.toolbarTitle.text="About us"

            } else if (position == 7) {
                fragment = AboutusFragment()
                replaceFragment(fragment)

            }
            updateAdpaterColor(position)
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        binding.menulist.adapter = adapter
    }

    public fun updateAdpaterColor(pos: Int) {
        for (i in 0 until adapter!!.itemCount) {
            val datamodel = adapter!!.getItem(i)
            datamodel.checked = pos == i
        }
        adapter!!.notifyDataSetChanged()
    }

    private fun replaceFragment(fragment: Fragment?) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.nav_host_fragment_content_navigation, fragment!!)
        ft.addToBackStack("Navigation")
        ft.commit()
    }

    /**
     * @param title
     */
    /*Update title depends on side menu from application*/
    fun updateTitle(title: String) {
        if (title == getString(R.string.menu_dash)) {
            binding.appBarNavigation.actionbarAccount.icon!!.visibility = View.VISIBLE
            binding.appBarNavigation.actionbarAccount.notify!!.visibility = View.VISIBLE
        } else {
            binding.appBarNavigation.actionbarAccount.notify!!.visibility = View.GONE
            binding.appBarNavigation.actionbarAccount.notificationcount.visibility = View.GONE
        }
        binding.appBarNavigation.actionbarAccount.toolbarTitle!!.text = title
        binding.appBarNavigation.actionbarAccount.toolbarTitle!!.textSize = 16f
    }
}