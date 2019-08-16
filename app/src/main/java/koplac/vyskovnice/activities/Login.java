package koplac.vyskovnice.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import koplac.vyskovnice.MainActivity;
import koplac.vyskovnice.R;
import koplac.vyskovnice.entities.User;

/**
 * Gives functionality and defines the login screen
 * @author Antonio Manuel Sanchez Amat
 * @author Juan Jose Espinosa Sanchez
 */
public class Login extends AppCompatActivity {

    public static final int SIGN_IN=1;

    private EditText etEmail, etPassword;
    private Button btnLoginLogin;
    JSONObject response;

    /**
     * Triggers at Activitys beginning, instances all Activities elements.
     * @param savedInstanceState
     */
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = (EditText) findViewById(R.id.etLoginEmail);
        etPassword = (EditText) findViewById(R.id.etLoginPassword);
        btnLoginLogin=(Button)findViewById(R.id.btnLoginLogin);

        // for facebook login
        if(getIntent().getStringExtra("mail") != null){

            etEmail.setText(getIntent().getStringExtra("mail"));
            btnLoginLogin.callOnClick();
            Toast.makeText(getApplicationContext(),"jsongeldi",Toast.LENGTH_SHORT).show();

        }
    }




    /**
     Gives functionality to the login button
     */
    public void login(View v) {
        String email,password;
        BackgroundTasks bgt;

        email=etEmail.getText().toString();
        password=etPassword.getText().toString();

        //Check is the fields are filled
        if(email.equals("")){
            Toast.makeText(this,getResources().getString(R.string.fillEmailField),Toast.LENGTH_SHORT).show();
        }
        else{
            if(MainActivity.ic.getConnectivityStatus(this)!=0){
                bgt=new BackgroundTasks(Login.this,email,password);
                bgt.execute();

            }
            else{
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.noInternet),Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Gives functionality to the  Sign in button, opens the user registry Activity
     * @param v
     */
    public void signin(View v){
        startActivityForResult(new Intent(Login.this, Signin.class), SIGN_IN);
    }

    @Override protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case SIGN_IN:{
                if(resultCode==RESULT_OK){
                    finish();
                }
                break;
            }
        }
    }

    /**
     * Makes the changes in the DB while a progress animation appears
     */
    private class BackgroundTasks extends AsyncTask<Void,Integer,User> {
        private final Context parentContext;
        private final String email,pass;
        private final ProgressDialog progressDialog;

        public BackgroundTasks(final Context parentContext,String email,String pass){
            this.parentContext=parentContext;
            this.email=email;
            this.pass=pass;

            this.progressDialog=new ProgressDialog(parentContext);
            this.progressDialog.setTitle(parentContext.getResources().getString(R.string.loading));
            this.progressDialog.setCancelable(false);
            this.progressDialog.setIndeterminate(true);
        }
        private void hideKeyboard(){
            ((InputMethodManager)getSystemService(parentContext.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(btnLoginLogin.getWindowToken(), 0);
        }

        @Override protected void onPreExecute(){
            hideKeyboard();
            progressDialog.show();
        }
        @Override protected User doInBackground(Void... params){
            User user;

            publishProgress(R.string.loggingUser);
            user=MainActivity.dbConnection.login(email,pass);

            return user;
        }
        @Override protected void onProgressUpdate(Integer... values) {
            progressDialog.setMessage(getResources().getString(values[0]));
        }
        @Override protected void onPostExecute(User user) {
            progressDialog.dismiss();
            if(user==null){
                Toast.makeText(parentContext,getResources().getString(R.string.nicknameAndOrPasswordIncorrect),Toast.LENGTH_SHORT).show();
                etEmail.requestFocus();
            } else{
                MainActivity.user=user;
                MainActivity.storeData.saveUser(user);
                Toast.makeText(parentContext,getResources().getString(R.string.welcome)+" "+user.getNickname(),Toast.LENGTH_SHORT).show();
                Intent goToMain = new Intent(Login.this, MainActivity.class);
                startActivity(goToMain);
                finish();
            }
        }
    }
}
