package com.marvsystems.fotosoftapp.data.model

data class MasterDataModel(
    val products: List<Product>,
    val productCategories: List<ProductCategory>,
    val productCategoryLists: List<ProductCategoryLists>,
    val albumBagLists: List<AlbumBagLists>,
    val albumBagMasts: List<AlbumBagMast>,
    val albumBoxLists: List<AlbumBoxLists>,
    val albumBoxMasts: List<AlbumBoxMast>,
    val coatingLists: List<CoatingLists>,
    val coatingMasts: List<CoatingMast>,
    val coverLists: List<CoverLists>,
    val coverMasts: List<CoverMast>,
    val paperLists: List<PaperLists>,
    val paperMasts: List<PaperMast>,
    val paperSizeLists: List<PaperSizeLists>,
    val paperSizeMasts: List<PaperSizeMast>,
    val albumTypes: List<AlbumType>,
)