package de.andicodes.vergissnix.data;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Task.class}, version = 3)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room
                            .databaseBuilder(context.getApplicationContext(), AppDatabase.class, "vergissnix_app_database")
                            .addMigrations(
                                    MIGRATION_1_2,
                                    MIGRATION_2_3
                            )
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Migration that adds the timeCreated column to the Task table
     */
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Task ADD COLUMN timeCreated TEXT");
        }
    };

    /**
     * Set the value "autoGenerate = true" on the Task id column. No action necessary in the migration.
     */
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
        }
    };
}
