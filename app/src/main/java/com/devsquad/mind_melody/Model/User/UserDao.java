package com.devsquad.mind_melody.Model.User;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.OnConflictStrategy;

@Dao
public interface UserDao {

    // 1. 获取用户：根据用户 ID 获取用户信息
    @Query("SELECT * FROM User WHERE userId = :userId")
    User getUser(int userId);

    // 2. 注册用户：注册新用户，确保 email 不重复
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long registerUser(User user);

    // 注册用户（检查邮箱是否重复）
    @Query("SELECT COUNT(*) FROM User WHERE userEmail = :email")
    int checkEmailExists(String email);

    // 3. 用户登录：根据 email 和密码查找用户
    @Query("SELECT * FROM User WHERE userEmail = :email AND userPassword = :password")
    User loginUser(String email, String password);

    // 4. 更新用户信息：更新用户的邮箱、密码、姓名
    @Query("UPDATE User SET userEmail = :email, userPassword = :password, firstName = :firstName, lastName = :lastName WHERE userId = :userId")
    void updateUser(int userId, String email, String password, String firstName, String lastName);
}
