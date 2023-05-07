package com.example.telemedicine.ui.userspace.doctor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.example.telemedicine.AppConstants
import com.example.telemedicine.R
import com.example.telemedicine.databinding.FragmentViewDoctorProfileBinding
import com.example.telemedicine.databinding.FragmentViewPatientProfileBinding
import com.example.telemedicine.ui.userspace.patient.PatientSpace
import com.example.telemedicine.ui.userspace.patient.ReportsFragment
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ViewPatientProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ViewPatientProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var patient_user_id : String? = null
    private lateinit var _viewPatientProfileBinding:FragmentViewPatientProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            patient_user_id = it.getString("patient_user_id")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _viewPatientProfileBinding.ivBack.setOnClickListener {

            var fragment = PatientListFragment()

            (context as FragmentActivity)!!.supportFragmentManager.
            beginTransaction().
            replace(R.id.root_layout,fragment!!).commit()
        }

        _viewPatientProfileBinding.tvMyReports.setOnClickListener {
            var fragment = ReportsFragment()

            val bundle = Bundle()

            bundle.putSerializable("patient_uid",patient_user_id)

            fragment.arguments = bundle

            ((context) as DoctorSpace)!!.activityDoctorSpaceBinding.
            bottomNavigation.visibility = View.GONE

            (context as FragmentActivity)!!.supportFragmentManager.
            beginTransaction().addToBackStack(AppConstants.tag).
            replace(R.id.root_layout,fragment!!).commit()
        }


        GlobalScope.launch {
            get_patient_data()
        }
    }

    private fun get_patient_data() {
        FirebaseDatabase.getInstance().getReference(AppConstants.patient_coll_name).child(patient_user_id!!).get().addOnCompleteListener{
            var patientProfile = it.result.value as HashMap<String, String>
        /* = java.util.HashMap<kotlin.String, kotlin.String> */
            var name = patientProfile?.get("name")
            var age = patientProfile?.get("age")
            var blood_group = patientProfile?.get("blood_group")
            var gender = patientProfile?.get("gender")
            var phone = patientProfile?.get("phone")
            var med_history = patientProfile?.get("med_history")

            _viewPatientProfileBinding.progressBar.visibility = View.GONE

            _viewPatientProfileBinding.tvPatientName.text = name
            _viewPatientProfileBinding.tvPatientAge.text = age
            _viewPatientProfileBinding.tvPatientPhone.text = phone
            _viewPatientProfileBinding.tvPatientBloodGroup.text = blood_group
            _viewPatientProfileBinding.tvPatientGender.text = gender
            _viewPatientProfileBinding.tvPatientMedicalHistory.text = med_history
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _viewPatientProfileBinding =  FragmentViewPatientProfileBinding.
        inflate(inflater, container, false)
        return _viewPatientProfileBinding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ViewPatientProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ViewPatientProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}