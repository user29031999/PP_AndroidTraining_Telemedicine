package com.example.telemedicine.ui.userspace.patient

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fooddeliveryapp.adapter.CalendarAdapter
import com.example.telemedicine.AppConstants
import com.example.telemedicine.R
import com.example.telemedicine.databinding.FragmentSelectAppointmentBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SelectAppointmentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SelectAppointmentFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var date_range : ArrayList<String> ? = null

    private var slot_range : ArrayList<String> ? = null

    private var temp_date_ranges  = mutableListOf<String>()

    private var all_date_ranges  = mutableListOf<Int>()

    private var all_time_ranges  = mutableListOf<String>()

    private var doctor_userid : String? = null

    private var doctor_name : String? = null

    private lateinit var _fragmentSelectAppointment : FragmentSelectAppointmentBinding

    private var date_count = 0

    val c = Calendar.getInstance()

    val monthName = arrayOf(
        "January", "February", "March", "April", "May", "June", "July",
        "August", "September", "October", "November",
        "December"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

            date_range = it.getStringArrayList("date_range")

            slot_range = it.getStringArrayList("slot_range")

            doctor_userid = it.getString("doctor_uid")

            doctor_name = it.getString("doctor_name")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*** requirement of the date ranges and time slots to be taken from the bundle intent ***/

        add_dates()

        add_slots()

        assign_date_count()

        /*** Date addition and subtraction bug needed to be fixed ***/


        _fragmentSelectAppointment.ivNxtMonth.setOnClickListener{
            date_count = date_count+1
            assign_date_count()
        }

        _fragmentSelectAppointment.ivPrevMonth.setOnClickListener{
            date_count = date_count-1
            assign_date_count()
        }


        _fragmentSelectAppointment.ivBack.setOnClickListener {

            var fragment = HomeFragment()

            (context as FragmentActivity)!!.supportFragmentManager.beginTransaction()
                .replace(R.id.root_layout, fragment!!).commit()
        }

    }

    private fun assign_date_count() {
        if (date_count>=0 && date_count<=all_date_ranges.size-1) {
            val month = monthName[c[Calendar.MONTH]]
            val year = c[Calendar.YEAR]
            _fragmentSelectAppointment.monthYearTV.text =
                "${all_date_ranges[date_count].toString()} ${month} ${year}"
        }
    }

    private fun add_slots() {
        slot_range!!.forEach{
            time_range(it.split("-"))
        }
    }

    private fun time_range(time: List<String>) {
       runBlocking {
       val job =  GlobalScope.launch {

            try {
                var d: Date = SimpleDateFormat("HH:mm aaa").parse(time[0])
                var d1 : Date = SimpleDateFormat("HH:mm aaa").parse(time[1])
                var dateFormat1 = SimpleDateFormat("HH:mm aaa")
                var date = dateFormat1.format(d)
                var date1 = dateFormat1.format(d1)
                var calendar = Calendar.getInstance()
                calendar.time = dateFormat1.parse(date)
                val strings1 = date1.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                while (true){
                    calendar.add(Calendar.MINUTE, 30)
                    var one = dateFormat1.format(calendar.time)
                    var slot = one
                    val strings = date.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    one = strings[0] + strings[1] + "-" + one
                    println("Time here $one")

                    val format = SimpleDateFormat("HH:mm")
                    val date1 = format.parse(time[1])
                    val date2 = format.parse(slot)

                    println("Dates $date1 and $date2")

                    val difference = date2.time - date1.time

                    if (difference>=0){
                        var op: String = strings[0] + strings[1] + "-" + strings1[0]+strings1[1]
                        println("Equal ele $op")
                        all_time_ranges.add(op)
                        break
                    }

                    all_time_ranges.add(one)

                    d = SimpleDateFormat("HH:mm aaa").parse(slot)
                    dateFormat1 = SimpleDateFormat("HH:mm aaa")
                    date = dateFormat1.format(d)
                    calendar.time = dateFormat1.parse(date)
                }


            } catch (e: ParseException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

            println(all_time_ranges.toString())
        }

           job.join()

           val calendarAdapter = CalendarAdapter(requireContext(), all_time_ranges as ArrayList<String>,
               _fragmentSelectAppointment.monthYearTV,doctor_userid,doctor_name)
           val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(requireContext(), 3)
           _fragmentSelectAppointment.rvTimeSlots.setLayoutManager(layoutManager)
           _fragmentSelectAppointment.rvTimeSlots.setAdapter(calendarAdapter)

       }



    }

    private fun add_dates() {
        runBlocking {
            val job1 = GlobalScope.launch {
                date_range!!.forEach {
                    it.split("–").forEach {
                        /// considering current month

                        val now: Calendar = Calendar.getInstance()

                        val month = now.get(Calendar.MONTH)

                        val month_name = AppConstants.monthName[month]

                        var date_ranges = it.replace(month_name, "").toString().replace(" ", "")

                        temp_date_ranges.add(date_ranges)
                    }
                }

                for (x in 0..temp_date_ranges.size - 1 step 2) {
                    var first: Int = temp_date_ranges[x].toInt()
                    var sec: Int = temp_date_ranges[x + 1].toInt()
                    for (y in first..sec) {
                        all_date_ranges.add(y)
                    }
                }
            }

            job1.join()
        }

        System.out.println(all_date_ranges.toString())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _fragmentSelectAppointment =  FragmentSelectAppointmentBinding.inflate(inflater, container, false)
        return _fragmentSelectAppointment.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SelectAppointmentFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SelectAppointmentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}