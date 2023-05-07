package com.example.telemedicine.ui.userspace.patient

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.telemedicine.AppConstants
import com.example.telemedicine.adapter.DoctorListAdapter
import com.example.telemedicine.databinding.FragmentHomeBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var doctor_information : MutableList<HashMap<String,String>> = mutableListOf()

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var permissionId : Int = 101
    private var curr_location : String?=""
    private lateinit var _fragmentHomeBinding: FragmentHomeBinding
    private var doctor_uid = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ((context) as PatientSpace)!!.patientSpaceBinding.bottomNavigation.visibility = View.VISIBLE

        _fragmentHomeBinding.searchView.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString()!!.isNullOrEmpty()){
                    _fragmentHomeBinding.progressBar5.visibility = View.GONE
                    _fragmentHomeBinding.textView3.visibility = View.VISIBLE
                    _fragmentHomeBinding.rvDoctorList.visibility = View.VISIBLE
                    _fragmentHomeBinding.tvErrorMessage.visibility = View.GONE
                    _fragmentHomeBinding.textView3.text = "List of Doctors in ${curr_location}"
                    filter_with_doctor_info(curr_location!!)
                }else {



                        val timer = object : CountDownTimer(1000, 100) {
                            override fun onTick(millisUntilFinished: Long) {
                                _fragmentHomeBinding.progressBar5.visibility = View.VISIBLE
                            }

                            override fun onFinish() {

                                if (p0.toString().isNotEmpty()) {

                                        _fragmentHomeBinding.textView3.text =
                                            "List of Doctors in ${p0.toString()}"
                                        Log.d("query_text", p0.toString())
                                        filter_with_doctor_info(p0.toString())
                                }else {
                                    _fragmentHomeBinding.progressBar5.visibility = View.GONE
                                }

                            }
                        }
                        timer.start()

                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        get_doctors()

    }

    private fun filter_with_doctor_info(query: String) {

        val filtered_doctor_uid = mutableListOf<String>()

        val filtered_doctor_information : MutableList<HashMap<String,String>> = mutableListOf()


        for (i in 0..doctor_information.size-1){
            if (doctor_information.get(i).get("location") == query){
                filtered_doctor_uid.add(doctor_uid.get(i))
                filtered_doctor_information.add(doctor_information.get(i))
            }
        }

        _fragmentHomeBinding.progressBar5.visibility = View.GONE
        _fragmentHomeBinding.rvDoctorList.layoutManager =
            LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL, false
            )
        if (filtered_doctor_information.size > 0 && filtered_doctor_uid.size>0) {

            _fragmentHomeBinding.rvDoctorList.visibility = View.VISIBLE
            _fragmentHomeBinding.textView3.visibility = View.VISIBLE
            _fragmentHomeBinding.tvErrorMessage.visibility = View.GONE
            _fragmentHomeBinding.progressBar5.visibility = View.GONE

            if (isAdded()) {
                _fragmentHomeBinding.rvDoctorList.adapter =
                    DoctorListAdapter(requireActivity(), filtered_doctor_information, filtered_doctor_uid)
            }
        }else {
            _fragmentHomeBinding.rvDoctorList.visibility = View.GONE
            _fragmentHomeBinding.textView3.visibility = View.GONE
            _fragmentHomeBinding.tvErrorMessage.visibility = View.VISIBLE
            _fragmentHomeBinding.progressBar5.visibility = View.GONE
        }


    }

    private fun get_doctors() {

        runBlocking {
            val loc_job = GlobalScope.launch {
                getLocation()
            }
            loc_job.join()
            if (checkPermissions()) {
                val job1 = GlobalScope.launch {
                    FirebaseDatabase.getInstance().getReference(AppConstants.doctor_coll_name).get()
                        .addOnCompleteListener {

                            if (it.isSuccessful) {

                                if (it.result.value != null){

                                val res: HashMap<String, HashMap<String, String>> =
                                    it.result.value as HashMap<String, HashMap<String, String>>

                                val filtered_doctor_uid = mutableListOf<String>()

                                res.keys.forEach {
                                    doctor_uid.add(it)
                                }

                                Log.d("doctor_uid", doctor_uid.toString())


                                val doc_info: MutableCollection<java.util.HashMap<String, String>> =
                                    res.values

                                val filtered_doctor_information : MutableList<HashMap<String,String>> = mutableListOf()

                                doc_info.forEach {
                                    doctor_information.add(it)
                                }

                                for (i in 0..doctor_information.size-1){
                                   if (doctor_information.get(i).get("location") == curr_location){
                                      filtered_doctor_uid.add(doctor_uid.get(i))
                                       filtered_doctor_information.add(doctor_information.get(i))
                                   }
                                }

                                _fragmentHomeBinding.progressBar5.visibility = View.GONE

                                _fragmentHomeBinding.rvDoctorList.layoutManager =
                                    LinearLayoutManager(
                                        context,
                                        LinearLayoutManager.VERTICAL, false
                                    )

                                if (filtered_doctor_information.size > 0 && filtered_doctor_uid.size>0) {

                                    if (isAdded()) {

                                        _fragmentHomeBinding.rvDoctorList.adapter =
                                            DoctorListAdapter(requireActivity(), filtered_doctor_information, filtered_doctor_uid)
                                    }
                                }else {
                                    _fragmentHomeBinding.tvErrorMessage.visibility = View.VISIBLE
                                    _fragmentHomeBinding.progressBar5.visibility = View.GONE
                                }

                            }else {

                                        _fragmentHomeBinding.tvErrorMessage.visibility = View.VISIBLE
                                        _fragmentHomeBinding.progressBar5.visibility = View.GONE

                            }
                            }

                        }
                }

                job1.join()
            }
        }
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
              mFusedLocationClient.lastLocation.addOnCompleteListener {
                  val location: Location? = it.result

                  if (location != null) {

                      val geocoder = Geocoder(requireContext(), Locale.getDefault())

                      val list: List<Address> =
                          geocoder.getFromLocation(location.latitude, location.longitude, 1)

                      curr_location = list.get(0).locality.toString()
                      _fragmentHomeBinding.textView3.text = _fragmentHomeBinding.textView3.text.toString()+" "+curr_location

                      println("Current location is $curr_location")

                  }

              }
            } else {
                Toast.makeText(requireContext(), "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            Permissions()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }
    private fun Permissions() {
        requestPermissions(

            arrayOf(

                Manifest.permission.ACCESS_FINE_LOCATION,

                Manifest.permission.ACCESS_COARSE_LOCATION

            ),

            permissionId

        )
    }
    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                get_doctors()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)

        return _fragmentHomeBinding.root

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}