package koplac.vyskovnice.activities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import koplac.vyskovnice.MainActivity;
import koplac.vyskovnice.R;
import koplac.vyskovnice.entities.Peak;
import koplac.vyskovnice.tools.InternetCheck;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Gives functionality and defines the write comments screen
 * @author Antonio Manuel Sanchez Amat
 * @author Juan Jose Espinosa Sanchez
 */
public class WriteComment extends AppCompatActivity {

    private InternetCheck ic;
    private Peak peak;
    private RatingBar ratingBar;
    private Button btnSend;
    private EditText etComment;
    private TextView tvPeakName;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    int i=1;

    /**
     * Triggers at Activitys beginning, instances all Activities elements.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_comment);

        ic=new InternetCheck();
        peak=Peak.seekPeak(getIntent().getExtras().getInt("peakId"));

        ratingBar=(RatingBar)findViewById(R.id.ratingBar);
        etComment =(EditText)findViewById(R.id.comment);
        btnSend =(Button)findViewById(R.id.sendbutton);
        tvPeakName=(TextView)findViewById(R.id.tvWriteCommentPeakName);

        preferences = PreferenceManager.getDefaultSharedPreferences(WriteComment.this);
        editor = preferences.edit();

        tvPeakName.setText(peak.getName());
    }

    /**
     * Behavior of the send comment button
     * @param v
     */
    public void send(View v) {
        boolean result;
        String comment;
        int bookStatus;
        SimpleDateFormat sdf;

        sdf=new SimpleDateFormat("yyyy-MM-dd");

        comment=etComment.getText().toString();
        comment=comment.trim();
        //Checks the comment field is not empty
        if(comment.equals("")){
            //Log.v("pruebas","WriteComment-Intentas enviar un comentario vacio");
            Toast.makeText(this,R.string.youCantSendAnEmptyComment,Toast.LENGTH_LONG).show();
            etComment.setText("");
        }
        //Store the comment in the DB
        else{
            bookStatus=(int)ratingBar.getRating();
            if(ic.getConnectivityStatus(getBaseContext())!=0) {
                //Log.v("pruebas","WriteComment-Internet OK Enviando comentario");
                result= MainActivity.dbConnection.sendComment(peak.getId(), MainActivity.user.getId(),bookStatus,sdf.format(new Date()), comment);
                //Log.v("pruebas","WriteComment-Internet OK Enviando comentario: "+result);
            }
            //Store the comment in the Shared Preferences to send it to the DB
            //when a internet connection is available
            else{
                //Log.e("pruebas", "WriteComment-Internet NO Guardando comentario");
                MainActivity.storeData.storeAddUnsentComment("INSERT INTO book_comments VALUES("+peak.getId()+","+MainActivity.user.getId()+","+bookStatus+",'"+sdf.format(new Date())+"','"+MainActivity.dbConnection.normalizeString(comment)+"');");
            }
            setCommented(peak.getId());

            Toast.makeText(this,getResources().getString(R.string.thanksForYourComment), Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    /**
     * Store in the Shared Prefernces that the user had commented the peak today
     * @param peakId
     * @return
     */
    public boolean setCommented(int peakId) {
        return MainActivity.storeData.storeUsersReachedPeaksComments(MainActivity.user.getId(),peakId);
    }


}
