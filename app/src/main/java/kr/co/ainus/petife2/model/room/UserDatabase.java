package kr.co.ainus.petife2.model.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {User.class}, version = 1)
abstract public class UserDatabase extends RoomDatabase {

    private static UserDatabase instance;

    public static UserDatabase getInstance(Context context) {

        if (instance == null) {
            instance = Room.databaseBuilder(
                    context
                    , UserDatabase.class
                    , "user.db")
                    .allowMainThreadQueries()
//                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return instance;
    }

    public abstract UserDao getDao();

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Since we didn't alter the table, there's nothing else to do here.
        }
    };
}
