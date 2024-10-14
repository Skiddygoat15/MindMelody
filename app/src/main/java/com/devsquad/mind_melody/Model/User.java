package com.devsquad.mind_melody.Model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;
@Entity(tableName = "User")
public class User implements Serializable{
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "userId")
    private int userId;

    @ColumnInfo(name = "userEmail")
    private String userEmail;

    @ColumnInfo(name = "userPassword")
    private String userPassword;

    @ColumnInfo(name = "firstName")
    private String firstName;

    @ColumnInfo(name = "lastName")
    private String lastName;

    @ColumnInfo(name = "registerDate")
    private Date registerDate;

    @ColumnInfo(name = "lastMeditDate")
    private Date lastMeditDate;

    // All-arg constructor
    public User(int userId, String userEmail, String userPassword, String firstName, String lastName, Date registerDate, Date lastMeditDate) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.firstName = firstName;
        this.lastName = lastName;
        this.registerDate = registerDate;
        this.lastMeditDate = lastMeditDate;
    }

    // Getter & Setter
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Date getLastMeditDate() {
        return lastMeditDate;
    }

    public void setLastMeditDate(Date lastMeditDate) {
        this.lastMeditDate = lastMeditDate;
    }

}
