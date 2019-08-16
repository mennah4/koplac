package koplac.vyskovnice.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import koplac.vyskovnice.MainActivity;
import koplac.vyskovnice.R;
import koplac.vyskovnice.tools.InternetCheck;

/**
 * Gives functionality and defines the screen that appears when
 * a user reach a peak or a checkpoint
 * @author Antonio Manuel Sanchez Amat
 * @author Juan Jose Espinosa Sanchez
 */
public class CheckPointReached extends AppCompatActivity {

    private TextView tvCheckPointIdCount,tvTrackName;

    private int userId,trackId,checkPointId,checkPointsCount;
    private String trackName;

    private BackgroundTasks bgt;
    private InternetCheck ic;

    /**
     * Triggers at Activitys beginning, instances all Activities elements.
     * @param savedInstanceState
     */
    @Override protected void onCreate(Bundle savedInstanceState) {
        Bundle extras;

        super.onCreate(savedInstanceState);
        ic=new InternetCheck();

        extras=getIntent().getExtras();
        /* Checks is not the goal checkpoint to shows the screen
           with the checkpoint number
        */
        if(extras.getBoolean("goal")==false){
            setContentView(R.layout.activity_check_point_reached);

            tvCheckPointIdCount=(TextView)findViewById(R.id.tvCheckPointIdCount);
            tvTrackName=(TextView)findViewById(R.id.tvTrackName);

            trackName=extras.getString("trackName");
            userId= MainActivity.user.getId();
            trackId=extras.getInt("trackId");
            checkPointId=extras.getInt("checkPointId");
            checkPointsCount=extras.getInt("checkPointsCount");

            tvCheckPointIdCount.setText(checkPointId + " / " + checkPointsCount);
            tvTrackName.setText(trackName);
        }
        // Shows the congratulations screen when the user finish the path
        else{
            setContentView(R.layout.activity_goal_reached);

            tvTrackName=(TextView)findViewById(R.id.tvTrackName);

            trackName=extras.getString("trackName");
            userId=MainActivity.user.getId();
            trackId=extras.getInt("trackId");
            checkPointId=extras.getInt("checkPointId");

            tvTrackName.setText(trackName);
        }

    }

    /**
     * Warning message at pressing the device back button, notifyng that the changes
     * will not be saved if the user leaves the screen
     */
    @Override public void onBackPressed(){
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle(getResources().getString(R.string.exitWithoutCheckIn))
                .setMessage(getResources().getString(R.string.areYouSureYouWantToContinueWithoutCheckInThisPoint))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton(getResources().getString(R.string.no), null).show();

    }
    /**
        Gives functionality to the accept button
     */
    public void accept(View v){
        if(ic.getConnectivityStatus(getApplicationContext())!=0){
            bgt=new BackgroundTasks(CheckPointReached.this,userId,trackId,checkPointId);
            bgt.execute();
        }
        else{
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.noInternet),Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.internetConnectionNeededIfYouWantToRecordCheckpointsTracks),Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Makes the changes in the DB while a progress animation appears
     */
    private class BackgroundTasks extends AsyncTask<Void,Integer,Boolean> {
        private final Context parentContext;
        private final int userId,trackId,checkPointId;
        private final ProgressDialog progressDialog;

        public BackgroundTasks(final Context parentContext,final int userId,final int trackId,final int checkPointId){
            this.parentContext=parentContext;
            this.userId=userId;
            this.trackId=trackId;
            this.checkPointId=checkPointId;

            this.progressDialog=new ProgressDialog(parentContext);
            this.progressDialog.setTitle(parentContext.getResources().getString(R.string.loading));
            this.progressDialog.setCancelable(false);
            this.progressDialog.setIndeterminate(true);
        }

        @Override protected void onPreExecute(){
            progressDialog.show();
        }
        @Override protected Boolean doInBackground(Void... params){
            Boolean result;

            publishProgress(R.string.waitForCheckIn);
            result=MainActivity.dbConnection.sendCheckpointReached(userId,trackId,checkPointId);

            return result;
        }
        @Override protected void onProgressUpdate(Integer... values) {
            progressDialog.setMessage(getResources().getString(values[0]));
        }
        @Override protected void onPostExecute(Boolean result) {
            progressDialog.dismiss();
            if(result){
                Toast.makeText(parentContext, getResources().getString(R.string.checkInSucceed),Toast.LENGTH_SHORT).show();
                finish();
            }
            else{
                Toast.makeText(parentContext,getResources().getString(R.string.checkInFailure),Toast.LENGTH_LONG).show();
                Toast.makeText(parentContext,getResources().getString(R.string.makeSureYouHaveInternetConnectionAndTryAgain),Toast.LENGTH_LONG).show();
            }
        }
    }
}
