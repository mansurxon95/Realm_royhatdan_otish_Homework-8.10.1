package com.mansurdev.homework_8101.realm

import android.annotation.SuppressLint
import android.net.Uri
import androidx.camera.core.processing.SurfaceProcessorNode.In
import io.realm.kotlin.types.ObjectId
import io.realm.kotlin.types.RealmObject

class UserRealm:RealmObject {
    var _id:ObjectId?=null
    var lastFirst_name:String? = null
    var tel_number:String? = null
    var davlats:String? = null
    var viloyats:String? = null
    var passwords:String? = null
    var images: String?=null
}