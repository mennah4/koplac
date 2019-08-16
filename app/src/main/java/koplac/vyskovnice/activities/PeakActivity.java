package koplac.vyskovnice.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import koplac.vyskovnice.MainActivity;
import koplac.vyskovnice.R;
import koplac.vyskovnice.entities.Peak;

/**
 * Gives functionality and defines the peak information screen
 * @author Antonio Manuel Sanchez Amat
 * @author Juan Jose Espinosa Sanchez
 */
public class PeakActivity extends AppCompatActivity {
    ImageView image;
    TextView name,height,longitude,latitude,distance,mountains,region,plants_animals,reservation;
    TextView geology,springs,hydrology,buildings,history,acces,attractions,view,links,notes;
    LinearLayout lyPlantsAnimals,lyReservation,lyGeology,lySprings,lyHydrology,lyBuildings;
    LinearLayout lyHistory,lyAccess,lyAttractions,lyViews,lyLinks,lyNotes;
    public Peak peak;

    /**
     * Instanciation and definition of its elements
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peak);

        image = (ImageView) findViewById(R.id.peakimage);
        name = (TextView) findViewById(R.id.peakname);
        longitude = (TextView) findViewById(R.id.longitude);
        latitude = (TextView) findViewById(R.id.latitude);
        height = (TextView) findViewById(R.id.height);
        distance = (TextView) findViewById(R.id.distance_from_start);
        mountains = (TextView) findViewById(R.id.mountains);
        region = (TextView) findViewById(R.id.region);
        plants_animals = (TextView) findViewById(R.id.plants_animals);
        reservation = (TextView) findViewById(R.id.reservation);
        geology = (TextView) findViewById(R.id.geology);
        springs = (TextView) findViewById(R.id.springs);
        hydrology = (TextView) findViewById(R.id.hydrology);
        buildings = (TextView) findViewById(R.id.buildings);
        history = (TextView) findViewById(R.id.history);
        acces = (TextView) findViewById(R.id.acces);
        attractions = (TextView) findViewById(R.id.atractions);
        view = (TextView) findViewById(R.id.view);
        links = (TextView) findViewById(R.id.link);
        notes = (TextView) findViewById(R.id.notes);

        lyPlantsAnimals=(LinearLayout)findViewById(R.id.lyPlantsAnimals);
        lyReservation=(LinearLayout)findViewById(R.id.lyReservation);
        lyGeology=(LinearLayout)findViewById(R.id.lyGeology);
        lySprings=(LinearLayout)findViewById(R.id.lySprings);
        lyHydrology=(LinearLayout)findViewById(R.id.lyHydrology);
        lyBuildings=(LinearLayout)findViewById(R.id.lyBuildings);
        lyHistory=(LinearLayout)findViewById(R.id.lyHistory);
        lyAccess=(LinearLayout)findViewById(R.id.lyAccess);
        lyAttractions=(LinearLayout)findViewById(R.id.lyAttractions);
        lyViews=(LinearLayout)findViewById(R.id.lyViews);
        lyLinks=(LinearLayout)findViewById(R.id.lyViews);
        lyNotes=(LinearLayout)findViewById(R.id.lyNotes);

        peak=Peak.seekPeak(getIntent().getExtras().getInt("peakId"));

        image.setImageResource(peak.getPicture());
        name.setText(peak.getName());
        longitude.setText((peak.getLongitude()));
        latitude.setText((peak.getLatitude()));
        height.setText(peak.getHeight()+" m");
        distance.setText(peak.getDistance_from_start()+" km");
        mountains.setText(peak.getMountains());
        region.setText(peak.getRegion());
        // If there is not information about a element, the field will not appear
        if(peak.getPlants_animals().equals("?")){lyPlantsAnimals.setVisibility(View.INVISIBLE);}
        else{plants_animals.setText(peak.getPlants_animals());}

        if(peak.getReservation().equals("?")){lyReservation.setVisibility(View.INVISIBLE);}
        else{reservation.setText(peak.getReservation());}

        if(peak.getGeology().equals("?")){lyGeology.setVisibility(View.INVISIBLE);}
        else{geology.setText(peak.getGeology());}

        if(peak.getSprings().equals("?")){lySprings.setVisibility(View.INVISIBLE);}
        else{springs.setText(peak.getGeology());}

        if(peak.getHydrology().equals("?")){lyHydrology.setVisibility(View.INVISIBLE);}
        else{hydrology.setText(peak.getGeology());}

        if(peak.getBuildings().equals("?")){lyBuildings.setVisibility(View.INVISIBLE);}
        else{buildings.setText(peak.getBuildings());}

        if(peak.getHistory().equals("?")){lyHistory.setVisibility(View.INVISIBLE);}
        else{history.setText(peak.getHistory());}

        if(peak.getAcces().equals("?")){lyAccess.setVisibility(View.INVISIBLE);}
        else{acces.setText(peak.getAcces());}

        if(peak.getAttractions().equals("?")){lyAttractions.setVisibility(View.INVISIBLE);}
        else{attractions.setText(peak.getAttractions());}

        if(peak.getView().equals("?")){lyViews.setVisibility(View.INVISIBLE);}
        else{view.setText(peak.getView());}

        if(peak.getLink().equals("?")){lyLinks.setVisibility(View.INVISIBLE);}
        else{links.setText(peak.getLink());}

        if(peak.getNotes().equals("?")){lyNotes.setVisibility(View.INVISIBLE);}
        else{notes.setText(peak.getNotes());}
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
     * Menu elements
     * @param item
     * @return
     */
    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            //Center the map in a peak
            case R.id.maPointOnMap:{
                MainActivity.storeData.store("pointPeakOnMap",peak.getId());
                startActivity(new Intent(PeakActivity.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
