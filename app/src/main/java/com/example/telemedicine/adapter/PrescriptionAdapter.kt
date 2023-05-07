package com.example.telemedicine.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.telemedicine.AppConstants
import com.example.telemedicine.R
import com.example.telemedicine.ui.userspace.patient.PatientSpace
import com.example.telemedicine.ui.userspace.patient.ViewDoctorProfileFragment
import com.squareup.picasso.Picasso

class PrescriptionAdapter(
    var context: Context,
    var op: List<HashMap<String, String>>,
    var progressBar5: ProgressBar
) :
    RecyclerView.Adapter<PrescriptionAdapter.DoctorViewHolder>(){


  inner  class DoctorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(doctorList: HashMap<String, String>) {
            with(itemView){

                var tv_doctor_name  : TextView = this.findViewById(R.id.tv_doctor_name) as TextView

                var img_loader : ProgressBar = this.findViewById(R.id.progressBar9) as ProgressBar


                var tv_prescriptions_date  : TextView = this.findViewById(R.id.tv_prescription_date) as TextView

                var iv_prescription : ImageView = this.findViewById(R.id.iv_prescription)

                tv_doctor_name.text = "Prescribed by : Dr ${doctorList.get("doctor_name")}"

                tv_prescriptions_date.text = "Date : ${doctorList.get("date")}"

                var url: String? = doctorList.get("imageUrl")

                Picasso.get().load(url).fit().centerCrop().into(iv_prescription)

                progressBar5.visibility = View.GONE

                img_loader.visibility = View.GONE



            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.prescription_item, parent, false)
        return DoctorViewHolder(v)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) = holder.bind(op[position])

    override fun getItemCount() : Int = op.size

}