package koplac.vyskovnice.entities;

/**
 * Definition of the user object
 * @author Antonio Manuel Sanchez Amat
 * @author Juan Jose Espinosa Sanchez
 */
public class User {
    private int id;
    private String nickname,password,email;
    private byte[] picture;

    /**
     * Contructor method
     * @param nickname apodo
     * @param password contrasena
     */
    public User(String nickname,String password){
        this.nickname=nickname;
        this.password=password;
    }

    /**
     * Constructor method
     * @param id identification number
     * @param nickname nickname
     * @param password password
     * @param email email address
     * @param picture picture
     */
    public User(int id,String nickname,String password,String email,byte[] picture){
        this.id=id;
        this.nickname=nickname;
        this.password=password;
        this.email=email;
        this.picture=picture;
    }

    /**
     * Returns the user id
     * @return user id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the user nickname
     * @return user nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Returns the user password
     * @return user password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Returns the user email address
     * @return user email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the user picture
     * @return user picture
     */
    public byte[] getPicture() {
        return picture;
    }

    /**
     * Assign a identification to the user
     * @param id user identification
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Assign a nickname to the user
     * @param nickname user nickname
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Assign a password to the user
     * @param password user password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Assign a email address to the user
     * @param email user email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Assign a picture to the user profile
     * @param picture user picture
     */
    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

}
