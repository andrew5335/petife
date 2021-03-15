package kr.co.ainus.petife2.model.room;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UserDao {

    @Query("select * from User")
    List<User> list();

    @Query("select * from User where email = :email and provider = :provider")
    User select(String email, String provider);

    @Query("delete from User")
    void clear();

    @Insert
    void insert(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);
}
