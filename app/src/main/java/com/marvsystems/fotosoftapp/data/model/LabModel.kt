package com.marvsystems.fotosoftapp.data.model

data class LabModel(
    val allowCoverUploadYn: String,
    val amcdateOriginal: String,
    val amcgracePeriod: Int,
    val amcgracePeriodDate: String,
    val amcstartDate: String,
    val billingModule: String,
    val cloudQuotaMb: Int,
    val comp: Comp,
    val compId: Int,
    val compressionValue: Int,
    val createdOnDt: String,
    val downloadScanDays: Int,
    val ebookActiveYn: String,
    val ebookCompid: Int,
    val ebookLicKey: String,
    val ftpId: Int,
    val ftpfolder: String,
    val ftpfolderImages: String,
    val ftphost: String,
    val ftphostDownload: String,
    val ftphostDownloadXx: String,
    val ftppwd: String,
    val ftpuserName: String,
    val hardDiskSrlNo: String,
    val id: Int,
    val imageDpi: Double,
    val inHouseFtpMode: String,
    val installationDate: String,
    val installationType: Int,
    val isEbookUser: String,
    val isiBillUser: String,
    val licenseKey: String,
    val licenseStatus: String,
    val oldFtphost: String,
    val productId: String,
    val productVersion: String,
    val sendSmslab: String,
    val sendSmsparty: String,
    val underAmc: String,
    val updatedBy: String,
    val updatedEntDt: String,
    val uploadDtExpHour: Int,
    val versionReleaseDate: String,
    val webModule: String,
    val yearlyOrderNo: Boolean
)