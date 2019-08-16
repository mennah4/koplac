package koplac.vyskovnice.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import koplac.vyskovnice.MainActivity;
import koplac.vyskovnice.R;

/**
 * Gives functionality and defines the change password screen
 * Checks that the old passworld is correctly written
 * and ask for the new one two times
 * @author Antonio Manuel Sanchez Amat
 * @author Juan Jose Espinosa Sanchez
 */
public class ChangePassword extends AppCompatActivity {

    private EditText etOldPassword,etNewPassword,etNewPassword2;
    private Button btnChangeNewPassword;
    View.OnClickListener pressedButton;

    /**
     * Triggers at Activitys beginning, instances all Activities elements.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);

        etOldPassword=(EditText)findViewById(R.id.etOldPassword);
        etNewPassword=(EditText)findViewById(R.id.etNewPassword);
        etNewPassword2=(EditText)findViewById(R.id.etNewPassword2);
        btnChangeNewPassword=(Button)findViewById(R.id.btnChangeNewPassword);
        /*
        Button listener, checks all fields are correctly filled in and coincide
        with the DB stored ones
        Modifies the user password in the DB
         */
        pressedButton=new View.OnClickListener() {
            @Override public void onClick(View v) {
                String currentPass,newPass,newPass2;
                boolean result;
                BackgroundTasks bgt;

                currentPass=etOldPassword.getText().toString();
                newPass=etNewPassword.getText().toString();
                newPass2=etNewPassword2.getText().toString();

                if(newPass.equals("")){
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.fillNewPasswordField),Toast.LENGTH_SHORT).show();
                }
                else if(newPass2.equals("")){
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.fillNewPassword2Field),Toast.LENGTH_SHORT).show();
                }
                else if(!MainActivity.dbConnection.cypher.stringCypher(currentPass).equals(MainActivity.user.getPassword())){
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.currentPasswordWrong),Toast.LENGTH_SHORT).show();
                }
                else if(!newPass.equals(newPass2)){
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.newPasswordsNotMatch),Toast.LENGTH_SHORT).show();
                }
                else{
                    if(MainActivity.ic.getConnectivityStatus(getApplicationContext())!=0){
                        bgt=new BackgroundTasks(ChangePassword.this,MainActivity.user.getId(),newPass);
                        bgt.execute();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.noInternet),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
        btnChangeNewPassword.setOnClickListener(pressedButton);
    }

    /**
     * Makes the password changes in the DB while a progress animation appears
     */
    private class BackgroundTasks extends AsyncTask<Void,Integer,Boolean> {
        private final Context parentContext;
        private final int userId;
        private final String pass;
        private final ProgressDialog progressDialog;

        public BackgroundTasks(final Context parentContext,int userId,String pass){
            this.parentContext=parentContext;
            this.userId=userId;
            this.pass=pass;

            this.progressDialog=new ProgressDialog(parentContext);
            this.progressDialog.setTitle(parentContext.getResources().getString(R.string.loading));
            this.progressDialog.setCancelable(false);
            this.progressDialog.setIndeterminate(true);
        }
        private void hideKeyboard(){
            ((InputMethodManager)getSystemService(parentContext.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(btnChangeNewPassword.getWindowToken(), 0);
        }

        @Override protected void onPreExecute(){
            hideKeyboard();
            progressDialog.show();
        }
        @Override protected Boolean doInBackground(Void... params){
            Boolean result;

            publishProgress(R.string.updatingNewPassword);
            result=MainActivity.dbConnection.changePassword(userId,pass);

            return result;
        }
        @Override protected void onProgressUpdate(Integer... values) {
            progressDialog.setMessage(getResources().getString(values[0]));
        }
        @Override protected void onPostExecute(Boolean result) {
            progressDialog.dismiss();
            if(result){
                MainActivity.user.setPassword(MainActivity.dbConnection.cypher.stringCypher(pass));
                Toast.makeText(parentContext,getResources().getString(R.string.passwordChangeSucceed),Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(parentContext,getResources().getString(R.string.passwordChangeFailure),Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }
}
