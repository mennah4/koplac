package koplac.vyskovnice.tools;


import android.support.v7.app.AppCompatActivity;

import koplac.vyskovnice.entities.Comment;
import koplac.vyskovnice.MainActivity;
import koplac.vyskovnice.entities.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * Library with all functions necessary for work with mysql databases
 * @author Antonio Manuel Sanchez Amat
 * @author Juan Jose Espinosa Sanchez
 */
public class DBConnection extends AppCompatActivity {

    private String user,password,schema,serverAddress;
    private int port;

    public Cypher cypher;
    private Connection connection;

    public DBConnection(String user, String password, String schema, String serverAddress, int port){
        this.user=user;
        this.password=password;
        this.schema=schema;
        this.serverAddress=serverAddress;
        this.port=port;

        this.connection=null;
        this.cypher=new Cypher("SHA-1");
    }

    /**
     * Create a new connection.
     */
    public void createConnection() {
        try {
            if(this.connection!=null){
                this.closeConnection();
            }
            else{
                Class.forName("com.mysql.jdbc.Driver");
                this.connection=DriverManager.getConnection("jdbc:mysql://"+serverAddress+"/"+schema+"",user,password);
            }
        }
        catch(Exception e){e.printStackTrace();}
    }

    /**
     * Close the current connection.
     */
    public void closeConnection() {
        try{
            if(this.connection!=null){
                this.connection.close();
                this.connection=null;
            }
        }
        catch(SQLException e){e.printStackTrace();}
    }

    /**
     * Execute only-boolean-result querys
     * @param sqlQuery consulta que se quiere realizar
     * @return si la consulta da algun resultado o no
     */
    public boolean executeDDLDMLSentence(String sqlQuery){
        try{
            this.connection.createStatement().executeUpdate(sqlQuery);
            return true;
        }
        catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Executes non-boolean-results querys
     * @param sqlQuery SQL sentence
     * @return Set of results of the current query
     */
    public ResultSet executeSelectSentence(String sqlQuery){
        ResultSet sqlResult;

        sqlResult=null;
        try{
            sqlResult=this.connection.createStatement().executeQuery(sqlQuery);
        }
        catch(SQLException excepcion){
            excepcion.printStackTrace();
            return null;
        }
        return sqlResult;
    }

    /**
     * Checks if current email is already at the database
     * @param email Current email
     * @return SQL boolean result
     */
    public boolean isValidEmail(String email){
        boolean isValid;
        ResultSet sqlResult;

        isValid=true;
        try{
            this.createConnection();
            sqlResult=this.executeSelectSentence("SELECT email FROM users WHERE email='" + normalizeString(email) + "';");
            while(sqlResult.next()){
                isValid=false;
            }
            this.closeConnection();
        }
        catch(Exception exception){exception.printStackTrace();}

        return isValid;
    }

    /**
     * Checks if current nickname is already at the database
     * @param nickname Current nickname
     * @return SQL boolean result
     */
    public boolean isValidNickname(String nickname){
        boolean isValid;
        ResultSet sqlResult;

        isValid=true;
        try{
            this.createConnection();
            sqlResult=this.executeSelectSentence("SELECT nickname FROM users WHERE nickname='" + normalizeString(nickname) + "';");
            while(sqlResult.next()){
                isValid=false;
            }
            this.closeConnection();
        }
        catch(Exception exception){exception.printStackTrace();}

        return isValid;
    }

    /**
     * Insert a new user in users table at database
     * @param nickname Users nickname
     * @param password Users password
     * @param email Users email
     * @param pictureName Name of Users profile image
     * @return SQL boolean result
     */
    public boolean signinUser(String nickname,String password,String email,String pictureName){
        boolean result;

        result=false;
        try{
            this.createConnection();
            result=this.executeDDLDMLSentence("INSERT INTO users (id,nickname,password,email,picture) VALUES(null,'" + normalizeString(nickname) + "','" + cypher.stringCypher(password) + "','" + normalizeString(email) + "','" + normalizeString(pictureName) + "');");
            this.closeConnection();
        }
        catch(Exception exception){exception.printStackTrace();}

        return result;
    }

    /**
     * Checks if current user is already at the database
     * @param email Users email
     * @param password Users password
     * @return If current user exists, returns an user java object with its properties
     */
    public User login(String email,String password){
        boolean userFound;
        ResultSet sqlResult;

        User user;
        int id;
        String nickname,avatarName;
        byte[] avatar;

        user=null;
        id=-1;
        nickname="";
        avatarName="";
        userFound=false;
        try{
            this.createConnection();
            sqlResult=this.executeSelectSentence("SELECT id,nickname,picture FROM users WHERE email='" + normalizeString(email) + "' AND password='" + cypher.stringCypher(password) + "';");
            while(sqlResult.next()){
                userFound=true;
                id=sqlResult.getInt("id");
                nickname=sqlResult.getString("nickname");
                avatarName=sqlResult.getString("picture");
            }
            this.closeConnection();
        }
        catch(Exception exception){exception.printStackTrace();}
        if(userFound){
            avatar=null;
            if(!avatarName.equals("")){
                MainActivity.ftpConection.createConnection();
                avatar= MainActivity.ftpConection.receiveStream(FTPConnection.AVATAR_IMAGES_DIRECTORY,avatarName);
                MainActivity.ftpConection.closeConnection();
            }
            user=new User(id,nickname,cypher.stringCypher(password),email,avatar);
        }
        return user;
    }

    /**
     * Check what peaks some user has climbed
     * @param userId Current users id
     * @return List of Peaks
     */
    public List<Integer> getUserClimbedPeaks(int userId){
        List<Integer> peaks;
        ResultSet sqlResult;

        peaks=null;
        try{
            this.createConnection();
            sqlResult=this.executeSelectSentence("SELECT peak_id FROM book_comments WHERE user_id=" + userId + " GROUP BY peak_id ORDER BY date DESC;");
            peaks=new LinkedList<>();
            while(sqlResult.next()){
                peaks.add(new Integer(sqlResult.getInt("peak_id")));
            }
            this.closeConnection();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return peaks;
    }

    /**
     * Checks when a user has reached current peak
     * @param userId Current users id
     * @param peakId Current peak id
     * @return List of String[] with dates
     */
    public List<String> getReachedPeakUserHistory(int userId,int peakId){
        List<String> aux;
        ResultSet sqlResult;

        aux=null;
        try{
            this.createConnection();
            sqlResult=this.executeSelectSentence("SELECT date FROM book_comments WHERE user_id="+userId+" AND peak_id="+peakId+" ORDER BY date DESC;");
            aux=new LinkedList();
            while(sqlResult.next()){
                aux.add(sqlResult.getString("date"));
            }
            this.closeConnection();
        }catch(Exception exception){exception.printStackTrace();}

        return aux;
    }
    public String getUserLastClimbedPeakHistory(int userId){
        String aux;
        ResultSet sqlResult;

        aux=null;
        try{
            this.createConnection();
            sqlResult=this.executeSelectSentence("SELECT date FROM book_comments WHERE user_id="+userId+";");

            if(sqlResult.next()){
                aux =sqlResult.getString("date");
            }
            this.closeConnection();
        }catch(Exception exception){exception.printStackTrace();}

        return aux;
    }
    public String getUserName(int userId){
        String username;
        ResultSet sqlResult;

        username=null;
        try{
            this.createConnection();
            sqlResult=this.executeSelectSentence(
                    "SELECT nickname FROM users WHERE id="+userId+";");
            if(sqlResult.next()){
                username = sqlResult.getString("nickname");
            }



            this.closeConnection();
        }catch(Exception exception){exception.printStackTrace();}

        return username;
    }
    public String getUserLastClimbedPeak(int userId){
        String lastPeak;
        ResultSet sqlResult;

        lastPeak=null;
        try{
            this.createConnection();
            sqlResult=this.executeSelectSentence(
                    "SELECT name FROM peaks " +
                            "WHERE id = (SELECT peak_id FROM book_comments " +
                                "WHERE user_id="+userId+" AND date = (Select max(date) FROM book_comments WHERE user_id="+userId+"));");
            if(sqlResult.next()){
                lastPeak = sqlResult.getString("name");
            }



            this.closeConnection();
        }catch(Exception exception){exception.printStackTrace();}

        return lastPeak;
    }
    public String getUserLastClimbedPeakDate(int userId){
        String lastPeak;
        ResultSet sqlResult;

        lastPeak=null;
        try{
            this.createConnection();
            sqlResult=this.executeSelectSentence(
                    "SELECT date FROM book_comments " +
                            "WHERE user_id="+userId+" AND date = (Select max(date) FROM book_comments WHERE user_id="+userId+");");
            if(sqlResult.next()){
                lastPeak = sqlResult.getString("date");
            }



            this.closeConnection();
        }catch(Exception exception){exception.printStackTrace();}

        return lastPeak;
    }
    public String getUserLastClimbedPeakComment(int userId){
        String lastPeakComment;
        ResultSet sqlResult;

        lastPeakComment=null;
        try{
            this.createConnection();
            sqlResult=this.executeSelectSentence(
                    "SELECT comment FROM book_comments " +
                            "WHERE user_id="+userId+" AND date = (Select max(date) FROM book_comments WHERE user_id="+userId+");");
            if(sqlResult.next()){
                lastPeakComment = sqlResult.getString("comment");
            }

            this.closeConnection();
        }catch(Exception exception){exception.printStackTrace();}

        return lastPeakComment;
    }
    public int getUserLastClimbedPeakid(int userId){
        int lastPeak = 0;
        ResultSet sqlResult;


        try{
            this.createConnection();
            sqlResult=this.connection.createStatement().executeQuery(
                    "SELECT peak_id FROM book_comments " +
                            "WHERE user_id="+userId+" AND date = (Select max(date) FROM book_comments WHERE user_id="+userId+");");
            if(sqlResult.next()){
                lastPeak = sqlResult.getInt("peak_id");
            }



            this.closeConnection();
        }catch(Exception exception){exception.printStackTrace();}

        return lastPeak-1;
    }
    /**
     * Modifies current users password
     * @param userId Current users id
     * @param newPass New password
     * @return SQL boolean result
     */
    public boolean changePassword(int userId,String newPass){
        boolean result;

        result=false;
        try{
            this.createConnection();
            result=this.executeDDLDMLSentence("UPDATE users SET password='"+cypher.stringCypher(newPass)+"' WHERE id="+userId+";");
            this.closeConnection();
        }
        catch(Exception exception){exception.printStackTrace();}

        return result;
    }
    public boolean changeUserName(int userId,String newUserName){
        boolean result;

        result=false;
        try{
            this.createConnection();
            result=this.executeDDLDMLSentence("UPDATE users SET nickname='"+newUserName+"' WHERE id="+userId+";");
            this.closeConnection();
        }
        catch(Exception exception){exception.printStackTrace();}

        return result;
    }
    public boolean changeImage(int userId,String imageName) {
        boolean result;

        result=false;
        try{
            this.createConnection();
            result=this.executeDDLDMLSentence("UPDATE users SET picture='"+imageName+"' WHERE id="+userId+";");
            this.closeConnection();
        }
        catch(Exception exception){exception.printStackTrace();}

        return result;
    }

    /**
     * Retrieves the top list of best climbers
     * @return List of String[] with SQL results
     */
    public List<String[]> getBestClimbersRanking(){
        ResultSet sqlResult;
        List<String[]> bestClimbers;
        int posicion;

        bestClimbers=null;
        try {
            this.createConnection();
            sqlResult=this.connection.createStatement().executeQuery("SELECT u.nickname AS 'userNickname',count(peak_id) AS 'reachedPeaks' FROM users u,book_comments b WHERE u.id=b.user_id GROUP BY b.user_id ORDER BY count(peak_id) DESC;");
            bestClimbers=new LinkedList();
            posicion=1;
            while(sqlResult.next()){
                String[] items={posicion+"",sqlResult.getString("userNickname"),sqlResult.getInt("reachedPeaks")+""};
                bestClimbers.add(items);
                posicion++;
            }

            this.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bestClimbers;
    }

    /**
     * Retrieves the top list of most climbed peaks
     * @return List of String[] with SQL results
     */
    public List<String[]> getMostClimbedPeaksRanking(){
        ResultSet sqlResult;
        List<String[]> mostClimbedPeaks;
        int posicion;

        mostClimbedPeaks=new LinkedList();
        try {
            this.createConnection();
            sqlResult=this.connection.createStatement().executeQuery("SELECT p.name AS 'peakName',count(peak_id) AS 'reachedTimes' FROM peaks p,book_comments b WHERE b.peak_id=p.id GROUP BY peak_id ORDER BY count(peak_id) DESC;");
            posicion=1;
            while(sqlResult.next()){
                String[] items={posicion+"",sqlResult.getString("peakName"),sqlResult.getInt("reachedTimes")+""};
                mostClimbedPeaks.add(items);
                posicion++;
            }
            this.closeConnection();
        }
        catch(Exception e){e.printStackTrace();}

        return mostClimbedPeaks;
    }

    /**
     * Retrieves the comments of the current peak
     * @param peakId Id of the current peak
     * @return List of Comment
     */
    public List<Comment> getBookComments(int peakId){
        ResultSet sqlResult;
        List<Comment> bookComments;
        Comment comment;
        SimpleDateFormat sdf;

        sdf=new SimpleDateFormat("dd-MM-yyyy");
        bookComments=null;
        try {
            this.createConnection();
            sqlResult=this.connection.createStatement().executeQuery("SELECT nickname,comment,date FROM users,book_comments WHERE users.id=book_comments.user_id AND peak_id="+peakId+" ORDER BY date ASC;");
            bookComments=new LinkedList();
            while(sqlResult.next()){
                comment=new Comment(sqlResult.getString("nickname"),sdf.format(sqlResult.getDate("date")),sqlResult.getString("comment"));
                bookComments.add(comment);
            }
            this.closeConnection();
        }
        catch(Exception e){e.printStackTrace();}

        return bookComments;
    }

    /**
     * Retrieves the comments of current peak util a date
     * @param sqlSentence SQL sentence
     * @return List of Comment
     */
    public List<Comment> getBookComments(String sqlSentence){
        ResultSet sqlResult;
        List<Comment> bookComments;
        Comment comment;
        SimpleDateFormat sdf;

        sdf=new SimpleDateFormat("dd-MM-yyyy");
        bookComments=null;
        try {
            this.createConnection();
            sqlResult=this.connection.createStatement().executeQuery(sqlSentence);
            bookComments=new LinkedList();
            while(sqlResult.next()){
                comment=new Comment(sqlResult.getString("nickname"),sdf.format(sqlResult.getDate("date")),sqlResult.getString("comment"));
                bookComments.add(comment);
            }
            this.closeConnection();
        }
        catch(Exception e){e.printStackTrace();}

        return bookComments;
    }

    /**
     * Writes a comment in book_comments table
     * @param peakId Id of current peak
     * @param userId Current users id
     * @param bookStatus Book status
     * @param date Date when was reached
     * @param comment Content of comment
     * @return SQL boolean result
     */
    public boolean sendComment(int peakId, int userId, int bookStatus,String date, String comment) {
        boolean result;
        this.createConnection();
        result=this.executeDDLDMLSentence("INSERT INTO book_comments VALUES("+peakId+","+userId+","+bookStatus+",'"+date+"','"+MainActivity.dbConnection.normalizeString(comment)+"');");
        this.closeConnection();

        return result;
    }

    /**
     * Writes if a user have reached a checkpoint
     * @param userId Current users id
     * @param trackId Id of current track
     * @param checkpointId Id of current checkpoint
     * @return SQL boolean result
     */
    public boolean sendCheckpointReached(final int userId,final int trackId,final int checkpointId){
        boolean result;
        String sqlSentence;

        result=false;
        sqlSentence="INSERT INTO users_tracks_checkpoints VALUES("+userId+","+trackId+","+checkpointId+",curdate(),curtime());";
        //Log.v("pruebas", "SQL checkpoint reached sending: " + sqlSentence);
        try{
            createConnection();
            result=executeDDLDMLSentence(sqlSentence);
            closeConnection();
        }
        catch(Exception e){e.printStackTrace();}

        return result;
    }

    /**
     * Retrieves the top list of Best Runners
     * @param trackId Id of current track
     * @return List of String[] with SQL results
     */
    public List<String[]> getRunners(final int trackId){
        List<String[]> result;
        ResultSet sqlResult;
        SimpleDateFormat dmy,hms;

        dmy=new SimpleDateFormat("dd-MM-yyyy");
        hms=new SimpleDateFormat("HH:mm:ss");
        result=new LinkedList();
        try{
            createConnection();
            sqlResult=executeSelectSentence("SELECT userNickname, checkpoints, lastDateReached, lastTimeReached FROM (SELECT u.nickname AS  'userNickname',COUNT(utc.id) AS  'checkpoints',MAX(utc.date) AS  'lastDateReached',MAX(utc.time) AS  'lastTimeReached' FROM users_tracks_checkpoints utc,users u WHERE u.id=utc.user_id AND track_id="+trackId+" GROUP BY utc.user_id) a ORDER BY lastDateReached,lastTimeReached ASC;");
            while(sqlResult.next()){
                result.add(new String[]{sqlResult.getString("userNickname"),sqlResult.getInt("checkpoints")+"",dmy.format(sqlResult.getDate("lastDateReached")),hms.format(sqlResult.getTime("lastTimeReached"))});
            }
            closeConnection();
        }
        catch(Exception e){e.printStackTrace();}

        return result;
    }

    /**
     * Delete all simple commas and doble commas of current string
     * @param string Current string
     * @return Manipulated string
     */
    public String normalizeString(String string){
        String normalized;

        normalized=string.replaceAll("'","");
        normalized=normalized.replaceAll("\"","");

        return normalized;

    }
}

