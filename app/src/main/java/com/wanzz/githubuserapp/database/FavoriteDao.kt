package com.wanzz.githubuserapp.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavorite(user: FavoriteEntity)

    @Query("DELETE FROM favorite WHERE favorite.id = :id")
    fun removeFavorite(id: Int)

    @Query("SELECT * FROM favorite ORDER BY login ASC")
    fun getAllUser(): LiveData<List<FavoriteEntity>>

}