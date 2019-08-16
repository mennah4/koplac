package koplac.vyskovnice.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import org.apache.commons.lang3.ObjectUtils;

import koplac.vyskovnice.MainActivity;
import koplac.vyskovnice.PeaksAdapter;
import koplac.vyskovnice.R;
import koplac.vyskovnice.entities.Peak;
import koplac.vyskovnice.tools.InternetCheck;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static koplac.vyskovnice.MainActivity.peaks;

/**
 * List of peak with the possibility of choose them and go
 * to the peak information screen
 *
 * @author Antonio Manuel Sanchez Amat
 * @author Juan Jose Espinosa Sanchez
 */
public class PeaksActivity extends AppCompatActivity {

    public static final int ALL = 1;
    public static final int USER = 2;

    private List<Integer> userClimbedPeaks;
    public ListView lvPeaks;
    public PeaksAdapter aaPeaks, aaPeakstemp;
    public AdapterView.OnItemClickListener selectedItem;
    private InternetCheck ic;
    BackgroundTasks bgt;
    private EditText etSearch;
    public  ArrayList<Peak> peaksTemp;

    /**
     * Triggers at Activitys beginning, instances all Activities elements.
     * Shows the list of all the peak, or the history of climbed peak by the user
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peaks);

        ic = new InternetCheck();

        lvPeaks = (ListView) findViewById(R.id.lvPeaks);
        etSearch = (EditText) findViewById(R.id.etSearch);



        //Shows all the peaks
        if (getIntent().getExtras().getInt("type") == ALL) {
            aaPeaks = new PeaksAdapter(getApplicationContext(), peaks);

            lvPeaks.setAdapter(aaPeaks);
            lvPeaks.setTextFilterEnabled(true);
        }
        //Shows the historic of user climbed peaks
        else {
            userClimbedPeaks = new LinkedList();
            bgt = new BackgroundTasks(PeaksActivity.this, MainActivity.user.getId());
            bgt.execute();
        }
        //Behavior when one of the peaks is selected
        selectedItem = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle extras;
                Intent i;

                extras = new Bundle();
                i = null;
                /*
                    Shows the information screen  of the chossed peak if user was in the
                    peaks list
                */
                if (getIntent().getExtras().getInt("type") == ALL && aaPeakstemp ==null) {
                    extras.putInt("peakId", peaks.get(position).getId());
                    i = new Intent(PeaksActivity.this, PeakActivity.class);
                    i.putExtras(extras);
                    startActivity(i);
                }
                 /*
                    if something has been searching, that is neccessery
                */
                else if (aaPeakstemp != null) {
                    extras.putInt("peakId", aaPeakstemp.getPeaks().get(position).getId());
                    i = new Intent(PeaksActivity.this, PeakActivity.class);
                    i.putExtras(extras);
                    startActivity(i);
                }
                /*
                    Shows the number of times the user reached a peak if the user was
                    historic of climbed peaks
                 */
                else {
                    extras.putInt("peakId", aaPeaks.getPeaks().get(position).getId());
                    i = new Intent(PeaksActivity.this, UserPeakHistoryActivity.class);
                    i.putExtras(extras);
                    startActivity(i);
                }


            }
        };
        lvPeaks.setOnItemClickListener(selectedItem);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // for peaks and books activity
                if(getIntent().getExtras().getInt("type") == ALL){
                    peaksTemp = new ArrayList<>();
                    for(int i=0; i < peaks.size(); i++){
                        if (peaks.get(i).getName().toUpperCase().contains(s.toString().toUpperCase())){
                            peaksTemp.add(peaks.get(i));
                        }
                    }

                    if("BOOK".equals(s.toString().toUpperCase())){
                        for (int i=0; i < peaks.size(); i++){
                            if(aaPeaks.getPeaks().get(i).getCategory() == "Book"){
                                peaksTemp.add(peaks.get(i));
                            }
                        }
                    }
                    aaPeakstemp = new PeaksAdapter(getApplicationContext(), peaksTemp);
                    lvPeaks = (ListView) findViewById(R.id.lvPeaks);
                    lvPeaks.setAdapter(aaPeakstemp);
                }
                //for the climbed peaks
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * Retrieves the climbed peaks historic meanwhile a progress animation appears
     */
    private class BackgroundTasks extends AsyncTask<Void, Integer, List<Integer>> {
        private final Context parentContext;
        private final int userId;
        private final ProgressDialog progressDialog;

        public BackgroundTasks(final Context parentContext, int userId) {
            this.parentContext = parentContext;
            this.userId = userId;

            this.progressDialog = new ProgressDialog(parentContext);
            this.progressDialog.setTitle(parentContext.getResources().getString(R.string.loading));
            this.progressDialog.setCancelable(false);
            this.progressDialog.setIndeterminate(true);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected List<Integer> doInBackground(Void... params) {
            List<Integer> result;

            result = new LinkedList();

            publishProgress(R.string.retrievingUserClimbedPeaks);
            result = MainActivity.dbConnection.getUserClimbedPeaks(userId);

            return result;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setMessage(getResources().getString(values[0]));
        }

        @Override
        protected void onPostExecute(List<Integer> result) {
            progressDialog.dismiss();
            userClimbedPeaks = result;
            aaPeaks = new PeaksAdapter(parentContext, userClimbedPeaks, "");
            lvPeaks.setAdapter(aaPeaks);
            aaPeaks.notifyDataSetChanged();
            lvPeaks.invalidate();

        }
    }
}
