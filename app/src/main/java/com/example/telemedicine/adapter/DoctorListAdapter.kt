package com.example.telemedicine.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.telemedicine.AppConstants
import com.example.telemedicine.R
import com.example.telemedicine.ui.userspace.patient.PatientSpace
import com.example.telemedicine.ui.userspace.patient.ViewDoctorProfileFragment

class DoctorListAdapter(
    var context: Context,
    var op: List<HashMap<String, String>>,
    var doctor_uid: MutableList<String>
) :
    RecyclerView.Adapter<DoctorListAdapter.DoctorViewHolder>(){


    class DoctorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(doctorList: HashMap<String, String>, doctor_user_id: String) {
            with(itemView){

                var tv_doctor_name  : TextView = this.findViewById(R.id.tv_doctor_name) as TextView


                var tv_doctor_degree_spec  : TextView = this.findViewById(R.id.tv_degree_spec) as TextView

                tv_doctor_name.text = "Dr ${doctorList.get("name")}"

                tv_doctor_degree_spec.text = "${doctorList.get("degree")}  (${doctorList.get("speciality")})"

                var tv_book_appointment : TextView = this.findViewById(R.id.tv_book_appointment) as TextView

                tv_book_appointment.setOnClickListener {
                    //// move to next screen that is booking appointments screen.
                    var fragment = ViewDoctorProfileFragment()

                    val bundle = Bundle()

                    bundle.putSerializable("doctor_info",doctorList)

                    bundle.putString("doctor_uid",doctor_user_id)

                    fragment.arguments = bundle

                    ((context) as PatientSpace)!!.patientSpaceBinding.
                    bottomNavigation.visibility = View.GONE

                    (context as FragmentActivity)!!.supportFragmentManager.
                    beginTransaction().addToBackStack(AppConstants.tag).
                    replace(R.id.root_layout,fragment!!).commit()
                }

            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.doctor_item, parent, false)
        return DoctorViewHolder(v)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) = holder.bind(op[position],doctor_uid[position])

    override fun getItemCount() : Int = op.size

}