package com.marvsystems.fotosoftapp.data.database

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.marvsystems.fotosoftapp.data.model.OrderStatus
import java.io.Serializable

@Entity
class Order : Serializable {
    @PrimaryKey(autoGenerate = true)
    var localOrderId: Long? = null
    var id: Int? = 0
    var albumBag: String? = ""
    var albumBox: String? = ""
    var albumName: String? = ""
    var albumType: String? = ""
    var amount: Int? = 0
    var appVersion: String? = ""
    var bankTrnId: String? = ""
    var bankTrnStatus: String? = ""
    var binding: String? = ""
    var clientIp: String? = ""
    var cloudUploadBy: String? = ""
    var coating: String? = ""
    var compId: Int? = 0
    var cover: String? = ""
    var createdOnDt: String? = ""
    var deletedByClientIp: String? = ""
    var deletedYn: String? = ""
    var deliveryMode: String? = ""
    var downloadByClientIp: String? = ""
    var downloadByOsname: String? = ""
    var downloadIncompleteOrderYn: String? = ""
    var downloadUserId: Int? = 0
    var downloadYn: String? = ""
    var emboss: String? = ""
    var folderName: String? = ""
    var iBillBookedByUserName: String? = ""
    var iBillBookedByUserid: Int? = 0
    var iBillOrderNo: String? = ""
    var imageQuality: String? = ""
    var imageResize: String? = ""

    @SerializedName("isCloudData")
    var cloudData: String? = ""
    var jobNo: String? = ""
    var noOfCopies: String? = ""
    var noOfFilesDownload: Int? = 0
    var noOfFilesReceived: Int? = 0
    var noOfFilesSelected: Int? = 0
    var orderDate: String? = "null"
    var orderNo: String? = ""
    var orderPaused: String? = ""
    var orderStatus: String? = ""
    var orderSuspendHour: Int? = 0
    var paidAmount: Int? = 0
    var paper: String? = ""
    var parentFolderName: String? = ""
    var partyId: Int? = 0
    var paymentMode: String? = ""
    var photoSize: String? = ""
    var productCategory: String? = ""
    var products: String? = ""
    var qty: Int? = 0
    var remarks: String? = ""
    var statusType: String? = ""
    var surchargeAmount: Int? = 0
    var totalAmount: Int? = 0
    var totalImageSize: Long? = 0
    var totalImageSizeUnCommpressed: Long? = 0
    var totalSizeInMb: Double? = 0.0
    var totalUncommpressedSizeInMb: Double? = 0.0
    var transferred: String? = ""
    var uploadEndDt: String? = ""
    var uploadStartDt: String? = ""
    var webUploadYn: String? = "Y"

    @Ignore
    var productId: String? = null

    @Ignore
    var productCategoryId: String? = null
    var status: String = OrderStatus.LOCAL_CREATED.toString()
    var isPause: Boolean = false
}