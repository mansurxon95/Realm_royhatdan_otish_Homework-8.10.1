package com.mansurdev.homework_8101

import android.annotation.SuppressLint
import android.graphics.RenderEffect.createBlurEffect
import android.graphics.Shader
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mansurdev.homework_8101.databinding.ItemRcBinding
import com.mansurdev.homework_8101.realm.UserRealm

class Rc_View(var onClik: OnClik): ListAdapter<UserRealm, Rc_View.VH>(MyDiffUtill()) {

    inner class VH(var itemview : ItemRcBinding): RecyclerView.ViewHolder(itemview.root){

        fun onBind(user: UserRealm,position: Int){

            itemview.itemName.text = user.lastFirst_name
            itemview.itemTelNum.text = user.tel_number
            itemview.itemImage.setImageURI(user.images?.toUri())



            itemview.itemBtn.setOnClickListener {
                onClik.edit(user,position,itemview.itemBtn.rootView)
            }
            itemview.viewBtn.setOnClickListener {
                onClik.view(user, position)
            }



        }

    }

    class MyDiffUtill: DiffUtil.ItemCallback<UserRealm>(){
        override fun areItemsTheSame(oldItem: UserRealm, newItem: UserRealm): Boolean {
            return  oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: UserRealm, newItem: UserRealm): Boolean {
            return oldItem.equals(newItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemRcBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        holder.onBind(item,position)
    }

    interface OnClik{
        fun edit(contact: UserRealm,position: Int,view: View){

        }

        fun view(contact: UserRealm,position: Int){

        }
    }


}