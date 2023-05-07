package com.example.telemedicine.ui.userspace.doctor

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
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
import com.example.telemedicine.AppConstants
import com.example.telemedicine.R
import com.example.telemedicine.databinding.FragmentDocumentSharingBinding
import com.example.telemedicine.model.Prescription
import com.example.telemedicine.ui.registration.RegsitrationActivity
import com.example.telemedicine.ui.userspace.patient.HomeFragment
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DocumentSharingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DocumentSharingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var _documentSharingBinding : FragmentDocumentSharingBinding
    private var pick_img:Int = 101
    private var patient_userid : String? = null
    private var doctor_name:String? = null
    private var patient_name:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            patient_name = it.getString("patient_name")
            patient_userid = it.getString("patient_user_id")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreference =  requireContext().getSharedPreferences(AppConstants.sharedPrefName, Context.MODE_PRIVATE)

        val userid = sharedPreference.getString(AppConstants.userid,null)

        GlobalScope.launch {
            fetch_doctor_name(userid = userid!!)
        }

        _documentSharingBinding.uploadDocument.setOnClickListener {
            //// open document uploading view
            var intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(intent,pick_img)
        }

        _documentSharingBinding.ivBack.setOnClickListener {

            var fragment = PatientListFragment()


            (context as FragmentActivity)!!.supportFragmentManager.
            beginTransaction().replace(R.id.root_layout,fragment!!).commit()
        }

        _documentSharingBinding.shareDocument.setOnClickListener {
            val timer = object: CountDownTimer(1600, 100) {
                override fun onTick(millisUntilFinished: Long) {
                      _documentSharingBinding.progressBar10.visibility = View.VISIBLE
                }

                override fun onFinish() {
                   Toast.makeText(requireContext(),"Prescription shared to patient successfully",Toast.LENGTH_LONG).show()
                    var fragment = PatientListFragment()

                    (context as FragmentActivity)!!.supportFragmentManager.
                    beginTransaction().replace(R.id.root_layout,fragment!!).commit()
                }
            }
            timer.start()
        }
    }

    private fun fetch_doctor_name(userid:String) {

        FirebaseDatabase.getInstance().getReference(AppConstants.doctor_coll_name).
        child(userid!!).get().addOnCompleteListener {
            var doctor_profile = it.result.value as HashMap<String, String> /* = java.util.HashMap<kotlin.String, kotlin.String> */
            doctor_name = doctor_profile.get("name")
            _documentSharingBinding.uploadDocument.isEnabled = true
        }
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
            Picasso.get().load(img_uri).into(_documentSharingBinding.ivPrescription)
            upload_to_firebase(img_uri)
        }
    }

    private fun upload_to_firebase(img_uri: Uri?) {

        val sdf = SimpleDateFormat("dd/M/yyyy")
        val currentDate : String = sdf.format(Date())

        FirebaseStorage.getInstance().
        getReference(AppConstants.presciption_coll_name).
        child("${System.currentTimeMillis()}.${getFileExtension(uri = img_uri)}").
        putFile(img_uri!!).addOnSuccessListener{
            it.storage.downloadUrl.addOnSuccessListener {
                _documentSharingBinding.progressBar8.visibility = View.GONE
                val prescription = Prescription(doctor_name = doctor_name!!,currentDate,it.toString())
                update_in_firebase_database(prescription)
            }
        }.addOnProgressListener {
            _documentSharingBinding.progressBar8.visibility = View.VISIBLE
        }
    }

    private fun update_in_firebase_database(prescription: Prescription) {
        prescription.doctor_name = patient_name.toString()

        val sharedPreference =  requireContext().getSharedPreferences(AppConstants.sharedPrefName, Context.MODE_PRIVATE)

        val userid = sharedPreference.getString(AppConstants.userid,null)

        FirebaseDatabase.getInstance().getReference(AppConstants.doctor_coll_name).child(userid!!).
        child(AppConstants.presciption_coll_name).child(patient_userid!!).setValue(prescription).addOnCompleteListener {
            if (it.isSuccessful){
                /// Exit from this screen
                update_in_patient_database(prescription,userid)
            }
        }
    }

    private fun update_in_patient_database(prescription: Prescription, userid: String) {
        prescription.doctor_name = doctor_name.toString()
        FirebaseDatabase.getInstance().getReference(AppConstants.patient_coll_name).child(patient_userid!!).
        child(AppConstants.presciption_coll_name).child(userid).setValue(prescription).addOnCompleteListener {
            _documentSharingBinding.shareDocument.isEnabled = true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _documentSharingBinding = FragmentDocumentSharingBinding.inflate(inflater,container,false)
        return _documentSharingBinding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DocumentSharingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DocumentSharingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}