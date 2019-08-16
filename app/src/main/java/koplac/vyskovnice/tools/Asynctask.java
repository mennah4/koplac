package koplac.vyskovnice.tools;

import android.content.Context;

import koplac.vyskovnice.MainActivity;

import java.util.List;


/**
 * Send, when possible, the unsent comments stored when there is no internet connection
 * @author Antonio Manuel Sanchez Amat.
 * @author Juan Jose Espinosa Sanchez.
 */
public class Asynctask implements Runnable {
    Context parentContext;

    /**
     * Main constructor
     * @param parentContext contexto de la aplicacion.
     */
    public Asynctask(Context parentContext){
        this.parentContext=parentContext;
    }

    /**
     * Main algorithm, when it has pending comments, it tries to send them, once and again.
     */
    @Override public void run() {
        InternetCheck internetCheck;
        List<String> tasks;
        boolean sqlResult;
        boolean hasModified;

        internetCheck=new InternetCheck();

        hasModified=false;
        while(true) {
            if (internetCheck.getConnectivityStatus(parentContext) != 0) {
                tasks = MainActivity.storeData.loadUnsentComments();
                while (tasks.size() != 0) {
                    //Log.v("pruebas-secondThread", "Starting to do tasks");
                    //Log.v("pruebas-secondThread", "tasks size:" + tasks.size());
                    MainActivity.dbConnection.createConnection();
                    sqlResult = MainActivity.dbConnection.executeDDLDMLSentence(tasks.get(0));
                    //Log.v("pruebas-secondThread", "Task done. Result: " + sqlResult);
                    MainActivity.dbConnection.closeConnection();
                    if (sqlResult) {
                        tasks.remove(0);
                        //Log.v("pruebas-secondThread", "Task deleted");
                        hasModified = true;
                    }
                }
                if (hasModified) {
                    MainActivity.storeData.storeUnsentComments(tasks);
                    hasModified = false;
                    //Log.v("pruebas-secondThread", "No more task to do. Closing secondary thread");
                }
            }
        }
    }
}
