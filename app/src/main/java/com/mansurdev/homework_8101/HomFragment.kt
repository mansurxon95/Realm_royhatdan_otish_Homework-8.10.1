package com.mansurdev.homework_8101

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.mansurdev.homework_8101.databinding.FragmentHomBinding
import com.mansurdev.homework_8101.realm.UserRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query


class HomFragment : Fragment() {

    lateinit var binding:FragmentHomBinding
    lateinit var list:ArrayList<UserRealm>
    // use the RealmConfiguration.Builder() for more options
    val configuration = RealmConfiguration.create(schema = setOf(UserRealm::class))
    val realm = Realm.open(configuration)





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomBinding.inflate(inflater,container,false)

        binding.btnLog.setOnClickListener {

            var tel =  binding.telraqamView.text.toString().trim()
            var password = binding.passvordView.text.toString().trim()
            if (tel.isNotEmpty()&&password.isNotEmpty()){

                list = ArrayList(realm.query<UserRealm>().find())

                if (list.isNotEmpty()){

                    var user:UserRealm?= null

                    for (i in list){

                        if (i.tel_number==tel&&i.passwords==password){
                            user=i
                        }

                    }

                    if (user!=null){

                        findNavController().navigate(R.id.action_homFragment_to_registeredFragment)
                    }else Toast.makeText(
                        binding.root.context,
                        "Xato login yoki parol",
                        Toast.LENGTH_SHORT
                    ).show()

                }else { Toast.makeText(
                    binding.root.context,
                    "Ro'yxatdan o'tgan foydalanuvchilar mavjud emas",
                    Toast.LENGTH_SHORT
                ).show()
                }

            }else { Toast.makeText(
                binding.root.context,
                "Barcha maydonlarnt to'ldirish shart",
                Toast.LENGTH_SHORT
            ).show()
            }

        }

        binding.btnRegistr.setOnClickListener {
            findNavController().navigate(R.id.action_homFragment_to_signUpFragment)
        }


        return binding.root

    }



}