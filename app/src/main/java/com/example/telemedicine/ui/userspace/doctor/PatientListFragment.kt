package com.example.telemedicine.ui.userspace.doctor

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.telemedicine.AppConstants
import com.example.telemedicine.adapter.PatientListAdapter
import com.example.telemedicine.databinding.FragmentPatientListBinding
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [PatientListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PatientListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var fragmentPatientListBinding: FragmentPatientListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ((context) as DoctorSpace)!!.activityDoctorSpaceBinding.
        bottomNavigation.visibility = View.VISIBLE

        get_consultation_data()


    }

    private fun get_consultation_data()
    {
        val sharedPreference =  requireContext().getSharedPreferences(AppConstants.sharedPrefName, Context.MODE_PRIVATE)

        val userid = sharedPreference.getString(AppConstants.userid,null)


        runBlocking {
            GlobalScope.launch {
                FirebaseDatabase.getInstance().getReference(AppConstants.doctor_coll_name).
                child(userid!!).child(AppConstants.consulation_coll_name).get().addOnCompleteListener {

                    if (it.result.value != null) {

                        val res: HashMap<String, HashMap<String, String>> =
                            it.result.value as HashMap<String, HashMap<String, String>>
                        var patient_uid = mutableListOf<String>()

                        Log.d("patient_uid", patient_uid.toString())

                        val patient_info: MutableCollection<java.util.HashMap<String, String>> =
                            res.values

                        res.keys.forEach {
                            patient_uid.add(it)
                        }


                        var patient_info1: MutableList<HashMap<String, String>> = mutableListOf()

                        patient_info.forEach {
                            patient_info1.add(it)
                        }

                        fragmentPatientListBinding.progressBar6.visibility = View.GONE

                        fragmentPatientListBinding.rvConsulations.layoutManager =
                            LinearLayoutManager(
                                context,
                                LinearLayoutManager.VERTICAL, false
                            )
                        if (isAdded() && patient_info1.size > 0 && patient_uid.size>0) {

                            fragmentPatientListBinding.rvConsulations.adapter =
                                PatientListAdapter(requireActivity(), patient_info1, patient_uid)
                        }

                    }else {
                        fragmentPatientListBinding.progressBar6.visibility = View.GONE
                        fragmentPatientListBinding.tvErrorMessage.visibility = View.VISIBLE
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


        fragmentPatientListBinding = FragmentPatientListBinding.inflate(inflater, container, false)
        return fragmentPatientListBinding.root

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PatientListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PatientListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }






}