package koplac.vyskovnice.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

import koplac.vyskovnice.entities.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Provides some functions to work with ANDROID SharedPreferences
 * @author Antonio Manuel Sanchez Amat
 * @author Juan Jose Espinosa Sanchez
 */
public class StoreData{
    private Context context;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public final static String USER_LEARNED_DRAWER="user-learned-drawer";
    public final static String USER_PARAMETERS="user-parameters";
    public final static String PEAKS_VISITED="peaks-visited";
    public final static String CHECKPOINTS_VISITED="checkpoints-visited";
    public final static String COMMENTS_MAKED="comments-maked";
    public final static String BOOKS_UNREAD ="books-unread";
    public final static String COMMENTS_UNSENT="comments-unsent";
    public final static String EMPTY="empty";

    /**
     * Main constructor
     * @param context
     */
    public StoreData(Context context){
        this.context=context;
        sp=PreferenceManager.getDefaultSharedPreferences(context);
        editor=sp.edit();
    }

    /**
     * Save a string value
     * @param keyword palabra clave
     * @param value  valor a guardar
     */
    public void store(String keyword,String value){
        editor.putString(keyword, value);
        editor.apply();
    }

    /**
     * Save a integer value
     * @param keyword palabra clave
     * @param value  valor a guardar
     */
    public void store(String keyword,int value){
        editor.putInt(keyword, value);
        editor.apply();
    }

    /**
     * Save a boolean value
     * @param keyword palabra clave
     * @param value  valor a guardar
     */
    public void store(String keyword,boolean value){
        editor.putBoolean(keyword, value);
        editor.apply();
    }

    /**
     * Retrieves a string stored previously
     * @param keyword Identifier of the string
     * @return texto a recuperar
     */
    public String loadString(String keyword){
        return sp.getString(keyword, "");
    }

    /**
     * Recupera un valor en forma de entero
     * @param keyword palabra clave
     * @return entero a recuperar
     */
    public int loadInt(String keyword){
        return sp.getInt(keyword, 0);
    }

    /**
     * Recupera un valor en forma de booleano
     * @param keyword palabra clave
     * @return booleano a recuperar
     */
    public boolean loadBoolean(String keyword){
        return sp.getBoolean(keyword, false);
    }

    /**
     * Guarda los datos de un usuario
     * @param user Objeto usuario a guardar
     */
    public void saveUser(User user){
        String storeString;

        storeString=user.getId()+";"+user.getNickname()+";"+user.getPassword()+";"+user.getEmail()+";";

        if(user.getPicture()==null){storeString+=EMPTY;}
        else{storeString+=Base64.encodeToString(user.getPicture(),Base64.DEFAULT);}

        editor.putString(USER_PARAMETERS,storeString);
        editor.apply();
    }

    /**
     * Recupera un usuario
     * @return Objeto usuario
     */
    public User loadUser(){
        String[] parameters,aux;
        User user;
        int id;
        String nickname,password,email;
        byte[] avatar;
        List<Integer> climbedPeaks;

        user=null;
        if(!sp.getString(USER_PARAMETERS,"").equals("")){
            parameters=sp.getString(USER_PARAMETERS,"").split(";");

            id=Integer.parseInt(parameters[0]);
            nickname=parameters[1];
            password=parameters[2];
            email=parameters[3];
            avatar=null;
            if(!parameters[4].equals(EMPTY)){
                avatar=Base64.decode(parameters[4],Base64.DEFAULT);
            }
            user=new User(id,nickname,password,email,avatar);
        }

        return user;
    }

    /**
     * Borra un usuario
     */
    public void deleteUser(){
        editor.putString(USER_PARAMETERS,"");
        editor.apply();
    }

    /**
     * Comprueba si el usuario ha pasado con anterioridad por un punto de control
     * @param checkpointNumber numero del punto de control
     * @return si el usuario ha pasado por el punto de control o no
     */
    public boolean checkCheckpointVisited(final int checkpointNumber){
        boolean checkpointVisited=false;
        String []aux;
        aux=sp.getString(CHECKPOINTS_VISITED,"").split(";");
        for(int i=1;i<aux.length;i++){
            if(aux[i].equals(checkpointNumber+"")){
                 checkpointVisited=true;
                 break;
            }
        }
        return checkpointVisited;
    }

    /**
     * Comprueba que el usuario ha pasado por todos los puntos de control
     * @param checkpointQuantity numero de puntos de control en el camino
     * @return si el usuario ha pasado por todos los puntos de control
     */
    public boolean checkLastCheckpoint(int checkpointQuantity){
        boolean lastCheckpoint=false;
        String[] aux;
        aux = sp.getString(CHECKPOINTS_VISITED, "").split(";");
        if (aux.length==checkpointQuantity+1){
            lastCheckpoint=true;
        }

        return lastCheckpoint;
    }

    /**
     * Guarda que el usuario ha llegado a un punto de control determinado
     * @param userId id del usuario
     * @param checkpointNumber numero del punto de control
     * @return si la operacion se ha realizado o no
     */
    public boolean storeUsersReachedCheckpoints(int userId ,int checkpointNumber){
        String storeString;
        String[] aux;
        boolean result;

        result=false;
        if(sp.getString(CHECKPOINTS_VISITED,"").equals("")){
            result=true;
            editor.putString(CHECKPOINTS_VISITED, userId+";"+checkpointNumber);
            editor.apply();
        }
        else{
            storeString=sp.getString(CHECKPOINTS_VISITED, "");
            aux=storeString.split(";");
            if(aux[0].equals(userId+"")){
                result=true;
                editor.putString(CHECKPOINTS_VISITED,storeString+";"+checkpointNumber);
                editor.apply();
            }
        }
        //Log.v("pruebas","Checkpoint visited: "+sp.getString(CHECKPOINTS_VISITED,""));
        return result;
    }

    /**
     * Guarda que el usuario ha llegado a un pico determinado en una fecha determinada
     * @param userId id del usuario
     * @param peakId id del pico
     * @return si la operacion se ha realizado o no
     */
    public boolean storeUsersReachedPeaks(int userId,int peakId){
        SimpleDateFormat sdf;
        Date date;
        String storeString;

        String[] dateUserIds,aux;
        boolean userFound,peakFound;

        sdf=new SimpleDateFormat("dd-MM-yyyy");
        date=new Date();
        userFound=false;
        peakFound=false;
        if(sp.getString(PEAKS_VISITED,"").equals("")){
            storeString=sdf.format(date)+";"+userId+","+peakId;
        }
        else{
            dateUserIds=sp.getString(PEAKS_VISITED,"").split(";");
            if(!dateUserIds[0].equals(sdf.format(date))){
                storeString=sdf.format(date)+";"+userId+","+peakId;
            }
            else{
                for(int i=1;(i<dateUserIds.length)&&(!userFound);i++){
                    aux=dateUserIds[i].split(",");
                    if(aux[0].equals(userId+"")){
                        userFound=true;
                        for(int j=1;(j<aux.length)&&(!peakFound);j++){
                            if(aux[j].equals(peakId+"")){
                                peakFound=true;
                            }
                        }
                        if(!peakFound){
                            dateUserIds[i]+=","+peakId;
                        }
                    }
                }
                storeString="";
                for(String s:dateUserIds){
                    if(storeString.equals("")) {
                        storeString=s;
                    }
                    else{
                        storeString+=";"+s;
                    }
                }
                if(!userFound){
                    storeString+=";"+userId+","+peakId;
                }
            }
        }
        editor.putString(PEAKS_VISITED,storeString);
        //Log.v("pruebas", "StoreData - PEAKS_VISITED: " + storeString);
        editor.apply();
        if(userFound&&peakFound){
            //El usuario ya ha visitado el lugar hoy.
            return false;
        }
        else{
            //Realizo un guardado, es decir, el usuario no habia visitado el lugar hoy
            return true;
        }
    }

    /**
     * Comprueba si un usuario ha estado en un pico determinado
     * @param userId id del usuario
     * @param peakId id del pico
     * @return si el usuario ha estado en el pico o no
     */
    public boolean checkUsersReachedPeaks(int userId,int peakId){
        SimpleDateFormat sdf;
        Date date;
        String storeString;

        String[] dateUserIds,aux;
        boolean userFound,peakFound;

        sdf=new SimpleDateFormat("dd-MM-yyyy");
        date=new Date();
        userFound=false;
        peakFound=false;
        if(sp.getString(PEAKS_VISITED,"").equals("")){
            return false;
        }
        else{
            dateUserIds=sp.getString(PEAKS_VISITED,"").split(";");
            if(!dateUserIds[0].equals(sdf.format(date))){
                return false;
            }
            else{
                for(int i=1;(i<dateUserIds.length)&&(!userFound);i++){
                    aux=dateUserIds[i].split(",");
                    if(aux[0].equals(userId+"")){
                        userFound=true;
                        for(int j=1;(j<aux.length)&&(!peakFound);j++){
                            if(aux[j].equals(peakId+"")){
                                peakFound=true;
                            }
                        }
                    }
                }
                storeString="";
                for(String s:dateUserIds){
                    if(storeString.equals("")) {
                        storeString=s;
                    }
                    else{
                        storeString+=";"+s;
                    }
                }
            }
        }
        editor.putString(PEAKS_VISITED, storeString);
        //Log.v("pruebas", "StoreData - PEAKS_VISITED: " + storeString);
        editor.apply();
        if(userFound&&peakFound){
            //El usuario ya ha visitado el lugar hoy.
            return true;
        }
        else{
            //Realizo un guardado, es decir, el usuario no habia visitado el lugar hoy
            return false;
        }
    }

    /**
     * Comprueba si el usuario ya ha escrito un comentario ese dia en un pico determinado
      * @param userId id del usuario
     * @param peakId id del pico
     * @return si ha escrito el comentario o no
     */
    public boolean checkUsersReachedPeaksComments(int userId,int peakId){
        SimpleDateFormat sdf;
        Date date;
        String storeString;

        String[] dateUserIds,aux;
        boolean userFound,peakFound;

        sdf=new SimpleDateFormat("dd-MM-yyyy");
        date=new Date();
        userFound=false;
        peakFound=false;
        if(sp.getString(COMMENTS_MAKED,"").equals("")){
            return false;
        }
        else{
            dateUserIds=sp.getString(COMMENTS_MAKED,"").split(";");
            if(!dateUserIds[0].equals(sdf.format(date))){
                return false;
            }
            else{
                for(int i=1;(i<dateUserIds.length)&&(!userFound);i++){
                    aux=dateUserIds[i].split(",");
                    if(aux[0].equals(userId+"")){
                        userFound=true;
                        for(int j=1;(j<aux.length)&&(!peakFound);j++){
                            if(aux[j].equals(peakId+"")){
                                peakFound=true;
                            }
                        }
                    }
                }
                storeString="";
                for(String s:dateUserIds){
                    if(storeString.equals("")) {
                        storeString=s;
                    }
                    else{
                        storeString+=";"+s;
                    }
                }
            }
        }
        editor.putString(COMMENTS_MAKED, storeString);
        //Log.v("pruebas", "StoreData - COMMENTS_MAKED: " + storeString);
        editor.apply();
        if(userFound&&peakFound){
            //El usuario ya ha comentado el lugar hoy.
            return true;
        }
        else{
            //Realizo un guardado, es decir, el usuario no habia comentado el lugar hoy
            return false;
        }
    }

    /**
     * Almacena que el usuario ha escrito un comentario en un pico determinado en el dia actual
     * @param userId id del usuario
     * @param peakId id del pico
     * @return si la operacion se ha realizado o o no
     */
    public boolean storeUsersReachedPeaksComments(int userId,int peakId){
        SimpleDateFormat sdf;
        Date date;
        String storeString;

        String[] dateUserIds,aux;
        boolean userFound,peakFound;

        sdf=new SimpleDateFormat("dd-MM-yyyy");
        date=new Date();
        userFound=false;
        peakFound=false;
        if(sp.getString(COMMENTS_MAKED,"").equals("")){
            storeString=sdf.format(date)+";"+userId+","+peakId;
        }
        else{
            dateUserIds=sp.getString(COMMENTS_MAKED,"").split(";");
            if(!dateUserIds[0].equals(sdf.format(date))){
                storeString=sdf.format(date)+";"+userId+","+peakId;
            }
            else{
                for(int i=1;(i<dateUserIds.length)&&(!userFound);i++){
                    aux=dateUserIds[i].split(",");
                    if(aux[0].equals(userId+"")){
                        userFound=true;
                        for(int j=1;(j<aux.length)&&(!peakFound);j++){
                            if(aux[j].equals(peakId+"")){
                                peakFound=true;
                            }
                        }
                    }
                }
                storeString="";
                for(String s:dateUserIds){
                    if(storeString.equals("")) {
                        storeString=s;
                    }
                    else{
                        storeString+=";"+s;
                    }
                }
                if(!userFound){
                    storeString+=";"+userId+","+peakId;
                }
            }
        }
        editor.putString(COMMENTS_MAKED,storeString);
        //Log.v("pruebas", "StoreData - COMMENTS_MAKED: " + storeString);
        editor.apply();
        if(userFound&&peakFound){
            //El usuario ya ha comentado el lugar hoy.
            return false;
        }
        else{
            //Realizo un guardado, es decir, el usuario no habia comentado el lugar hoy
            return true;
        }
    }

    /**
     * Almacena un comentario
     * @param sqlSentence Sentencia SQL
     */
    public void storeAddUnsentComment(String sqlSentence){
        String unsentComments;

        unsentComments=sp.getString(COMMENTS_UNSENT,"");
        if(unsentComments.equals("")){
            editor.putString(COMMENTS_UNSENT,sqlSentence);
            editor.apply();
        }
        else{
            unsentComments+="#"+sqlSentence;
            editor.putString(COMMENTS_UNSENT,unsentComments);
            editor.apply();
        }
        //Log.v("pruebas","StoreDate-AddComment-UnsentComments:"+sp.getString(COMMENTS_UNSENT,""));
    }

    /**
     * Almacena una lista de comentarios
     * @param unsentComments Lista de comentarios
     */
    public void storeUnsentComments(List<String> unsentComments){
        String storeString;

        storeString="";
        if(unsentComments!=null){
            for(String s:unsentComments){
                if(storeString.equals("")){
                    storeString=s;
                }
                else{
                    storeString+="#"+s;
                }
            }
        }
        //Log.v("pruebas","StoreDate-StoreUnsentComments-UnsentComments:"+sp.getString(COMMENTS_UNSENT,""));
        editor.putString(COMMENTS_UNSENT,storeString);
        editor.apply();
    }

    /**
     * Recupera una lista de comentarios
     * @return Lista de comentarios
     */
    public List<String> loadUnsentComments(){
        List<String> unsentComments;
        String[] aux;

        unsentComments=new LinkedList();
        if(!sp.getString(COMMENTS_UNSENT,"").equals("")){
            aux=sp.getString(COMMENTS_UNSENT,"").split("#");
            for(String s:aux){
                unsentComments.add(s);
            }
        }
        //Log.v("pruebas", "StoreDate-LoadUnsentComments-UnsentComments:" + sp.getString(COMMENTS_UNSENT, ""));
        return unsentComments;
    }

    /**
     * Almacena el nombre del libro para poder leer sus comentarios con posterioridad
     * @param userId id del usuario
     * @param peakId id del pico
     * @param sqlSentence sentencia SQL.
     */
    public void storeAddUnreadBook(int userId,int peakId,String sqlSentence){
        String unreadBooks;

        unreadBooks=sp.getString(BOOKS_UNREAD,"");
        if(unreadBooks.equals("")){
            editor.putString(BOOKS_UNREAD,userId+"~"+peakId+"~"+sqlSentence);
            editor.apply();
        }
        else{
            unreadBooks+="#"+userId+"~"+peakId+"~"+sqlSentence;
            editor.putString(BOOKS_UNREAD,unreadBooks);
            editor.apply();
        }
        //Log.v("pruebas", "StoreData - BOOKS_UNREAD:" + sp.getString(BOOKS_UNREAD, ""));
    }

    /**
     * Recupera la lista de libros no leidos
     * @param userId id del usuario
     * @return Lista de libros no leidos
     */
    public List<String[]> loadUserUnreadBooks(int userId){
        List<String[]> unreadBooks;
        String[] aux,aux2;

        unreadBooks=new LinkedList();
        if(!sp.getString(BOOKS_UNREAD,"").equals("")){
            unreadBooks=new LinkedList();
            aux=sp.getString(BOOKS_UNREAD,"").split("#");
            for(String s:aux){
                aux2=s.split("~");
                //Log.v("pruebas", "StoreData-s:"+s);
                //Log.v("pruebas", "StoreData-aux2[0]:"+aux2[0]);
                //Log.v("pruebas", "StoreData-aux2[1]:"+aux2[1]);
                //Log.v("pruebas", "StoreData-aux2[2]:"+aux2[2]);
                if(aux2[0].equals(userId+"")){
                    unreadBooks.add(aux2);
                }
            }
        }
        //Log.v("pruebas", "StoreData - BOOKS_UNREAD:" + sp.getString(BOOKS_UNREAD, ""));
        return unreadBooks;
    }

    /**
     * Borra un libro
     * @param userId id del usuario
     * @param peakId id del pico
     */
    public void deleteUserUnreadBook(int userId,int peakId){
        boolean userFound,peakFound;
        String[] unreadBooks,unreadBook;
        String storeString;

        userFound=false;
        peakFound=false;
        if(!sp.getString(BOOKS_UNREAD,"").equals("")){
            unreadBooks=sp.getString(BOOKS_UNREAD,"").split("#");
            for(int i=0;(i<unreadBooks.length)&&(!userFound)&&(!peakFound);i++){
                unreadBook=unreadBooks[i].split("~");
                if((unreadBook[0].equals(userId+""))&&(unreadBook[1].equals(peakId+""))){
                    userFound=true;
                    peakFound=true;
                    unreadBooks[i]="";
                }
            }
            storeString="";
            for(String s:unreadBooks){
                if(storeString.equals("")){
                    storeString=s;
                }
                else{
                    storeString+="#"+s;
                }
            }
            editor.putString(BOOKS_UNREAD,storeString);
            editor.apply();
        }
        //Log.v("pruebas","StoreData-deleteUserUnreadBook - BOOKS_UNREAD: "+sp.getString(BOOKS_UNREAD,""));

    }

}