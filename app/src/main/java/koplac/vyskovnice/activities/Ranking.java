package koplac.vyskovnice.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.ViewFlipper;

import koplac.vyskovnice.MainActivity;
import koplac.vyskovnice.R;

import java.util.List;

/**
 * Gives functionality and defines the ranking screen
 * @author Antonio Manuel Sanchez Amat
 * @author Juan Jose Espinosa Sanchez
 */
public class Ranking extends AppCompatActivity {

    public List<String[]> topUsers,topPeaks;
    public ListView lvTopUsers,lvTopPeaks;
    private RankingAdapter raTopUsers,raTopPeaks;
    private BackgroundTasks bgt;

    private ViewFlipper vf;
    private View.OnTouchListener slideFinger;

    /**
     * Triggers at Activitys beginning, instances all Activities elements.
     * @param savedInstanceState
     */
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        lvTopUsers=(ListView)findViewById(R.id.lvTopUsers);
        lvTopPeaks=(ListView)findViewById(R.id.lvTopPeaks);

        bgt=new BackgroundTasks(Ranking.this);
        bgt.execute();

        vf=(ViewFlipper)findViewById(R.id.vfRankings);
        slideFinger=new View.OnTouchListener() {
            float lastX,currentX;

            /**
             * Sets the behavior of the lateral animation for navigate between the ranking of the user with
             * most number of climbed peaks and the ranking of peaks most times climbed
             * @param v View of the element
             * @param event place of displacement
             * @return
             */
            @Override public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN: //When the user touches the screen the first time
                        lastX=event.getX();
                        return true;
                    case MotionEvent.ACTION_UP: //When the user stop pressing
                        currentX=event.getX();
                        //left to right movement
                        if (lastX < currentX){
                            //if (vf.getDisplayedChild()==0){break;}
                            vf.setInAnimation(getApplicationContext(),R.anim.in_from_left);
                            vf.setOutAnimation(getApplicationContext(),R.anim.out_to_right);
                            vf.showNext();
                        }
                        //right to left movement
                        if (lastX > currentX){
                            //if (vf.getDisplayedChild()==0){break;}
                            vf.setInAnimation(getApplicationContext(),R.anim.in_from_right);
                            vf.setOutAnimation(getApplicationContext(),R.anim.out_to_left);
                            vf.showPrevious();
                        }
                    default: {
                        break;
                    }
                }
                return false;
            }
        };
        vf.setOnTouchListener(slideFinger);
    }

    /**
     * Adapter of the most climbed peaks ranking screen
     */
    class RankingAdapter extends BaseAdapter{
        Context parentContext;
        LayoutInflater inflater;
        List<String[]> items;

        /**
         * Class contructor
         * @param parentContext father context
         * @param items     list of user objects
         */
        public RankingAdapter(Context parentContext,List<String[]> items){
            this.parentContext=parentContext;
            this.items=items;
            this.inflater=LayoutInflater.from(parentContext);
        }

        /**
         * Returns the number of elements
         */
        @Override public int getCount(){return items.size();}

        /**
         *Returns an element given its position
         */
        @Override public Object getItem(int position){return items.get(position);}

        /**
         * Returns an element id given its position
         */
        @Override public long getItemId(int position){return 0;}

        /**
         * Fill the list with all the elements
         */
        @Override public View getView(int position, View convertView, ViewGroup parent){
            String[] item;
            TextView tvNumber,tvLeft,tvRight;

            convertView=inflater.inflate(R.layout.schema_ranking_list,null);

            tvNumber=(TextView)convertView.findViewById(R.id.tvRankingNumber);
            tvLeft=(TextView)convertView.findViewById(R.id.tvRankingLeft);
            tvRight=(TextView)convertView.findViewById(R.id.tvRankingRight);

            item=items.get(position);
            tvNumber.setText(item[0]);
            tvLeft.setText(item[1]);
            tvRight.setText(item[2]);

            return convertView;
        }
    }

    /**
     * Adapter of the users with most climbed peaks ranking screen
     */
    private class RunnersAdapter extends BaseAdapter{
        Context parentContext;
        LayoutInflater inflater;
        List<String[]> items;

        /**
         * Class contructor
         * @param parentContext Contexto del padre
         * @param items     Lista de objetos usuarios
         */
        public RunnersAdapter(Context parentContext,List<String[]> items){
            this.parentContext=parentContext;
            this.items=items;
            this.inflater=LayoutInflater.from(parentContext);
        }

        /**
         * Returns the number of elements
         */
        @Override public int getCount(){return items.size();}

        /**
         *Returns an element given its position
         */
        @Override public Object getItem(int position){return items.get(position);}

        /**
         * Devuelve la id de un elemento dada su possicion
         */
        @Override public long getItemId(int position){return 0;}

        /**
         * Fill the list with all the elements
         */
        @Override public View getView(int position, View convertView, ViewGroup parent){
            TextView tvNumber,tvNickname,tvCheckpoints,tvDateTime;
            String[] aux;

            convertView=inflater.inflate(R.layout.schema_runners_list,null);
            tvNumber=(TextView)convertView.findViewById(R.id.tvRunnersNumber);
            tvNickname=(TextView)convertView.findViewById(R.id.tvRunnersNickname);
            tvCheckpoints=(TextView)convertView.findViewById(R.id.tvRunnersCheckpoints);
            tvDateTime=(TextView)convertView.findViewById(R.id.tvRunnersDateTime);

            aux=items.get(position);
            tvNumber.setText(String.valueOf(position+1));
            tvNickname.setText(aux[0]);
            tvCheckpoints.setText(aux[1]);
            tvDateTime.setText(aux[2] + " " + aux[3]);

            return convertView;
        }
    }

    /**
     * Retrieves the needed data from DB in a background task,
     * meanwhile a progress animation appears
     */
    private class BackgroundTasks extends AsyncTask<Void,Integer,Boolean> {
        private final Context parentContext;
        private final ProgressDialog progressDialog;

        public BackgroundTasks(final Context parentContext){
            this.parentContext=parentContext;

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

            result=false;

            publishProgress(R.string.retrievingTopClimbers);
            topUsers= MainActivity.dbConnection.getBestClimbersRanking();

            publishProgress(R.string.retrievingTopReachedPeaks);
            topPeaks=MainActivity.dbConnection.getMostClimbedPeaksRanking();

            if(topUsers!=null && topPeaks!=null){
                result=true;
            }

            return result;
        }
        @Override protected void onProgressUpdate(Integer... values) {
            progressDialog.setMessage(getResources().getString(values[0]));
        }
        @Override protected void onPostExecute(Boolean result) {
            progressDialog.dismiss();
            if(result){
                raTopUsers=new RankingAdapter(parentContext,topUsers);
                raTopPeaks=new RankingAdapter(parentContext,topPeaks);

                lvTopUsers.setAdapter(raTopUsers);
                lvTopPeaks.setAdapter(raTopPeaks);
            }
        }
    }


}
