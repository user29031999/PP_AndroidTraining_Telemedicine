package com.example.fooddeliveryapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.telemedicine.R
import com.squareup.picasso.Picasso

class MedicalReportsAdapter(
    var op: ArrayList<String>
) :
    RecyclerView.Adapter<MedicalReportsAdapter.DoctorViewHolder>(){


   inner class DoctorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(image_uri: String) {
            with(itemView){

                var iv_reports = itemView.findViewById(R.id.iv_reports) as ImageView

                Picasso.get().load(image_uri).fit().centerCrop().into(iv_reports)

                }

            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.reports_item, parent, false)
        return DoctorViewHolder(v)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
            var sel : String =  op[position]
            holder.bind(sel)
    }

    override fun getItemCount() : Int = op.size

}