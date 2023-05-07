package com.example.telemedicine.ui.userspace.patient

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.telemedicine.AppConstants
import com.example.telemedicine.R
import com.example.telemedicine.databinding.FragmentBookConsulationBinding
import com.example.telemedicine.model.DoctorConsulationReq
import com.example.telemedicine.model.PatientConsulationReq
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "time_slot"
private const val ARG_PARAM2 = "date_slot"

/**
 * A simple [Fragment] subclass.
 * Use the [BookConsulationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BookConsulationFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var time_slot: String? = null
    private var date_slot: String? = null
    private var doctor_userid:String? = null
    private var doctor_name:String? = null
    private var patient_name:String? = null

    private lateinit var _fragmentbookConsulation : FragmentBookConsulationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            time_slot = it.getString(ARG_PARAM1)
            date_slot = it.getString(ARG_PARAM2)
            doctor_userid = it.getString("doctor_uid")
            doctor_name = it.getString("doctor_name")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _fragmentbookConsulation.tvDoctorName.text = "Dr ${doctor_name}"
        _fragmentbookConsulation.tvDate.text = date_slot
        _fragmentbookConsulation.tvTime.text = time_slot

        val sharedPreference =  requireContext().getSharedPreferences(AppConstants.sharedPrefName, Context.MODE_PRIVATE)

        val userid = sharedPreference.getString(AppConstants.userid,null)

        GlobalScope.launch {
            fetch_patient_name(userid!!)
        }

        _fragmentbookConsulation.btnBookAppointment.setOnClickListener {
            _fragmentbookConsulation.progressBar7.visibility = View.VISIBLE
            GlobalScope.launch {
                add_booking_slot(userid!!)
            }
        }

        _fragmentbookConsulation.ivBack.setOnClickListener {

            var fragment = HomeFragment()

            (context as FragmentActivity)!!.supportFragmentManager.
            beginTransaction().
            replace(R.id.root_layout,fragment!!).commit()
        }
    }

    private fun fetch_patient_name(userid: String) {
        FirebaseDatabase.getInstance().getReference(AppConstants.patient_coll_name).child(userid!!).get().addOnCompleteListener{
            var patient_profile = it.result.value as HashMap<String, String> /* = java.util.HashMap<kotlin.String, kotlin.String> */
            patient_name = patient_profile.get("name")
        }
    }

    private fun add_booking_slot(patient_userid:String) {

        var consulationReq = DoctorConsulationReq(patient_name = patient_name!!,time_slot = time_slot!!,date_slot = date_slot!!)

        val hashMap: HashMap<String, DoctorConsulationReq> = hashMapOf() //define empty hashmap
        hashMap.put(patient_userid, consulationReq)


       FirebaseDatabase.getInstance().getReference(AppConstants.doctor_coll_name).child(doctor_userid!!).
       child(AppConstants.consulation_coll_name).
       updateChildren(hashMap as Map<String, Any>).addOnCompleteListener {
          if (it.isSuccessful){
              update_in_patient_collection(patient_userid,doctor_userid!!)
          }
       }

    }

    private fun update_in_patient_collection(patient_userid: String,doctor_userid:String) {

        var consulationReq = PatientConsulationReq(doctor_name = doctor_name!!,time_slot = time_slot!!,date_slot = date_slot!!)
        val hashMap: HashMap<String, PatientConsulationReq> = hashMapOf() //define empty hashmap
        hashMap.put(doctor_userid, consulationReq)

        FirebaseDatabase.getInstance().getReference(AppConstants.patient_coll_name).child(patient_userid).
        child(AppConstants.consulation_coll_name).
        updateChildren(hashMap as Map<String, Any>).addOnCompleteListener {
            if (it.isSuccessful){

                _fragmentbookConsulation.progressBar7.visibility = View.GONE

                Toast.makeText(requireContext(),"Consulation Booked",Toast.LENGTH_LONG).show()

                var fragment = HomeFragment()

                (context as FragmentActivity)!!.supportFragmentManager.
                beginTransaction().replace(R.id.root_layout,fragment!!).commit()

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _fragmentbookConsulation = FragmentBookConsulationBinding.inflate(inflater, container, false)
        return _fragmentbookConsulation.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BookConsulationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BookConsulationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}