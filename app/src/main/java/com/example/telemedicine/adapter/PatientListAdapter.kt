package com.example.telemedicine.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.telemedicine.AppConstants
import com.example.telemedicine.R
import com.example.telemedicine.ui.userspace.doctor.DoctorSpace
import com.example.telemedicine.ui.userspace.doctor.DocumentSharingFragment
import com.example.telemedicine.ui.userspace.doctor.ViewPatientProfileFragment
import com.google.firebase.database.FirebaseDatabase


class PatientListAdapter(
    var context: Context,
    var patient_info: List<HashMap<String, String>>,
    var patient_uid: MutableList<String>
)
    : RecyclerView.Adapter<PatientListAdapter.ConsultViewHolder>(){

    inner class ConsultViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(consultList: HashMap<String, String>, patient_user_id: String){

            var tv_consultant_name : TextView = itemView.findViewById(R.id.tv_consultant_name) as TextView
            var tv_date_slot : TextView = itemView.findViewById(R.id.tv_date_slot) as TextView
            var tv_time_slot : TextView = itemView.findViewById(R.id.tv_time_slot) as TextView
            var iv_prescription : ImageView = itemView.findViewById(R.id.iv_prescription) as ImageView
            var iv_patient_info : ImageView = itemView.findViewById(R.id.iv_patient_info) as ImageView
            var iv_call_option : ImageView = itemView.findViewById(R.id.iv_call_option) as ImageView



            tv_consultant_name.text = "Name :  ${consultList.get("patient_name")}"
            tv_date_slot.text = "Date : ${consultList.get("date_slot")}"
            tv_time_slot.text = "Time : ${consultList.get("time_slot")}"

            iv_prescription.setOnClickListener {

                ((context) as DoctorSpace)!!.activityDoctorSpaceBinding.
                bottomNavigation.visibility = View.GONE

                var fragment = DocumentSharingFragment()

                var bundle = Bundle()

                bundle.putString("patient_name",consultList.get("patient_name"))

                bundle.putString("patient_user_id",patient_user_id)

                fragment.arguments = bundle

                (context as FragmentActivity)!!.supportFragmentManager.
                beginTransaction().addToBackStack(AppConstants.tag).
                replace(R.id.root_layout,fragment!!).commit()

            }

            iv_patient_info.setOnClickListener {

                ((context) as DoctorSpace)!!.activityDoctorSpaceBinding.
                bottomNavigation.visibility = View.GONE

                var fragment = ViewPatientProfileFragment()

                var bundle = Bundle()

                bundle.putString("patient_user_id",patient_user_id)

                fragment.arguments = bundle

                (context as FragmentActivity)!!.supportFragmentManager.
                beginTransaction().addToBackStack(AppConstants.tag).
                replace(R.id.root_layout,fragment!!).commit()
            }

            iv_call_option.setOnClickListener {
                get_patient_data(patient_user_id)
            }

        }

    }

    private fun call_the_patient(patient_ph_number: String) {
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.data = Uri.parse("tel:" + patient_ph_number)
        context.startActivity(dialIntent)
    }

    private fun get_patient_data(patient_user_id: String) {
        FirebaseDatabase.getInstance().getReference(AppConstants.patient_coll_name).child(patient_user_id!!).get().addOnCompleteListener{
            var patientProfile = it.result.value as HashMap<String, String>
            /* = java.util.HashMap<kotlin.String, kotlin.String> */
            var patient_ph_number = patientProfile?.get("phone")
            if (patient_ph_number != null){
                call_the_patient(patient_ph_number)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultViewHolder {
        var itemView = LayoutInflater.from(parent.context).inflate(R.layout.consultation_doctor_item, parent, false)
        return ConsultViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ConsultViewHolder, position: Int) {
        holder.bind(patient_info[position],patient_uid[position])

    }

    override fun getItemCount(): Int {
        return patient_info.size
    }

}