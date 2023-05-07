package com.example.fooddeliveryapp.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.telemedicine.AppConstants
import com.example.telemedicine.R
import com.example.telemedicine.ui.userspace.patient.BookConsulationFragment
import com.example.telemedicine.ui.userspace.patient.PatientSpace
import com.example.telemedicine.ui.userspace.patient.ViewDoctorProfileFragment

class CalendarAdapter(
    var mContext: Context,
    var op: ArrayList<String>,
    var date: TextView,
    var doctor_userid: String?,
    var doctor_name: String?
) :
    RecyclerView.Adapter<CalendarAdapter.DoctorViewHolder>(){


   inner class DoctorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(time_slot: String) {
            with(itemView){

                var tv_day  : TextView = this.findViewById(R.id.tv_slot) as TextView
                var cv_root : CardView = this.findViewById(R.id.cv_root) as CardView

                tv_day.text = time_slot

                cv_root.setOnClickListener {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            cv_root.setBackgroundColor(mContext.getColor(R.color.color_screen_background))
                    }

                    var fragment = BookConsulationFragment()

                    val bundle = Bundle()

                    bundle.putString("time_slot",time_slot)

                    bundle.putString("date_slot",date.text.toString())

                    bundle.putString("doctor_uid",doctor_userid)

                    bundle.putString("doctor_name",doctor_name)

                    fragment.arguments = bundle

                    (context as FragmentActivity)!!.supportFragmentManager.
                    beginTransaction().addToBackStack(AppConstants.tag).
                    replace(R.id.root_layout,fragment!!).commit()

                }

            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.slot_item, parent, false)
        return DoctorViewHolder(v)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
            var sel : String =  op[position]
            holder.bind(sel)
    }

    override fun getItemCount() : Int = op.size

}