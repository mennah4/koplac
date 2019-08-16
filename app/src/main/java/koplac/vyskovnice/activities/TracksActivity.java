package koplac.vyskovnice.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import koplac.vyskovnice.MainActivity;
import koplac.vyskovnice.R;
import koplac.vyskovnice.entities.Track;

import java.util.LinkedList;
import java.util.List;

/**
 * Gives functionality and defines select path screen
 * @author Antonio Manuel Sanchez Amat
 * @author Juan Jose Espinosa Sanchez
 */
public class TracksActivity extends AppCompatActivity {

    private TextView noTracks;
    private List<String> trackNames;
    private ListView lvTracks;
    private ArrayAdapter aaTracks;
    private AdapterView.OnItemClickListener selectedItem;

    /**
     * Triggers at Activitys beginning, instances all Activities elements.
     * @param savedInstanceState
     */
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracks);

        trackNames=new LinkedList();
        for(Track track: MainActivity.tracks){
            trackNames.add(track.getName());
        }
        noTracks =(TextView)findViewById(R.id.noTracks);
        lvTracks=(ListView)findViewById(R.id.lvTracks);
        aaTracks=new ArrayAdapter(TracksActivity.this,android.R.layout.simple_list_item_1,trackNames);
        lvTracks.setAdapter(aaTracks);
        selectedItem=new AdapterView.OnItemClickListener() {
            //Al elegir un camino, lleva a usuario a la pantalla principal, y muestra el camino
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i;
                MainActivity.storeData.store("showTrack",position+1);
                i=new Intent(TracksActivity.this,MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        };
        lvTracks.setOnItemClickListener(selectedItem);

    }

    //@Override public boolean onCreateOptionsMenu(Menu menu){return true;}

    //@Override public boolean onOptionsItemSelected(MenuItem item){return super.onOptionsItemSelected(item);}


}
