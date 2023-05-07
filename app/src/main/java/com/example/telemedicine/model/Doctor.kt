package com.example.telemedicine.model

data class Doctor(val name:String, val age:String,val gender:String,val ph_no:String,val reg_no:String,
                  val degree:String,val speciality:String,val experience:String,val location:String,
                  val date_range:List<String>,val slot_range:List<String>)
