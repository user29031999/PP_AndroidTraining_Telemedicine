package com.example.telemedicine.ui.userspace.patient

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fooddeliveryapp.adapter.MedicalReportsAdapter
import com.example.fooddeliveryapp.adapter.MyReportsAdapter
import com.example.telemedicine.AppConstants
import com.example.telemedicine.R
import com.example.telemedicine.databinding.FragmentAppointmentBinding
import com.example.telemedicine.databinding.FragmentReportsBinding
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ReportsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReportsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var patient_uid:String? = null
    private lateinit var _binding: FragmentReportsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            patient_uid = it.getString("patient_uid")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        GlobalScope.launch {
            get_all_prescription_data()
        }
    }

    private fun get_all_prescription_data() {
        FirebaseDatabase.getInstance().getReference(AppConstants.patient_coll_name).
        child(patient_uid!!)
            .child(AppConstants.reports_coll_name).get().addOnCompleteListener {
                if (it.result.value != null ){
                    val value = it.result.value as ArrayList<String>

                    _binding.progressBar11.visibility = View.GONE

                    val calendarAdapter = MyReportsAdapter(value)
                    val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
                    _binding.rvPrescriptionList.setLayoutManager(layoutManager)
                    _binding.rvPrescriptionList.setAdapter(calendarAdapter)

                }else {
                    _binding.progressBar11.visibility = View.GONE
                    _binding.tvErrorMessage.visibility = View.VISIBLE
                }

            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        return _binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ReportsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ReportsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}