package com.example.telemedicine.ui.userspace.doctor

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.util.Pair
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.telemedicine.AppConstants
import com.example.telemedicine.R
import com.example.telemedicine.adapter.ChipsAdapter
import com.example.telemedicine.databinding.FragmentEditSlotBinding
import com.example.telemedicine.ui.userspace.patient.HomeFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "date_range"
private const val ARG_PARAM2 = "slot_range"

/**
 * A simple [Fragment] subclass.
 * Use the [EditSlotFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditSlotFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var date_range: ArrayList<String>? = null
    private var slot_range: ArrayList<String>? = null

    private lateinit var _editSlotBinding:FragmentEditSlotBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            date_range = it.getStringArrayList(ARG_PARAM1)
            slot_range = it.getStringArrayList(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("date_range",date_range.toString())
        Log.d("slot_range",slot_range.toString())

        _editSlotBinding.rvCalendarRange.adapter = ChipsAdapter(requireActivity(),"edit_date",date_range!!)

       _editSlotBinding.rvCalendarRange.layoutManager = LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL,false)

        _editSlotBinding.rvTimeRange.adapter = ChipsAdapter(requireActivity(),"edit_time",slot_range!!)

        _editSlotBinding.rvTimeRange.layoutManager = LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL,false)


        _editSlotBinding.btnSave.setOnClickListener {
            _editSlotBinding.progressBar4.visibility = View.VISIBLE
            save_to_firebase()
        }

        _editSlotBinding.ivBack.setOnClickListener {
            var fragment = DoctorProfileFragment()

            (context as FragmentActivity)!!.supportFragmentManager.
            beginTransaction().
            replace(R.id.root_layout,fragment!!).commit()
        }

        _editSlotBinding.tvAddDate.setOnClickListener {
           showDatePickerDialog()
        }


    }

    private fun showDatePickerDialog() {

        val builder : MaterialDatePicker.Builder<Pair<Long, Long>> = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Select Consulation Dates")

        val today = MaterialDatePicker.todayInUtcMilliseconds()
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

        calendar.timeInMillis = today
        calendar[Calendar.MONTH] = Calendar.getInstance().get(Calendar.MONTH)
        val decThisYear = calendar.timeInMillis

        val constraintsBuilder : CalendarConstraints =
            CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())
                .setEnd(decThisYear).build()

        builder.setCalendarConstraints(constraintsBuilder)

        val picker = builder.build()
        picker.show(requireActivity().supportFragmentManager!!, picker.toString())

        picker.addOnPositiveButtonClickListener {
            var date_range :String = picker.headerText.toString()
            this.date_range!!.add(date_range)
            _editSlotBinding.rvCalendarRange.adapter!!.notifyDataSetChanged()
        }

    }

    private fun save_to_firebase() {
        val sharedPreference =  requireContext().getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val userid = sharedPreference.getString("userid",null)

        val hashMap: HashMap<String, ArrayList<String>> = HashMap<String, ArrayList<String>>() //define empty hashmap
        hashMap.put("date_range", date_range!!)
        hashMap.put("slot_range", slot_range!!)

        FirebaseDatabase.getInstance().getReference(AppConstants.doctor_coll_name).
        child(userid!!).updateChildren(hashMap as Map<String,ArrayList<String>>).addOnCompleteListener {
            it.apply {
                if (isSuccessful){
                    _editSlotBinding.progressBar4.visibility = View.GONE
                    Toast.makeText(requireContext(),"Calendar Updated Successfully",Toast.LENGTH_LONG).show()
                }else {
                    _editSlotBinding.progressBar4.visibility = View.GONE
                    Toast.makeText(requireContext(),"Calendar Updation Failed",Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _editSlotBinding = FragmentEditSlotBinding.inflate(inflater,container,false)
        return _editSlotBinding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditSlotFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditSlotFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}