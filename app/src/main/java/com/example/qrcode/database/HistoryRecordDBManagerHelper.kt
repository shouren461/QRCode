package com.example.qrcode.database

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.Update
import com.example.qrcode.functions.createFunction.CreateType

//历史记录实体类
@Entity(tableName = "HISTORY_RECORD_TABLE")
data class HistoryRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val category: CreateType,
    val isFavorite: Boolean = false,
    val timeStamp: Long = System.currentTimeMillis(),
    val favoriteTime: Long? = null
)
//历史记录数据操作接口
@Dao
interface HistoryRecordDao {
    //插入单条历史记录
    @Insert
    fun insertHistoryRecord(historyRecord: HistoryRecord): Long
    //根据id删除单条历史记录
    @Delete
    fun deleteHistoryRecord(historyRecord: HistoryRecord): Int
    //根据id批量删除历史记录
    @Query("Delete from HISTORY_RECORD_TABLE where id in (:ids)")
    fun deleteHistoryRecordByBatch(ids: List<Long>): Int

    //查询所有历史记录
    @Query("select * from HISTORY_RECORD_TABLE order by timeStamp DESC")
    fun selectAllHistoryRecord(): List<HistoryRecord>
    //查询已收藏的历史记录列表
    @Query("select * from HISTORY_RECORD_TABLE where isFavorite = 1 order by favoriteTime DESC")
    fun selectFavoriteHistoryRecord(): List<HistoryRecord>

    //更新用户收藏记录信息
    @Query("update HISTORY_RECORD_TABLE set isFavorite = :isFavourite,favoriteTime = :timeStamp where id = :id")
    fun updateFavoriteHistoryRecord(id: Long,isFavourite: Boolean,timeStamp: Long?): Int

}
@Database(version = 1, entities = [HistoryRecord::class])
@TypeConverters(HistoryRecordConverter::class)
abstract class HistoryRecordDB: RoomDatabase() {

    abstract fun historyRecordDao(): HistoryRecordDao
    companion object{
        //创建数据库实例类
        private var instance: HistoryRecordDB ?= null

        @Synchronized
        fun getDatabase(context: Context): HistoryRecordDB{
            instance?.let {
                return it
            }
            return Room.databaseBuilder(
                context.applicationContext, HistoryRecordDB::class.java,"history_record_db"
            ).build().apply {
                instance = this
            }
        }
    }
}
