package com.example.telemedicine.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.util.Pair
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.telemedicine.R
import com.example.telemedicine.timepicker.TimePicker
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.*

class ChipsAdapter(val context: Context, val scr_type: String, val chipsContents  : MutableList<String>) :
    RecyclerView.Adapter<ChipsAdapter.ChipViewHolder>(){

    inner class ChipViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(chipContent: String) {
            with(itemView) {

                when(scr_type){
                    "add"->{

                        var date_range : TextView = this.findViewById(R.id.date_chip)

                        var iv_cancel : ImageView = this.findViewById(R.id.iv_cancel_dates)


                        date_range.text = chipContent
                        iv_cancel.setOnClickListener{
                            chipsContents.remove(chipContent)
                            notifyDataSetChanged()
                        }
                    }

                    "edit_date"->{

                        var date_range : TextView = this.findViewById(R.id.date_chip)

                        var iv_cancel : ImageView = this.findViewById(R.id.iv_cancel_dates)

                        var iv_delete_date : ImageView = this.findViewById(R.id.iv_delete_dates)

                        iv_delete_date.visibility = View.VISIBLE

                        var index = chipsContents.indexOf(chipContent)


                        date_range.text = chipContent
                        iv_cancel.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_edit_24))
                        /// open calendar view
                        iv_cancel.setOnClickListener{
                            showDatePickerDialog(date_range,chipsContents,index)
                        }

                        iv_delete_date.setOnClickListener {
                            create_alert_dialog_box("Do you want to delete the date range ?",chipContent)
                        }

                    }

                    "edit_time"->{

                        var index = chipsContents.indexOf(chipContent)

                        var tv_slot_type : TextView = this.findViewById(R.id.tv_slot_type)

                        var iv_from_edit_time : ImageView = this.findViewById(R.id.iv_from_edit_time)

                        var iv_to_edit_time : ImageView = this.findViewById(R.id.iv_to_edit_time)

                        var from_date_chip : TextView = this.findViewById(R.id.from_date_chip)

                        var to_date_chip : TextView = this.findViewById(R.id.to_date_chip)

                        var time_slot : MutableList<String> = chipContent.split("-").toMutableList()

                        when(index){

                            0 -> {
                                from_date_chip.text  = time_slot[0]
                                to_date_chip.text = time_slot[1]
                                tv_slot_type.text = "Morning Slot"
                            }

                            1 -> {
                                from_date_chip.text  = time_slot[0]
                                to_date_chip.text = time_slot[1]
                                tv_slot_type.text = "Afternoon Slot"
                            }

                            2 -> {
                                from_date_chip.text  = time_slot[0]
                                to_date_chip.text = time_slot[1]
                                tv_slot_type.text = "Evening Slot"
                            }

                        }

                        iv_from_edit_time.setOnClickListener{
                            showTimePickerDialog(from_date_chip,time_slot,"From",index)
                        }

                        iv_to_edit_time.setOnClickListener{
                            showTimePickerDialog(to_date_chip,time_slot,"To",index)
                        }
                    }
                }

            }
        }
    }

    fun create_alert_dialog_box(msg:String,chipsContents: String){
        // build alert dialog
        val dialogBuilder : AlertDialog.Builder = AlertDialog.Builder(context)

        // set message of alert dialog
        dialogBuilder.setMessage(msg)
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton("Proceed", DialogInterface.OnClickListener {
                    dialog, id -> delete_date(chipsContents)
            })
            // negative button text and action
            .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                    dialog, id -> dialog.cancel()
            })

        // create dialog box
        val alert = dialogBuilder.create()
        alert.show()
    }

    private fun delete_date(chipsContent: String) {
        chipsContents.remove(chipsContent)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChipViewHolder {

        var v = LayoutInflater.from(parent.context).inflate(R.layout.chip_item, parent, false)

        when(scr_type){
            "edit_time" -> {
                v = LayoutInflater.from(parent.context).inflate(R.layout.time_slot, parent, false)
            }
        }
        return ChipViewHolder(v)
    }

    override fun getItemCount() = chipsContents.size

    override fun onBindViewHolder(holder: ChipViewHolder, position: Int) = holder.bind(chipsContents[position])

    private fun showDatePickerDialog(
        date_range_txt: TextView,
        chipsContents: MutableList<String>,
        index: Int
    ) {

        val builder : MaterialDatePicker.Builder<Pair<Long, Long>> = MaterialDatePicker.Builder.dateRangePicker()
        val picker = builder.build()
        picker.show((context as FragmentActivity).supportFragmentManager!!, picker.toString())


        picker.addOnPositiveButtonClickListener {
            var date_range :String = picker.headerText.toString()
            date_range_txt.setText(date_range)
            chipsContents[index] = date_range
        }

    }

    private fun showTimePickerDialog(
        textView: TextView,
        time_slot: MutableList<String>,
        s: String,
        index: Int
    ) {
        var timePicker = TimePicker(context =context, false, true)
        val cal = Calendar.getInstance()
        val h = cal.get(Calendar.HOUR_OF_DAY)
        val m = cal.get(Calendar.MINUTE)
        timePicker.showDialog(h, m, object : TimePicker.Callback {
            override fun onTimeSelected(hourOfDay: Int, minute: Int) {
                val hourStr = if (hourOfDay < 10) "0${hourOfDay}" else "${hourOfDay}"
                val minuteStr = if (minute < 10) "0${minute}" else "${minute}"


                val AM_PM: String
                AM_PM = if (hourOfDay < 12) {
                    "am"
                } else {
                    "pm"
                }

                textView.text = "${hourOfDay}:${minuteStr} ${AM_PM}"

                when(s){

                    "From" -> {
                        time_slot[0] = textView.text.toString()
                    }

                    "To" -> {
                        time_slot[1] = textView.text.toString()
                    }

                }

                chipsContents[index] = "${time_slot[0]}-${time_slot[1]}"

            }
        })
    }

}