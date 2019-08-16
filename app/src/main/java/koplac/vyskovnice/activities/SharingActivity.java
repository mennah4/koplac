package koplac.vyskovnice.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;

import java.util.List;

import koplac.vyskovnice.MainActivity;
import koplac.vyskovnice.R;
import koplac.vyskovnice.entities.Peak;

import static org.osmdroid.bonuspack.utils.BonusPackHelper.LOG_TAG;

public class SharingActivity extends AppCompatActivity {

    private Peak peakLast;
    private String lastSummit, lastSummitDate, lastSummitComment,userLastClimbedPeakHistory,appLink;
    private TextView txtLastSummit,txtLastSummitDate,txtLastSummitComment,ClimbedPeakHistory;
    private ImageView imgLastSummit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setSupportActionBar(toolbar);
        CallbackManager callbackManager = CallbackManager.Factory.create();
        final ShareDialog shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {

            @Override
            public void onSuccess(Sharer.Result result) {
                Log.d(LOG_TAG, "success");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(LOG_TAG, "error");
            }

            @Override
            public void onCancel() {
                Log.d(LOG_TAG, "cancel");
            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        txtLastSummitDate = (TextView) findViewById(R.id.last_summit_date);
        lastSummitDate = MainActivity.dbConnection.getUserLastClimbedPeakDate(MainActivity.user.getId());
        txtLastSummitDate.setText(lastSummitDate);

        txtLastSummitComment = (TextView) findViewById(R.id.last_summit_comment);
        lastSummitComment = MainActivity.dbConnection.getUserLastClimbedPeakComment(MainActivity.user.getId());
        txtLastSummitComment.setText(lastSummitComment);

        // there is an if condition because user may not have a last climbed peak
        if(MainActivity.dbConnection.getUserLastClimbedPeakid(MainActivity.user.getId()) != -1 ){
            peakLast = MainActivity.peaks.get(MainActivity.dbConnection.getUserLastClimbedPeakid(MainActivity.user.getId()));
            imgLastSummit = (ImageView) findViewById(R.id.last_summit_view);
            imgLastSummit.setImageResource(peakLast.getPicture());
            txtLastSummit = (TextView) findViewById(R.id.last_summit_text);
            lastSummit = MainActivity.dbConnection.getUserLastClimbedPeak(MainActivity.user.getId());
            txtLastSummit.setText(lastSummit);
            txtLastSummit.setTypeface(txtLastSummit.getTypeface(), Typeface.BOLD);
        }




//        userLastClimbedPeakHistory = MainActivity.dbConnection.getUserLastClimbedPeakHistory(MainActivity.user.getId());
//        ClimbedPeakHistory = (TextView) findViewById(R.id.climbed_peak_history);
//        ClimbedPeakHistory.setText(userLastClimbedPeakHistory);

        appLink = "https://play.google.com/store/apps/details?id=koplac.vyskovnice";
        ShareButton shareButton = (ShareButton)findViewById(R.id.fb_share_button);
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(appLink))
                .setContentTitle("Your Title")
                .setContentDescription("Your Description")
                .build();


        shareButton.setShareContent(content);



        // button to share lastSummit
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(

                        android.content.Intent.ACTION_SEND);

                i.setType("text/plain");
                i.setAction(Intent.ACTION_SEND);
                i.putExtra(android.content.Intent.EXTRA_SUBJECT, lastSummit);
                if(lastSummit == null){
                    i.putExtra(Intent.EXTRA_TITLE,"https://play.google.com/store/apps/details?id=koplac.vyskovnice");
                    i.putExtra(android.content.Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=koplac.vyskovnice");
                }else{

                    i.putExtra(Intent.EXTRA_TITLE,"I was in " +  lastSummit + " on " + lastSummitDate +",  "+lastSummitComment +"\n https://play.google.com/store/apps/details?id=koplac.vyskovnice");
                    i.putExtra(android.content.Intent.EXTRA_TEXT, "I was in " +  lastSummit + " on " + lastSummitDate +",  "+lastSummitComment);
                }
                startActivity(Intent.createChooser(i, "Share via"));
            }
        });



    }
}
