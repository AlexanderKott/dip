package ru.netology.diploma.db

import android.content.Context
import androidx.room.*
import com.google.gson.Gson
import ru.netology.diploma.dao.*
import ru.netology.diploma.dao.old.PostWorkDao
import ru.netology.diploma.dao.pagination.EventPaginationKeyDao
import ru.netology.diploma.dao.pagination.PostPaginationKeyDao
import ru.netology.diploma.dao.pagination.UserPaginationKeyDao
import ru.netology.diploma.entity.*
import ru.netology.diploma.enumeration.AttachmentType

@Database(entities = [
    PostEntity::class,
    UserEntity::class,
    PostWorkEntity::class,
    PostKeyEntry::class,
    EventEntity::class,
    UserKeyEntry::class,
    EventKeyEntry::class,
    JobEntity::class
                     ], version = 11, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
    abstract fun eventDao(): EventDao
    abstract fun jobDao(): JobDao

    abstract fun postWorkDao(): PostWorkDao
    abstract fun keyPostPaginationDao(): PostPaginationKeyDao
    abstract fun keyUserPaginationDao(): UserPaginationKeyDao
    abstract fun keyEventPaginationDao(): EventPaginationKeyDao



    companion object {
        @Volatile
        private var instance: AppDb? = null

        fun getInstance(context: Context): AppDb {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDb::class.java, "app.db")
                .fallbackToDestructiveMigration()
                .build()
    }
}


class Converters {
    @TypeConverter
    fun toAttachmentType(value: String) = enumValueOf<AttachmentType>(value)
    @TypeConverter
    fun fromAttachmentType(value: AttachmentType) = value.name

    @TypeConverter
    fun listToJson(value: List<String>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String) = Gson().fromJson(value, Array<String>::class.java).toList()

    @TypeConverter
    fun listIntToJson(value: List<Int>?) = Gson().toJson(value)

}