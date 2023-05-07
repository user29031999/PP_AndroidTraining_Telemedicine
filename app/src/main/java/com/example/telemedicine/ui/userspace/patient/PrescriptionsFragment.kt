package com.example.telemedicine.ui.userspace.patient

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.telemedicine.AppConstants
import com.example.telemedicine.adapter.PrescriptionAdapter
import com.example.telemedicine.databinding.FragmentPrescriptionsBinding
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
 * Use the [PrescriptionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PrescriptionsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var _fragmentPrescriptionBinding : FragmentPrescriptionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getPrescriptionData()
    }

    private fun getPrescriptionData() {

        val sharedPreference =  requireContext().getSharedPreferences(AppConstants.sharedPrefName, Context.MODE_PRIVATE)

        val userid = sharedPreference.getString(AppConstants.userid,null)

        runBlocking {
            GlobalScope.launch {
                FirebaseDatabase.getInstance().
                getReference(AppConstants.patient_coll_name).child(userid!!).
                child(AppConstants.presciption_coll_name).get().addOnCompleteListener{
                    if (it.result.value != null) {
                        val presciption_data = it.result.value as HashMap<String, String>
                        var vals: MutableCollection<HashMap<String, String>> =
                            presciption_data.values as MutableCollection<HashMap<String, String> /* = java.util.HashMap<kotlin.String, kotlin.String> */>
                        val pres_data: MutableList<HashMap<String, String>> = mutableListOf()
                        vals.forEach {
                            pres_data.add(it)
                        }

                        if (isAdded()) {
                            _fragmentPrescriptionBinding.rvPrescriptionList.layoutManager =
                                LinearLayoutManager(
                                    context,
                                    LinearLayoutManager.VERTICAL, false
                                )

                            _fragmentPrescriptionBinding.rvPrescriptionList.adapter =
                                PrescriptionAdapter(
                                    requireActivity(),
                                    pres_data,
                                    _fragmentPrescriptionBinding.progressBar5
                                )
                        }
                    }else {

                        _fragmentPrescriptionBinding.progressBar5.visibility = View.GONE
                        _fragmentPrescriptionBinding.tvErrorMessage.visibility = View.VISIBLE

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
        _fragmentPrescriptionBinding = FragmentPrescriptionsBinding.inflate(inflater,container,false)
        return _fragmentPrescriptionBinding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PrescriptionsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PrescriptionsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}