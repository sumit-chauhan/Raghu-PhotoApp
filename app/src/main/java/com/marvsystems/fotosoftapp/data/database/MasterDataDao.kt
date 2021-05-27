package com.marvsystems.fotosoftapp.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.marvsystems.fotosoftapp.data.database.mapper.*
import com.marvsystems.fotosoftapp.data.model.*
import io.reactivex.Flowable

@Dao
interface MasterDataDao {

    @Query("SELECT * FROM Product")
    fun getAllProducts(): Flowable<List<Product>>

    @Query("SELECT pcl.productCategoryId,pc.description FROM ProductCategoryLists pcl LEFT JOIN ProductCategory pc on pcl.productCategoryId=pc.id WHERE pcl.productId =:productId")
    fun getProductCategories(productId:String): Flowable<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllProducts(products: List<Product>)

    @Insert
    fun insertAllProductCategory(products: List<ProductCategory>)

    @Insert
    fun insertAllProductCategories(products: List<ProductCategoryLists>)

    @Query("SELECT abl.albumBagId,ab.description FROM AlbumBagLists abl LEFT JOIN AlbumBagMast ab on abl.albumBagId=ab.id WHERE abl.productCategoryId =:productCategoryId")
    fun getAlbumBagList(productCategoryId:String): Flowable<List<AlbumBag>>

    @Insert
    fun insertAllAlbumBagList(products: List<AlbumBagLists>)

    @Insert
    fun insertAllAlbumBagMast(products: List<AlbumBagMast>)

    @Query("SELECT abl.albumBoxId,ab.description FROM AlbumBoxLists abl LEFT JOIN AlbumBoxMast ab on abl.albumBoxId=ab.id WHERE abl.productCategoryId =:productCategoryId")
    fun getAlbumBoxList(productCategoryId:String): Flowable<List<AlbumBox>>

    @Insert
    fun insertAllAlbumBoxList(products: List<AlbumBoxLists>)

    @Insert
    fun insertAllAlbumBoxMast(products: List<AlbumBoxMast>)

    @Query("SELECT cl.coatingId,cm.description FROM CoatingLists cl LEFT JOIN CoatingMast cm on cl.coatingId=cm.id WHERE cl.productCategoryId =:productCategoryId")
    fun getCoatingList(productCategoryId:String): Flowable<List<Coating>>

    @Insert
    fun insertAllCoatingList(products: List<CoatingLists>)

    @Insert
    fun insertAllCoatingMast(products: List<CoatingMast>)

    @Query("SELECT cl.coverId,cm.description FROM CoverLists cl LEFT JOIN CoverMast cm on cl.coverId=cm.id WHERE cl.productCategoryId =:productCategoryId")
    fun getCoverList(productCategoryId:String): Flowable<List<Cover>>

    @Insert
    fun insertAllCoverList(products: List<CoverLists>)

    @Insert
    fun insertAllCoverMast(products: List<CoverMast>)

    @Query("SELECT pl.paperId,pm.description FROM PaperLists pl LEFT JOIN PaperMast pm on pl.paperId=pm.id WHERE pl.productCategoryId =:productCategoryId")
    fun getPaperList(productCategoryId:String): Flowable<List<Paper>>

    @Insert
    fun insertAllPaperList(products: List<PaperLists>)

    @Insert
    fun insertAllPaperMast(products: List<PaperMast>)

    @Query("SELECT pl.paperSizeId,pm.description FROM PaperSizeLists pl LEFT JOIN PaperSizeMast pm on pl.paperSizeId=pm.id WHERE pl.productCategoryId =:productCategoryId")
    fun getPaperSizeList(productCategoryId:String): Flowable<List<PaperSize>>

    @Insert
    fun insertAllPaperSizeList(products: List<PaperSizeLists>)

    @Insert
    fun insertAllPaperSizeMast(products: List<PaperSizeMast>)

    @Query("Delete FROM Product")
    fun deleteAllProducts()

    @Query("Delete FROM ProductCategory")
    fun deleteAllProductCategory()

    @Query("Delete FROM ProductCategoryLists")
    fun deleteAllProductCategoryList()

    @Query("Delete FROM AlbumBagLists")
    fun deleteAllAlbumBagList()

    @Query("Delete FROM AlbumBagMast")
    fun deleteAllAlbumBagMast()

    @Query("Delete FROM AlbumBoxLists")
    fun deleteAllAlbumBoxList()

    @Query("Delete FROM AlbumBoxMast")
    fun deleteAllAlbumBoxMast()

    @Query("Delete FROM CoatingLists")
    fun deleteAllCoatingList()

    @Query("Delete FROM CoatingMast")
    fun deleteAllCoatingMast()

    @Query("Delete FROM CoatingLists")
    fun deleteAllCoverList()

    @Query("Delete FROM CoverMast")
    fun deleteAllCoverMast()

    @Query("Delete FROM PaperLists")
    fun deleteAllPaperList()

    @Query("Delete FROM PaperMast")
    fun deleteAllPaperMast()

    @Query("Delete FROM PaperSizeLists")
    fun deleteAllPaperSizeList()

    @Query("Delete FROM PaperSizeMast")
    fun deleteAllPaperSizeMast()

}