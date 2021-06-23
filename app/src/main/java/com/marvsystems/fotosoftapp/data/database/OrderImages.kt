package com.marvsystems.fotosoftapp.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.marvsystems.fotosoftapp.data.model.ImageUploadStatus
import java.io.Serializable

@Entity
class OrderImages : Serializable {

    @PrimaryKey(autoGenerate = true)
    var localImageId: Int? = null
    var id: Int? = 0
    var localOrderId: Int? = 0
    var orderId: Int? = 0
    var orderNo: String? = ""
    var imageName: String? = null
    var imageNameLocal: String? = null
    var imageSizeInKb: Long? = 0
    var status: String = ImageUploadStatus.PENDING.toString()
    var compId: Long? = null
    var partyId: Long? = null
    var paper: String? = null
    var paperSize: String? = null
    var imageType: String? = null
    var createdByUsrId: Long? = null
    var usrAe: String? = "A"
    var updateByUserId: Long? = 0
    var imageFolderName: String? = ""
    var imagePath: String? = ""
}