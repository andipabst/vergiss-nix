package de.andicodes.vergissnix.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import de.andicodes.vergissnix.data.AppDatabase
import java.util.concurrent.Executors

@Database(entities = [Task::class], version = 3)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        const val DATABASE_NAME = "vergissnix_app_database"

        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val NUMBER_OF_THREADS = 4

        @JvmField
        val databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS)

        @JvmStatic
        fun getDatabase(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room
                            .databaseBuilder(
                                context.applicationContext,
                                AppDatabase::class.java,
                                DATABASE_NAME
                            )
                            .addMigrations(
                                MIGRATION_1_2,
                                MIGRATION_2_3
                            )
                            .build()
                    }
                }
            }
            return INSTANCE
        }

        /**
         * Migration that adds the timeCreated column to the Task table
         */
        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Task ADD COLUMN timeCreated TEXT")
            }
        }

        /**
         * Set the value "autoGenerate = true" on the Task id column. No action necessary in the migration.
         */
        private val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {}
        }
    }
}