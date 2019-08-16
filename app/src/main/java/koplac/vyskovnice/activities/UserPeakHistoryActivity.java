package koplac.vyskovnice.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import koplac.vyskovnice.MainActivity;
import koplac.vyskovnice.R;
import koplac.vyskovnice.entities.Peak;

import java.util.List;

/**
 * Gives functionality and defines the historic of user reached peaks screen
 * @author Antonio Manuel Sanchez Amat
 * @author Juan Jose Espinosa Sanchez
 */
public class UserPeakHistoryActivity extends AppCompatActivity {

    private Peak peak;
    private TextView tvPeakName,tvTimesReached;
    private ImageView ivPeakPicture;
    private List<String> peakReachedDates;
    private ListView lvUserPeakHistory;
    private ArrayAdapter<String> aaUserPeakHistory;

    /**
     * Triggers at Activitys beginning, instances all Activities elements.
     * @param savedInstanceState
     */
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userpeakhistory);

        tvPeakName=(TextView)findViewById(R.id.tvUserPeakName);
        tvTimesReached=(TextView)findViewById(R.id.tvTimesReached);
        ivPeakPicture=(ImageView)findViewById(R.id.ivUserPeakPicture);
        lvUserPeakHistory=(ListView)findViewById(R.id.lvUserPeakHistory);

        peak= Peak.seekPeak(getIntent().getExtras().getInt("peakId"));

        peakReachedDates= MainActivity.dbConnection.getReachedPeakUserHistory(MainActivity.user.getId(),peak.getId());
        tvPeakName.setText(peak.getName());
        tvTimesReached.setText(peakReachedDates.size()+"");
        ivPeakPicture.setImageResource(peak.getPicture());

        aaUserPeakHistory=new ArrayAdapter(this,android.R.layout.simple_list_item_1,peakReachedDates);
        lvUserPeakHistory.setAdapter(aaUserPeakHistory);
    }

    /**
     * Menu creation
     * @param menu
     * @return
     */
    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_peak, menu);
        return true;
    }

    /**
     * Menu element thats centers the map in the choosed peak
     * @param item
     * @return
     */
    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.maPointOnMap:{
                MainActivity.storeData.store("pointPeakOnMap",peak.getId());
                startActivity(new Intent(UserPeakHistoryActivity.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
