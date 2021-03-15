package kr.co.ainus.petife2.model.room;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface PostDao {

    @Query("select * from post WHERE userIdx = :userIdx order by timestamp desc")
    List<Post> list(long userIdx);

    @Query("delete from post where userIdx = :userIdx")
    void clear(long userIdx);

    @Insert
    void insert(Post post);

    @Update
    void update(Post post);

    @Delete
    void delete(Post post);

}
