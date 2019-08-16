package koplac.vyskovnice.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import koplac.vyskovnice.MainActivity;
import koplac.vyskovnice.R;
import koplac.vyskovnice.entities.Comment;
import koplac.vyskovnice.entities.Peak;

import java.util.List;


/**
 * Gives functionality and defines the peaks user comments screen
 * @author Antonio Manuel Sanchez Amat
 * @author Juan Jose Espinosa Sanchez
 */
public class ReadComments extends AppCompatActivity {

    private Peak peak;
    private List<Comment> comments;
    private ListView lvComments;
    private CommentAdapter aaComments;
    private BackgroundTasks bgt;

    private String sqlSentence;

    /**
     * Triggers at Activitys beginning, instances all Activities elements.
     * @param savedInstanceState
     */
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_comments);

        lvComments=(ListView)findViewById(R.id.lvComments);
        //Shows the comments list when a query is sended
        if(!getIntent().getExtras().getString("sqlSentence").equals("")){
            Log.v("pruebas","id del pico: "+getIntent().getExtras().getInt("peakId"));
            Log.v("pruebas","sql: "+getIntent().getExtras().getString("sqlSentence"));
            sqlSentence=getIntent().getExtras().getString("sqlSentence");
            peak=Peak.seekPeak(getIntent().getExtras().getInt("peakid"));
            bgt=new BackgroundTasks(ReadComments.this,getIntent().getExtras().getInt("peakId"),sqlSentence);
        }
        //Shows the list without query
        else{
            peak=Peak.seekPeak(getIntent().getExtras().getInt("peakid"));
            bgt=new BackgroundTasks(ReadComments.this,peak.getId(),"");
        }

        bgt.execute();
    }

    /**
     * Adapter for the comments list
     */
    class CommentAdapter extends BaseAdapter {

        private List<Comment> comments;
        private Context parentContext;
        private LayoutInflater inflater;

        public CommentAdapter(Context parentContext,List<Comment> comments){
            this.parentContext=parentContext;
            this.comments=comments;
            this.inflater=LayoutInflater.from(this.parentContext);
        }

        @Override public int getCount() {
            return comments.size();
        }
        @Override public Object getItem(int position) {
            return comments.get(position);
        }
        @Override public long getItemId(int position) {
            return 0;
        }
        @Override public View getView(int position, View convertView, ViewGroup parent) {
            convertView=inflater.inflate(R.layout.schema_comments_list,null);

            TextView tvAuthor,tvDate,tvContent;
            Comment comment;

            tvAuthor=(TextView)convertView.findViewById(R.id.tvCommentAuthor);
            tvDate=(TextView)convertView.findViewById(R.id.tvCommentDate);
            tvContent=(TextView)convertView.findViewById(R.id.tvCommentContent);

            comment=this.comments.get(position);

            tvAuthor.setText(comment.getAuthor());
            tvDate.setText(comment.getDate());
            tvContent.setText(comment.getComment());

            return convertView;
        }
    }

    /**
     * Retrieves the needed data from DB in a background task,
     * meanwhile a progress animation appears
     */
    private class BackgroundTasks extends AsyncTask<Void,Integer,List<Comment>> {
        private final Context parentContext;
        private final int peakId;
        private final String sql;
        private final ProgressDialog progressDialog;

        public BackgroundTasks(final Context parentContext,int peakId,String sql){
            this.parentContext=parentContext;
            this.peakId=peakId;
            this.sql=sql;

            this.progressDialog=new ProgressDialog(parentContext);
            this.progressDialog.setTitle(parentContext.getResources().getString(R.string.loading));
            this.progressDialog.setCancelable(false);
            this.progressDialog.setIndeterminate(true);
        }

        @Override protected void onPreExecute(){
            progressDialog.show();
        }
        @Override protected List<Comment> doInBackground(Void... params){
            List<Comment> result;

            result=null;
            publishProgress(R.string.retrievingComments);
            if(sql.equals("")){
                result=MainActivity.dbConnection.getBookComments(peakId);
            }
            else{
                result=MainActivity.dbConnection.getBookComments(sql);
            }

            return result;
        }
        @Override protected void onProgressUpdate(Integer... values) {
            progressDialog.setMessage(getResources().getString(values[0]));
        }
        @Override protected void onPostExecute(List<Comment> result) {
            progressDialog.dismiss();
            if(result!=null){
                comments=result;
                aaComments=new CommentAdapter(getApplicationContext(),comments);
                lvComments.setAdapter(aaComments);
            }
        }
    }
}
