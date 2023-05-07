package com.example.telemedicine.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.telemedicine.R
import com.example.telemedicine.adapter.DoctorConsultationListAdapter.*

class DoctorConsultationListAdapter(var context: Context , var doctor_info :List<HashMap<String,String>>)
    : RecyclerView.Adapter<DoctorConsultationListAdapter.ConsultViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultViewHolder {
        var itemView = LayoutInflater.from(parent.context).inflate(R.layout.consultation_item, parent, false)
        return DoctorConsultationListAdapter.ConsultViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ConsultViewHolder, position: Int) {
        holder.bind(doctor_info[position])

    }

    override fun getItemCount(): Int {
        return doctor_info.size
    }

    class ConsultViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(consultList: HashMap<String, String>){

                var tv_consultant_name : TextView = itemView.findViewById(R.id.tv_consultant_name) as TextView
                var tv_date_slot : TextView = itemView.findViewById(R.id.tv_date_slot) as TextView
                var tv_time_slot : TextView = itemView.findViewById(R.id.tv_time_slot) as TextView



                tv_consultant_name.text = "Doctor : Dr ${consultList.get("doctor_name")}"
                tv_date_slot.text = "Date : ${consultList.get("date_slot")}"
                tv_time_slot.text = "Time : ${consultList.get("time_slot")}"

        }

    }

}