package com.example.telemedicine.ui.userspace.patient

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.telemedicine.AppConstants
import com.example.telemedicine.R
import com.example.telemedicine.databinding.FragmentProfileBinding
import com.example.telemedicine.ui.registration.RegistrationActivity
import com.example.telemedicine.viewmodel.PatientProfileViewModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

/*** Chandan Patro ***/

class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var viewModel: PatientProfileViewModel
    private lateinit var fragmentProfileBinding :FragmentProfileBinding
    private var pick_img:Int = 101
    private var report_img : ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this@ProfileFragment).get(PatientProfileViewModel::class.java)
        val sharedPreference =  requireContext().getSharedPreferences(AppConstants.sharedPrefName, android.content.Context.MODE_PRIVATE)

        val userid = sharedPreference.getString(AppConstants.userid,null)


        viewModel.get_patient_profile(this,userid!!)

        ((activity) as PatientSpace)!!.patientSpaceBinding.bottomNavigation.visibility = View.VISIBLE

        fragmentProfileBinding.tvLogout.setOnClickListener {
            create_alert_dialog_box("Do you want to Log out ?")
        }

        fragmentProfileBinding.tvUploadReports.setOnClickListener {
            var intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(intent,pick_img)
        }

        fragmentProfileBinding.tvMyReports.setOnClickListener {
            var fragment = ReportsFragment()

            val bundle = Bundle()

            bundle.putSerializable("patient_uid",userid)

            fragment.arguments = bundle

            ((context) as PatientSpace)!!.patientSpaceBinding.
            bottomNavigation.visibility = View.GONE

            (context as FragmentActivity)!!.supportFragmentManager.
            beginTransaction().addToBackStack(AppConstants.tag).
            replace(R.id.root_layout,fragment!!).commit()
        }

    }


    fun clear_shared_prefs(){
        val sharedPreference =
            requireContext().getSharedPreferences(AppConstants.sharedPrefName, android.content.Context.MODE_PRIVATE)

        val timer = object: CountDownTimer(1600, 100) {
            override fun onTick(millisUntilFinished: Long) {
                fragmentProfileBinding.progressBar.visibility = View.VISIBLE
            }

            override fun onFinish() {
                sharedPreference.edit().clear().apply()
                Toast.makeText(requireContext(),"Logged Out Sucessfully",Toast.LENGTH_LONG).show()
                val i = Intent(activity, RegistrationActivity::class.java)
                startActivity(i)
                (activity as Activity?)!!.overridePendingTransition(0, 0)
            }
        }
        timer.start()
    }


    fun create_alert_dialog_box(msg:String){
        // build alert dialog
        val dialogBuilder : AlertDialog.Builder = AlertDialog.Builder(requireContext())

        // set message of alert dialog
        dialogBuilder.setMessage(msg)
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton("Proceed", DialogInterface.OnClickListener {
                    dialog, id -> clear_shared_prefs()
            })
            // negative button text and action
            .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                    dialog, id -> dialog.cancel()
            })

        // create dialog box
        val alert = dialogBuilder.create()
        alert.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentProfileBinding = FragmentProfileBinding.inflate(inflater,container,false)
        return fragmentProfileBinding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun display_in_view(patientProfile : HashMap<String,String>?){

//        fragmentProfileBinding.btnEdit.isEnabled = true
        fragmentProfileBinding.progressBar.visibility = View.GONE

        var name = patientProfile?.get("name")
        var age = patientProfile?.get("age")
        var blood_group = patientProfile?.get("blood_group")
        var gender = patientProfile?.get("gender")
        var phone = patientProfile?.get("phone")
        var med_history = patientProfile?.get("med_history")

        fragmentProfileBinding.progressBar.visibility = View.GONE

        fragmentProfileBinding.tvPatientName.text = name
        fragmentProfileBinding.tvPatientAge.text = age
        fragmentProfileBinding.tvPatientPhone.text = phone
        fragmentProfileBinding.tvPatientBloodGroup.text = blood_group
        fragmentProfileBinding.tvPatientGender.text = gender
        fragmentProfileBinding.tvPatientMedicalHistory.text = med_history
    }

    fun getFileExtension(uri: Uri?) : String{
        val extension: String?
        extension = if (uri!!.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            val mime = MimeTypeMap.getSingleton()
            mime.getExtensionFromMimeType(requireContext()!!.contentResolver.getType(uri!!))
        } else {
            MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.getPath())).toString())
        }

        return extension!!
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pick_img && data != null){
            var img_uri : Uri? = data.data

            fragmentProfileBinding.progressBar.visibility =
                View.VISIBLE

            upload_to_firebase(img_uri)
        }
    }

    private fun upload_to_firebase(img_uri: Uri?) {

        FirebaseStorage.getInstance().
        getReference(AppConstants.reports_coll_name).
        child("${System.currentTimeMillis()}.${getFileExtension(uri = img_uri)}").
        putFile(img_uri!!).addOnSuccessListener{
            it.storage.downloadUrl.addOnSuccessListener {
                update_in_firebase(it)
            }
        }
    }

    private fun update_in_firebase(uri: Uri) {
        report_img.add(uri.toString())
        val sharedPreference =  requireContext().getSharedPreferences(AppConstants.sharedPrefName, Context.MODE_PRIVATE)

        val userid = sharedPreference.getString(AppConstants.userid,null)

        var prescription_hashmap : HashMap<String,ArrayList<String>> = hashMapOf()

        prescription_hashmap.put("reports",report_img)

        FirebaseDatabase.getInstance().getReference(AppConstants.patient_coll_name).child(userid!!).
        updateChildren(prescription_hashmap as Map<String, ArrayList<String>>).addOnCompleteListener {
            if (it.isSuccessful){
                fragmentProfileBinding.progressBar.visibility =
                    View.GONE
                Toast.makeText(context,"Report Uploaded Successfully",Toast.LENGTH_LONG).show()
            }
        }

    }
}