package com.marvsystems.fotosoftapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.marvsystems.fotosoftapp.data.model.*

@Database(
    entities = [
        Lab::class,
        Order::class,
        Product::class,
        ProductCategory::class,
        ProductCategoryLists::class,
        AlbumBagMast::class,
        AlbumBagLists::class,
        AlbumBoxLists::class,
        AlbumBoxMast::class,
        CoatingLists::class,
        CoatingMast::class,
        CoverLists::class,
        CoverMast::class,
        PaperMast::class,
        PaperLists::class,
        PaperSizeLists::class,
        PaperSizeMast::class,
        OrderImages::class,
    ], version = 1, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun labDao(): LabDao
    abstract fun orderDao(): OrderDao
    abstract fun masterDataDao(): MasterDataDao
    abstract fun orderImageDao(): OrderImagesDao
}