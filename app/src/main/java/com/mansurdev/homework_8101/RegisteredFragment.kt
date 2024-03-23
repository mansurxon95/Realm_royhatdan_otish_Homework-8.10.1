package com.mansurdev.homework_8101

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.telephony.SmsManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.concurrent.futures.await
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.mansurdev.homework_8101.databinding.DialogLayoutBinding
import com.mansurdev.homework_8101.databinding.DialogSmsBinding
import com.mansurdev.homework_8101.databinding.EditDialogBinding
import com.mansurdev.homework_8101.databinding.FragmentRegisteredBinding
import com.mansurdev.homework_8101.realm.UserRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class RegisteredFragment : Fragment() {
    lateinit var binding: FragmentRegisteredBinding
    lateinit var list:ArrayList<UserRealm>
    // use the RealmConfiguration.Builder() for more options
    val configuration = RealmConfiguration.create(schema = setOf(UserRealm::class))
    val realm = Realm.open(configuration)
    lateinit var adapter:Rc_View




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisteredBinding.inflate(inflater,container,false)

        list = ArrayList(realm.query<UserRealm>().find())
        adapter = Rc_View(object :Rc_View.OnClik{

            override fun edit(contact: UserRealm, position: Int, view: View) {
                super.edit(contact, position, view)

                val popupMenu: PopupMenu = PopupMenu(requireContext(),view)
                popupMenu.menuInflater.inflate(R.menu.contex_menu,popupMenu.menu)
                popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                    when(item.itemId) {
                        R.id.edit ->{
                            var alertDialog = AlertDialog.Builder(requireContext())
                            alertDialog.setCancelable(false)
                            var dialogview = EditDialogBinding.inflate(LayoutInflater.from(binding.root.context),
                                null,false)
                            dialogview.itemName.setText(contact.lastFirst_name)
                            dialogview.itemTelNum.setText(contact.tel_number)
                            dialogview.itemImage.setImageURI(contact.images?.toUri())
                            alertDialog.setView(dialogview.root)

                            var shov = alertDialog.show()

                            dialogview.close.setOnClickListener {
                                shov.dismiss()

                            }
                            dialogview.save.setOnClickListener {
                                val name = dialogview.itemName.text.trim().toString()
                                val num = dialogview.itemTelNum.text.trim().toString()
                                if (name.isNotEmpty()&&num.isNotEmpty()){

                                    var list = ArrayList(realm.query<UserRealm>().find())

                                    realm.writeBlocking {
                                        findLatest(list[position])?.lastFirst_name = name
                                        findLatest(list[position])?.tel_number = num
                                    }

                                }




                            }
                        }

                        R.id.delete ->{
                            var alertDialog = AlertDialog.Builder(requireContext())
                            alertDialog.setCancelable(false)
                            alertDialog.setMessage("Kontakt malumotini o'chirmoqchimisiz")
                            alertDialog.setTitle("Delete")
                            alertDialog.setCancelable(false)

                            alertDialog.setPositiveButton("O'chirish"
                            ) { dialog, which ->
                               realm.writeBlocking {
                                   val delet = query<UserRealm>().find()[position]
                                   delete(delet)
                               }
                            }
                            alertDialog.setNegativeButton("Rat etish"
                            ) { dialog, which ->

                            }


                            alertDialog.show()



                        }
                    }
                    true
                })
                popupMenu.show()


            }

            override fun view(contact: UserRealm, position: Int) {
                super.view(contact, position)

                var alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setCancelable(true)
                var dialogview = DialogLayoutBinding.inflate(LayoutInflater.from(binding.root.context),null,false)
                alertDialog.setView(dialogview.root)
                var shov = alertDialog.show()

                dialogview.btnCall.setOnClickListener{

                    if (checkReadContactsPermission(requireContext(),android.Manifest.permission.CALL_PHONE)){
                        val callIntent = Intent(Intent.ACTION_CALL)
                        callIntent.data = Uri.parse("tel:${contact.tel_number}")
                        startActivity(callIntent)
                        shov.dismiss()
                    }else{
                        requestPermissions(android.Manifest.permission.CALL_PHONE)
                    }


                }

                dialogview.btnSms.setOnClickListener {
                    shov.dismiss()
                    var alertDialog = AlertDialog.Builder(requireContext())
                    alertDialog.setCancelable(false)
                    var dialogview = DialogSmsBinding.inflate(LayoutInflater.from(binding.root.context),null,false)
                    alertDialog.setView(dialogview.root)
                    var shov = alertDialog.show()
                    dialogview.telNumber.setText(contact.tel_number)

                    dialogview.btnSend.setOnClickListener {
                        if (checkReadContactsPermission(requireContext(),android.Manifest.permission.SEND_SMS)){
                            try {

                                val smsManager: SmsManager
                                if (Build.VERSION.SDK_INT>=23) {

                                    smsManager = binding.root.context.getSystemService(SmsManager::class.java)
                                }
                                else{

                                    smsManager = SmsManager.getDefault()
                                }

                                smsManager.sendTextMessage(dialogview.telNumber.text.toString(), null, dialogview.textSms.text.toString(), null, null)

                                Toast.makeText(binding.root.context, "XABAR YUBORILDI", Toast.LENGTH_LONG).show()
                               shov.dismiss()
                            } catch (e: Exception) {

                                Toast.makeText(binding.root.context, "Iltimos, barcha ma'lumotlarni kiriting.."+e.message.toString(), Toast.LENGTH_LONG)
                                    .show()
                            }
                            shov.dismiss()
                        }else{
                            requestPermissions(android.Manifest.permission.SEND_SMS)
                        }
                    }

                }


            }

        })

        binding.rcView.adapter = adapter

        adapter.submitList(list)



        return binding.root
    }


    fun checkReadContactsPermission(context: Context, permission: String): Boolean {
        if (Build.VERSION.SDK_INT<33&&(
                    permission==android.Manifest.permission.READ_MEDIA_AUDIO||
                            permission==android.Manifest.permission.READ_MEDIA_VIDEO||
                            permission==android.Manifest.permission.READ_MEDIA_IMAGES)){

            return ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        }else return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }


    private fun requestPermissions(permission: String){
        if (Build.VERSION.SDK_INT<33&&(
                    permission==android.Manifest.permission.READ_MEDIA_AUDIO||
                            permission==android.Manifest.permission.READ_MEDIA_VIDEO||
                            permission==android.Manifest.permission.READ_MEDIA_IMAGES)){
            requestPermissions2(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }else requestPermissions2(permission)
    }


    private fun requestPermissions2(permission: String) {

        // pastdagi satr joriy faoliyatda ruxsat so'rash uchun ishlatiladi.
        // bu usul ish vaqti ruxsatnomalarida xatoliklarni hal qilish uchun ishlatiladi
        Dexter.withContext(binding.root.context)
            // pastdagi satr ilovamizda talab qilinadigan ruxsatlar sonini so'rash uchun ishlatiladi.
            .withPermissions(
                permission
            )
            // ruxsatlarni qo'shgandan so'ng biz tinglovchi bilan usulni chaqiramiz.
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {
                    // bu usul barcha ruxsatlar berilganda chaqiriladi
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        // hozir ishlayapsizmi
                        Toast.makeText(binding.root.context, "Barcha ruxsatlar berilgan, xarakatni davom etirishingiz mumki", Toast.LENGTH_SHORT).show()

                        if (checkReadContactsPermission(binding.root.context,permission)) {


                            // Ruxsat berilgan, kontakt ma'lumotlariga kirishingiz mumkin
                            // Kerakli harakatlar tugallanishi uchun shu yerga kod yozing
                        }

                    }
                    // har qanday ruxsatni doimiy ravishda rad etishni tekshiring
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied) {
                        // ruxsat butunlay rad etilgan, biz foydalanuvchiga dialog xabarini ko'rsatamiz.
                        showSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(list: List<PermissionRequest>, permissionToken: PermissionToken) {
                    // foydalanuvchi ba'zi ruxsatlarni berib, ba'zilarini rad qilganda bu usul chaqiriladi.
                    permissionToken.continuePermissionRequest()
                }
            }).withErrorListener {
                // biz xato xabari uchun tost xabarini ko'rsatmoqdamiz.
                Toast.makeText(binding.root.context, "Error occurred! ", Toast.LENGTH_SHORT).show()
            }
            // pastdagi satr bir xil mavzudagi ruxsatlarni ishga tushirish va ruxsatlarni tekshirish uchun ishlatiladi
            .onSameThread().check()

    }

    // quyida poyabzal sozlash dialog usuli
    // dialog xabarini ko'rsatish uchun ishlatiladi.
    private fun showSettingsDialog() {
        // biz ruxsatlar uchun ogohlantirish dialogini ko'rsatmoqdamiz
        val builder = AlertDialog.Builder(binding.root.context)

        // pastdagi satr ogohlantirish dialogining sarlavhasidir.
        builder.setTitle("Need Permissions")

        // pastdagi satr bizning muloqotimiz uchun xabardir
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.")
        builder.setPositiveButton("GOTO SETTINGS") { dialog, which ->
            // bu usul musbat tugmani bosganda va shit tugmasini bosganda chaqiriladi
            // biz foydalanuvchini ilovamizdan ilovamiz sozlamalari sahifasiga yo'naltirmoqdamiz.
            dialog.cancel()
            // quyida biz foydalanuvchini qayta yo'naltirish niyatimiz.
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", null, "SetingsFragment") // TODO:
            intent.data = uri
            startActivityForResult(intent, 101)
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            // bu usul foydalanuvchi salbiy tugmani bosganda chaqiriladi.
            dialog.cancel()
        }
        // dialog oynamizni ko'rsatish uchun quyidagi qatordan foydalaniladi
        builder.show()
    }



}