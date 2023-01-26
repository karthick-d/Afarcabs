package com.wings.mile.ui.home


import android.Manifest
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri

import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.core.app.ActivityCompat
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.wings.mile.databinding.FragmentHomeBinding
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit


class HomeFragment : Fragment(), OnMapReadyCallback {


    internal lateinit var mLastLocation: Location
    internal lateinit var mLocationResult: LocationRequest
    internal lateinit var mLocationCallback: LocationCallback
    internal var mCurrLocationMarker: Marker? = null
    internal var mGoogleApiClient: GoogleApiClient? = null
    internal lateinit var mLocationRequest: LocationRequest
    internal var mFusedLocationClient: FusedLocationProviderClient? = null

    private var _binding: FragmentHomeBinding? = null
    private lateinit var mMap: GoogleMap
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    private var marker: Marker? = null

    private val points: Queue<LatLng> = LinkedList()

    private var animatorSet = AnimatorSet()
    private var fusedLocationProvider: FusedLocationProviderClient? = null
    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 10
        fastestInterval = 10
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        maxWaitTime = 20
    }

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                //The last location in the list is the newest
                val location = locationList.last()
                mMap.isMyLocationEnabled = true
//
//                //enable location button
                mMap.uiSettings.isMyLocationButtonEnabled = true
                mMap.uiSettings.isCompassEnabled = false
                startLocationUpdates()
                SubscribetoTimer()
                Log.e("location--->",""+location.toString())

//                Toast.makeText(
//                    context!!,
//                    "Got Location: " + location.toString(),
//                    Toast.LENGTH_LONG
//                )
//                    .show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val manager: FragmentManager? = fragmentManager
        val transaction: FragmentTransaction = manager!!.beginTransaction()
        val fragment = SupportMapFragment()
        transaction.add(com.wings.mile.R.id.map, fragment)
        transaction.commit()

        fragment.getMapAsync(this)

        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        val root: View = binding.root

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(context!!)

        checkLocationPermission()

        return binding!!.root
    }
        override fun onDestroyView() {
        super.onDestroyView()
        _binding = null


    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        fusedLocationProvider?.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
//        mMap.uiSettings.isMyLocationButtonEnabled = true
//        mMap.uiSettings.isCompassEnabled = false
//        startLocationUpdates()
//        SubscribetoTimer()
//        mMap = googleMap
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(
//                    context!!,
//                    Manifest.permission.ACCESS_FINE_LOCATION
//                ) == PackageManager.PERMISSION_GRANTED &&
//                ContextCompat.checkSelfPermission(
//                    context!!,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//                ) == PackageManager.PERMISSION_GRANTED
//            ) {
//
//                // mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
//                mMap.isMyLocationEnabled = true
//
//                //enable location button
//                mMap.uiSettings.isMyLocationButtonEnabled = true
//                mMap.uiSettings.isCompassEnabled = false
//                //Registering the listener to update location for every 100m displacement
//                startLocationUpdates()
//                //getting the latest point from the queue for every 5 seconds
//                SubscribetoTimer()
//            } else {
//                checkLocationPermission()
//            }
//        }
    }


    // Trigger new location updates at interval
    @SuppressLint("MissingPermission")
    protected fun startLocationUpdates() {

        // Create the location request to start receiving updates
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.smallestDisplacement = 100f
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0

        // Create LocationSettingsRequest object using location request
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest)
        val locationSettingsRequest = builder.build()

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        settingsClient.checkLocationSettings(locationSettingsRequest)

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        LocationServices.getFusedLocationProviderClient(requireActivity()).requestLocationUpdates(
            mLocationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    points.add(
                        LatLng(
                            locationResult.lastLocation!!.latitude, locationResult.lastLocation!!
                                .longitude
                        )
                    )
                }
            },
            Looper.myLooper()
        )
    }
    private fun SubscribetoTimer() {
        Observable.interval(5, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { SendNextPoints() }
    }


    private fun SendNextPoints() {
        if (!animatorSet.isRunning && !points.isEmpty()) UpdateMarker(points.poll()) // taking the points f rom head of the queue.
    }

    private fun UpdateMarker(newlatlng: LatLng) {
        if (marker != null) {
            val bearingangle = Calculatebearingagle(newlatlng)
            marker!!.setAnchor(0.5f, 0.5f)
            animatorSet = AnimatorSet()
            animatorSet.playTogether(
                rotateMarker(
                    (bearingangle -1),
                    marker!!.getRotation()
                ), moveVechile(newlatlng, marker!!.getPosition())
            )
            animatorSet.start()
        } else {
            AddMarker(newlatlng)
        }
        mMap.animateCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder().target(newlatlng)
                    .zoom(16f).build()
            )
        )
    }


    private fun AddMarker(initialpos: LatLng) {
        val markerOptions = MarkerOptions().position(initialpos).flat(true)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        marker = mMap.addMarker(markerOptions)
    }

    private fun Calculatebearingagle(newlatlng: LatLng): Float {
        val destinationLoc = Location("service Provider")
        val userLoc = Location("service Provider")
        userLoc.latitude = marker!!.position.latitude
        userLoc.longitude = marker!!.position.longitude
        destinationLoc.latitude = newlatlng.latitude
        destinationLoc.longitude = newlatlng.longitude
        return userLoc.bearingTo(destinationLoc)
    }

    @Synchronized
    fun rotateMarker(toRotation: Float, startRotation: Float): ValueAnimator? {
        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.duration = 1555
        valueAnimator.addUpdateListener { valueAnimator ->
            val t = valueAnimator.animatedValue.toString().toFloat()
            val rot = t * toRotation + (1 - t) * startRotation
            marker!!.rotation = if (-rot > 180) rot / 2 else rot
        }
        return valueAnimator
    }


    @Synchronized
    fun moveVechile(finalPosition: LatLng, startPosition: LatLng): ValueAnimator? {
        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.duration = 3000
        valueAnimator.addUpdateListener { valueAnimator ->
            val t = valueAnimator.animatedValue.toString().toFloat()
            val currentPosition = LatLng(
                startPosition.latitude * (1 - t) + finalPosition.latitude * t,
                startPosition.longitude * (1 - t) + finalPosition.longitude * t
            )
            marker!!.position = currentPosition
        }
        return valueAnimator
    }

    /**
     * Get permissions for our app if they didn't previously exist.
     * requestCode -> the number assigned to the request that we've made.
     * Each request has it's own unique request code.
     */
    private fun checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) &&
                ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) {
                AlertDialog.Builder(requireActivity())
                    .setTitle("give permission")
                    .setMessage("give permission message")
                    .setPositiveButton(
                        "OK"
                    ) { dialogInterface: DialogInterface?, i: Int ->
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,

                            ),
                            1
                        )
                    }
                    .create()
                    .show()
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,

                    ),
                    1
                )
            }
        }
    }
    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {

            fusedLocationProvider?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    override fun onPause() {
        super.onPause()
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {

            fusedLocationProvider?.removeLocationUpdates(locationCallback)
        }
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(requireActivity())
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        //Prompt the user once explanation has been shown
                        requestLocationPermission()
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                requestLocationPermission()
            }
        } else {
            checkBackgroundLocation()
        }
    }

    private fun checkBackgroundLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestBackgroundLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            MY_PERMISSIONS_REQUEST_LOCATION
        )
    }

    private fun requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION
            )
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            context!!,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        fusedLocationProvider?.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.getMainLooper()
                        )
                         startLocationUpdates()
                        SubscribetoTimer()
                        // Now check background location
                        //checkBackgroundLocation()
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(context!!, "permission denied", Toast.LENGTH_LONG).show()

                    // Check if we are in a state where the user has denied the permission and
                    // selected Don't ask again
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            requireActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", requireActivity().packageName, null),
                            ),
                        )
                    }
                }
                return
            }
            MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            context!!,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        fusedLocationProvider?.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.getMainLooper()
                        )

                        Toast.makeText(
                            context!!,
                            "Granted Background Location Permission",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(context!!, "permission denied", Toast.LENGTH_LONG).show()
                }
                return

            }
        }
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
        private const val MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION = 66
    }
}
