package com.example.telemedicine.ui.userspace.doctor

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.telemedicine.AppConstants
import com.example.telemedicine.R
import com.example.telemedicine.databinding.FragmentDoctorProfileBinding
import com.example.telemedicine.ui.registration.RegsitrationActivity
import com.example.telemedicine.viewmodel.DoctorProfileViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DoctorProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DoctorProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var viewModel: DoctorProfileViewModel
    private lateinit var _fragmentDoctorProfileBinding : FragmentDoctorProfileBinding

    private var date_range : ArrayList<String> ? = null

    private var slot_range : ArrayList<String> ? = null

    private var  fragment = EditSlotFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this@DoctorProfileFragment).get(DoctorProfileViewModel::class.java)

        val sharedPreference =  requireContext().getSharedPreferences(AppConstants.sharedPrefName, Context.MODE_PRIVATE)

        val userid = sharedPreference.getString(AppConstants.userid,null)


        viewModel.get_doctor_profile(this,userid!!)

        ((activity) as DoctorSpace)!!.activityDoctorSpaceBinding.
        bottomNavigation.visibility = View.VISIBLE


        _fragmentDoctorProfileBinding.tvLogout.setOnClickListener {

            create_alert_dialog_box("Do you want to Log out ?")
        }



        _fragmentDoctorProfileBinding.btnEditSlots.setOnClickListener {

            ((activity) as DoctorSpace)!!.activityDoctorSpaceBinding.
            bottomNavigation.visibility = View.GONE

            requireActivity().supportFragmentManager.
            beginTransaction().addToBackStack(AppConstants.tag).
            replace(R.id.root_layout,fragment!!).commit()
        }




    }

    fun create_alert_dialog_box(msg:String){
        // build alert dialog
        val dialogBuilder : AlertDialog.Builder = AlertDialog.Builder(requireContext())

        // set message of alert dialog
        dialogBuilder.setMessage(msg)
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton("Proceed", DialogInterface.OnClickListener {
                    dialog, id -> clear_shared_prefs()
            })
            // negative button text and action
            .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                    dialog, id -> dialog.cancel()
            })

        // create dialog box
        val alert = dialogBuilder.create()
        alert.show()
    }


    fun clear_shared_prefs(){
        val sharedPreference =
            requireContext().getSharedPreferences(AppConstants.sharedPrefName, android.content.Context.MODE_PRIVATE)

        val timer = object: CountDownTimer(1600, 100) {
            override fun onTick(millisUntilFinished: Long) {
                _fragmentDoctorProfileBinding.progressBar3.visibility = View.VISIBLE
            }

            override fun onFinish() {
                sharedPreference.edit().clear().apply()
                Toast.makeText(requireContext(),"Logged out successfully",Toast.LENGTH_LONG).show()
                val i = Intent(activity, RegsitrationActivity::class.java)
                startActivity(i)
                (activity as Activity?)!!.overridePendingTransition(0, 0)
            }
        }
        timer.start()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _fragmentDoctorProfileBinding = FragmentDoctorProfileBinding.inflate(inflater, container, false)

       return _fragmentDoctorProfileBinding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DoctorProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DoctorProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun display_in_view(doctorProfile: HashMap<String, String>?) {
        date_range = doctorProfile!!.get("date_range") as ArrayList<String>
        slot_range = doctorProfile!!.get("slot_range") as ArrayList<String> /* = java.util.ArrayList<kotlin.String> */
        set_values()
        _fragmentDoctorProfileBinding.btnEditSlots.isEnabled = true
        _fragmentDoctorProfileBinding.progressBar3.visibility = View.GONE

        var name = doctorProfile.get("name")
        var age =   doctorProfile.get("age")
        var ph_no =  doctorProfile.get("ph_no")
        var reg_no = doctorProfile.get("reg_no")
        var degree = doctorProfile.get("degree")
        var speciality = doctorProfile.get("speciality")
        var experience =  doctorProfile.get("experience")
        var location =  doctorProfile.get("location")

        _fragmentDoctorProfileBinding.progressBar3.visibility = View.GONE

        _fragmentDoctorProfileBinding.tvDoctorName.text = "Dr ${name}"
        _fragmentDoctorProfileBinding.tvDoctorAge.text = age
        _fragmentDoctorProfileBinding.tvPhNo.text = ph_no
        _fragmentDoctorProfileBinding.tvDoctorRegistration.text = reg_no
        _fragmentDoctorProfileBinding.tvDoctorDegree.text = "${degree} (${speciality})"
        _fragmentDoctorProfileBinding.tvDoctorExperience.text = experience
        _fragmentDoctorProfileBinding.tvLocation.text = location

    }

    private fun set_values() {
        val bundle = Bundle()

        bundle.putStringArrayList("date_range", date_range)

        bundle.putStringArrayList("slot_range", slot_range)

        fragment.arguments = bundle
    }
}