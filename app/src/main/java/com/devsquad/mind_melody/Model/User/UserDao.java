package com.devsquad.mind_melody.Model.User;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.OnConflictStrategy;

import java.util.Date;

@Dao
public interface UserDao {

    // Get User: Get user information based on user ID
    @Query("SELECT * FROM User WHERE userId = :userId")
    User getUser(int userId);

    // Registered Users: Register a new user and make sure the email is not duplicated.
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long registerUser(User user);

    // Registered users (check for duplicate mailboxes)
    @Query("SELECT COUNT(*) FROM User WHERE userEmail = :email")
    int checkEmailExists(String email);

    // User login: lookup user by email and password (deprecated!!! Pair encrypted passwords with bcrypt and log in)
    @Query("SELECT * FROM User WHERE userEmail = :email AND userPassword = :password")
    User loginUser(String email, String password);

    // Update user information: update user's email, password, name
    @Query("UPDATE User SET userEmail = :email, userPassword = :password, firstName = :firstName, lastName = :lastName WHERE userId = :userId")
    void updateUser(int userId, String email, String password, String firstName, String lastName);

    // Get the user's most recent meditation date
    @Query("SELECT lastMeditDate FROM User WHERE userId = :userId LIMIT 1")
    Date getLastMeditDate(int userId);

    // Updating a user's meditation date
    @Query("UPDATE User SET lastMeditDate = :date WHERE userId = :userId")
    void updateLastMeditDate(int userId, Date date);

    // Get user info: get user info based on user's email (no more password lookups)
    @Query("SELECT * FROM User WHERE userEmail = :email")
    User getUserByEmail(String email);

    @Query("UPDATE User SET favouriteMusic = :favouriteMusic WHERE userId = :userId")
    void updateFavouriteMusic(int userId, String favouriteMusic);

    @Query("SELECT favouriteMusic FROM User WHERE userId = :userId")
    String getFavouriteMusic(int userId);
}
