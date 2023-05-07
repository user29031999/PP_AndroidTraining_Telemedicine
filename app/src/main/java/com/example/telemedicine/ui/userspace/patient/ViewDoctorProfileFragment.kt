package com.example.telemedicine.ui.userspace.patient

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.example.telemedicine.AppConstants
import com.example.telemedicine.R
import com.example.telemedicine.databinding.FragmentViewDoctorProfileBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ViewDoctorProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

/***Mukesh Jha***/

class ViewDoctorProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var doctor_info : HashMap<String,String>? = null
    private var doctor_userid:String? = null

    private lateinit var _fragmentViewDoctorProfile : FragmentViewDoctorProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            doctor_userid = it.getString("doctor_uid")
            doctor_info = it.getSerializable("doctor_info") as HashMap<String, String> /* = java.util.HashMap<kotlin.String, kotlin.String> */
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (doctor_info != null) {
            _fragmentViewDoctorProfile.tvDoctorName.text = "Dr " + doctor_info!!.get("name")
            _fragmentViewDoctorProfile.tvDoctorAge.text = doctor_info!!.get("age")
            _fragmentViewDoctorProfile.tvDoctorRegistration.text = doctor_info!!.get("reg_no")
            _fragmentViewDoctorProfile.tvPhNo.text = doctor_info!!.get("ph_no")
            _fragmentViewDoctorProfile.tvDoctorDegree.text = doctor_info!!.get("degree")
            _fragmentViewDoctorProfile.tvSpecialization.text = doctor_info!!.get("speciality")
            _fragmentViewDoctorProfile.tvDoctorExperience.text = doctor_info!!.get("experience")
            _fragmentViewDoctorProfile.tvLocation.text = doctor_info!!.get("location")



            _fragmentViewDoctorProfile.btnBookAppointment.setOnClickListener {

                var slot_range = doctor_info!!.get("slot_range") as ArrayList<String>


                var date_range = doctor_info!!.get("date_range") as ArrayList<String>


                var fragment = SelectAppointmentFragment()

                val bundle = Bundle()

                bundle.putStringArrayList("date_range", date_range)

                bundle.putStringArrayList("slot_range", slot_range)

                bundle.putString("doctor_uid", doctor_userid)

                bundle.putString("doctor_name", doctor_info!!.get("name"))

                fragment.arguments = bundle

                (context as FragmentActivity)!!.supportFragmentManager.beginTransaction()
                    .addToBackStack(AppConstants.tag).replace(R.id.root_layout, fragment!!).commit()


            }

            _fragmentViewDoctorProfile.ivBack.setOnClickListener {

                var fragment = HomeFragment()

                (context as FragmentActivity)!!.supportFragmentManager.beginTransaction()
                    .replace(R.id.root_layout, fragment!!).commit()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         _fragmentViewDoctorProfile = FragmentViewDoctorProfileBinding.inflate(inflater,container, false)
        return _fragmentViewDoctorProfile.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ViewDoctorProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ViewDoctorProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}