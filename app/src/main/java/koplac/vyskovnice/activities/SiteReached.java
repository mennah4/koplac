package koplac.vyskovnice.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import koplac.vyskovnice.MainActivity;
import koplac.vyskovnice.R;
import koplac.vyskovnice.entities.Peak;
import koplac.vyskovnice.tools.InternetCheck;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Gives functionality and defines the peak reached screen
 * @author Antonio Manuel Sanchez Amat
 * @author Juan Jose Espinosa Sanchez
 */
public class SiteReached extends AppCompatActivity {

    private Peak peak;
    private TextView tvPeakName;
    private ImageView ivPeakPicture;
    private InternetCheck ic;

    /**
     * Triggers at Activitys beginning, instances all Activities elements.
     * @param savedInstanceState
     */
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_reached);

        ic=new InternetCheck();

        peak=Peak.seekPeak(getIntent().getExtras().getInt("peakId"));

        tvPeakName = (TextView) findViewById(R.id.peakreached);
        ivPeakPicture =(ImageView)findViewById(R.id.ivReachedPeakPicture);

        tvPeakName.setText(peak.getName());
        ivPeakPicture.setImageResource(peak.getPicture());
        //Si no se dispone de conexion a internet
        if(ic.getConnectivityStatus(getBaseContext()) == 0){
            addUnreadBookPending();
        }
    }

    /**
     * Behavior of the device back button
     * Show a warning screen  reminding the user that the data about reaching the peak
     * will not be saved if leaves the screen
     */
    @Override public void onBackPressed() {
        //super.onBackPressed();
        boolean hasCommented;
        hasCommented=checkCommented(peak.getId());
        if(!hasCommented) {
            new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle(getResources().getString(R.string.goBackToMap))
                    .setMessage(getResources().getString(R.string.areYouSureYouWantToContinueWithoutLeftAComment))
                    .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).setNegativeButton(getResources().getString(R.string.no), null).show();
        }
        else{
            finish();
        }
    }

    /**
     * Sends the user to the write comment screen
     * @param v
     */
    public void writeComment(View v) {
        boolean hasCommented;
        hasCommented=checkCommented(peak.getId());
        //It´s only possible to write one comment
        if(hasCommented){
            //Log.v("pruebas","SiteReached-comentado?"+hasCommented);
            Toast.makeText(this, R.string.youHaveAlreadySentYoutComment, Toast.LENGTH_LONG).show();
        }
        //If no one comment it´s written yet
        else {
            //Log.v("pruebas","SiteReached-comentado?"+hasCommented);
            Bundle extras;
            Intent writeComment;

            extras=new Bundle();
            extras.putInt("peakId", peak.getId());

            writeComment=new Intent(SiteReached.this, WriteComment.class);
            writeComment.putExtras(extras);
            //Log.v("pruebas", "SiteReached-Vas a comentar");
            startActivity(writeComment);
        }
    }

    /**
     *Send the user to the read comments screen.
     * @param v
     */
    public void readComments(View v) {
        //If the user dont dispose of internet connection
        if (ic.getConnectivityStatus(getBaseContext()) == 0) {
            Toast.makeText(this,getResources().getString(R.string.noInternet), Toast.LENGTH_LONG).show();
        }
        //If dispose of a internet connection
        else {
            Bundle extras;
            Intent readComments;

            extras=new Bundle();
            extras.putInt("peakid", peak.getId());
            extras.putString("sqlSentence","");

            readComments=new Intent(SiteReached.this,ReadComments.class);
            readComments.putExtras(extras);

            startActivity(readComments);
        }
    }

    /**
     * Save in the Shared Preferences that the user had written a comment
     * @param peakId id of the reached peak
     * @return if the method had worked of not
     */
    public boolean checkCommented(int peakId) {
        return MainActivity.storeData.checkUsersReachedPeaksComments(MainActivity.user.getId(), peakId);
    }

    /**
     * Save in the Shared Preferences that there is a books to read pending
     */
    public void addUnreadBookPending(){
        SimpleDateFormat sdf;

        sdf=new SimpleDateFormat("yyyy-MM-dd");
        MainActivity.storeData.storeAddUnreadBook(MainActivity.user.getId(),peak.getId(),"SELECT nickname,comment,date FROM users,book_comments WHERE users.id=book_comments.user_id AND peak_id="+peak.getId()+" AND date<='"+sdf.format(new Date())+"' ORDER BY date ASC;");
    }

}
