package com.example.telemedicine.ui.userspace.patient

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fooddeliveryapp.adapter.ConsulationsAdapter
import com.example.telemedicine.AppConstants
import com.example.telemedicine.R
import com.example.telemedicine.adapter.DoctorConsultationListAdapter
import com.example.telemedicine.adapter.DoctorListAdapter
import com.example.telemedicine.databinding.FragmentAppointmentBinding
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AppointmentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AppointmentFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var _fragmentAppointmentBinding : FragmentAppointmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        /*** To be done ***/

//        ((context) as PatientSpace)!!.patientSpaceBinding.
//        bottomNavigation.selectedItemId = R.id.nav_appointments

        ((context) as PatientSpace)!!.patientSpaceBinding.
        bottomNavigation.visibility = View.VISIBLE

        get_consulation_data()

//        _fragmentAppointmentBinding.rvConsulations.adapter = ConsulationsAdapter(this@AppointmentFragment,)
    }

    private fun get_consulation_data() {

        val sharedPreference =  requireContext().getSharedPreferences(AppConstants.sharedPrefName, Context.MODE_PRIVATE)

        val userid = sharedPreference.getString(AppConstants.userid,null)


        runBlocking {
            GlobalScope.launch {
                FirebaseDatabase.getInstance().getReference(AppConstants.patient_coll_name).
                child(userid!!).child(AppConstants.consulation_coll_name).get().addOnCompleteListener {

                    if (it.result.value != null) {

                        val res: HashMap<String, HashMap<String, String>> =
                            it.result.value as HashMap<String, HashMap<String, String>>
                        val doctor_uid = mutableListOf<String>()

                        Log.d("doctor_uid", doctor_uid.toString())

                        val doc_info: MutableCollection<java.util.HashMap<String, String>> =
                            res.values


                        var doc_info1: MutableList<HashMap<String, String>> = mutableListOf()

                        doc_info.forEach {
                            doc_info1.add(it)
                        }

                        _fragmentAppointmentBinding.progressBar.visibility = View.GONE

                        _fragmentAppointmentBinding.rvConsulations.layoutManager =
                            LinearLayoutManager(
                                context,
                                LinearLayoutManager.VERTICAL, false
                            )
                        if(isAdded())
                        {

                            _fragmentAppointmentBinding.rvConsulations.adapter =
                                DoctorConsultationListAdapter(requireActivity(), doc_info1)
                        }

                    }else {
                        _fragmentAppointmentBinding.progressBar.visibility = View.GONE
                        _fragmentAppointmentBinding.tvErrorMessage.visibility = View.VISIBLE
                    }
                }
            }
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _fragmentAppointmentBinding = FragmentAppointmentBinding.inflate(inflater, container, false)
        return _fragmentAppointmentBinding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AppointmentFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AppointmentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}