package koplac.vyskovnice.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import koplac.vyskovnice.MainActivity;
import koplac.vyskovnice.PeaksAdapter;
import koplac.vyskovnice.R;
import koplac.vyskovnice.entities.Peak;

import java.util.LinkedList;
import java.util.List;

/**
 * Gives functionality and defines the pending comments books screen
 * @author Antonio Manuel Sanchez Amat
 * @author Juan Jose Espinosa Sanchez
 */
public class UnreadBooks extends AppCompatActivity {

    private TextView noBooks;
    private ListView lvUnreadBooks;
    private PeaksAdapter aaUnreadBooks;
    private List<Peak> peaks;

    private List<String> sqlSentences;
    private List<Integer> peakIds;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    /**
     * Triggers at Activitys beginning, instances all Activities elements.
     * @param savedInstanceState
     */
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unreaded_books);

        getUnreadBooks();
        //Log.v("pruebas", "peakIds: " + peakIds.toString());
        //Log.v("pruebas", "sqlSentences: " + sqlSentences.toString());

        noBooks=(TextView)findViewById(R.id.nobooks);
        lvUnreadBooks=(ListView) findViewById(R.id.unreadedlist);
        peaks=Peak.seekPeaks(peakIds);

        aaUnreadBooks = new PeaksAdapter(UnreadBooks.this,peaks);
        lvUnreadBooks.setAdapter(aaUnreadBooks);
        lvUnreadBooks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //LLeva al usuario a la pantalla de comentarios al pulsar en un libro determinado
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle extras;
                Intent i;

                extras=new Bundle();
                extras.putInt("peakId", peakIds.get(position));
                extras.putString("sqlSentence", sqlSentences.get(position));

                i=new Intent(UnreadBooks.this,ReadComments.class);
                i.putExtras(extras);
                startActivity(i);

                MainActivity.storeData.deleteUserUnreadBook(MainActivity.user.getId(), peakIds.get(position));
                peaks.remove(position);
                sqlSentences.remove(position);

                peakIds.remove(position);
                aaUnreadBooks.notifyDataSetChanged();
            }
        });
    }

    /**
     * Checks if there is more pending comments books
     * In case not, shows a message
     */
    @Override protected void onResume() {
        super.onResume();

        if(peakIds.size()==0) {
            noBooks.setVisibility(View.VISIBLE);
            lvUnreadBooks.setVisibility(View.INVISIBLE);
        }
        else{
            noBooks.setVisibility(View.INVISIBLE);
            lvUnreadBooks.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Retrieves the list of pending comments books
     */
    public void getUnreadBooks(){
        List<String[]> unreadBooks;

        sqlSentences=new LinkedList();
        peakIds=new LinkedList();
        unreadBooks=MainActivity.storeData.loadUserUnreadBooks(MainActivity.user.getId());
        //Log.v("pruebas","UnreadBooks-getUnreadBooks-unreadBooks size:" +unreadBooks.size());
        for(String[] s:unreadBooks){
            peakIds.add(Integer.parseInt(s[1]));
            sqlSentences.add(s[2]);
        }
    }
}
