package koplac.vyskovnice.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import koplac.vyskovnice.MainActivity;
import koplac.vyskovnice.R;
import koplac.vyskovnice.entities.User;
import koplac.vyskovnice.tools.FTPConnection;
import koplac.vyskovnice.tools.ImageManipulation;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Gives functionality and defines the signin screen
 * @author Antonio Manuel Sanchez Amat
 * @author Juan Jose Espinosa Sanchez
 */
public class Signin extends AppCompatActivity {

    private final int SELECT_PHOTO = 100;

    Bitmap yourSelectedImage;
    String jsondata;
    InputStream imageStream;
    Uri selectedImage;
    private ImageView ivSigninAvatar;
    private EditText etSigninPassword,etSigninPassword2,etSigninEmail,etSigninNickname;
    private Button btnSigninSignin;
    JSONObject response, profile_pic_data, profile_pic_url;

    /**
     * Triggers at Activitys beginning, instances all Activities elements.
     * @param savedInstanceState
     */
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        ivSigninAvatar = (CircleImageView) findViewById(R.id.photo);
        etSigninPassword = (EditText) findViewById(R.id.editpassword);
        etSigninPassword2 = (EditText) findViewById(R.id.editpassword2);
        etSigninEmail = (EditText) findViewById(R.id.editemail);
        etSigninNickname = (EditText) findViewById(R.id.edit_user);
        btnSigninSignin=(Button)findViewById(R.id.btnSigninSignin);

        selectedImage=null;

        // facebook signin
        Intent intent = getIntent();
        String jsondata = intent.getStringExtra("jsonFbData");
        if(jsondata != null){
            setUserProfile(jsondata);
        }

    }

    public  void  setUserProfile(String jsondata){

        try {
            response = new JSONObject(jsondata);
            etSigninEmail.setText(response.get("email").toString());
            etSigninNickname.setText(response.get("name").toString());
            profile_pic_data = new JSONObject(response.get("picture").toString());
            profile_pic_url = new JSONObject(profile_pic_data.getString("data"));
            Picasso.with(this).load(profile_pic_url.getString("url"))
                    .into(ivSigninAvatar);
            btnSigninSignin.callOnClick();

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void alreadySigninViaFacebook(Intent intent){

        intent = new Intent(Signin.this, Login.class);
        startActivity(intent);
    }

    /**
     * Behavior of the accept button
     * @param v
     */
    public void accept(View v) {
        String userNick,userPass,userPass2,userEmail,pictureName;
        boolean validEmail,validNickname;
        Bitmap bitmap;
        BackgroundTasks bgt;
        Intent intent = getIntent();
        String jsondata = intent.getStringExtra("jsonFbData");

        userNick= etSigninNickname.getText().toString();
        userPass= etSigninPassword.getText().toString();
        userPass2= etSigninPassword2.getText().toString();
        userEmail= etSigninEmail.getText().toString();
        //Checks the nick field is not empty
        if(userNick.equals("")){
            Toast.makeText(this,getResources().getString(R.string.fillNicknameField), Toast.LENGTH_SHORT).show();
            etSigninNickname.requestFocus();
        }
        //Checks the password field is not empty
        else if(userPass.equals("") && jsondata == null  ){
            Toast.makeText(this,getResources().getString(R.string.fillPasswordField), Toast.LENGTH_SHORT).show();
            etSigninPassword.requestFocus();
        }
        //Checks the repeat password field is not empty
        else if(userPass2.equals("") && jsondata == null){
            Toast.makeText(this,getResources().getString(R.string.fillPassword2Field), Toast.LENGTH_SHORT).show();
            etSigninPassword2.requestFocus();
        }
        //Checks the email field is not empty
        else if(userEmail.equals("")){
            Toast.makeText(this,getResources().getString(R.string.fillEmailField), Toast.LENGTH_SHORT).show();
            etSigninEmail.requestFocus();
        }
        //Checks the contet of the two password fields is the same
        else if (!userPass.equals(userPass2)) {
            Toast.makeText(this,getResources().getString(R.string.passwordsNotMatch), Toast.LENGTH_SHORT).show();
        }
        //Checks if its a valid email
        else if (!isEmailValid(etSigninEmail.getText().toString())) {
            Toast.makeText(this,getResources().getString(R.string.emailNotValid), Toast.LENGTH_SHORT).show();
            etSigninEmail.requestFocus();
        }
        //if all the conditions fulfill
        else{
            //if the user dispose of a internet connection
            if(MainActivity.ic.getConnectivityStatus(this)!=0){
                pictureName="";
                bitmap=null;
                if(yourSelectedImage!=null){
                    pictureName="img-"+userNick+".png";
                    bitmap = ((BitmapDrawable) ivSigninAvatar.getDrawable()).getBitmap();
                }
                bgt=new BackgroundTasks(Signin.this,userNick,userPass,userEmail,pictureName,bitmap);
                bgt.execute();
            }
            //if the user don´t dispose of a internet connection
            else{
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.noInternet),Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Check if the email has a correct format
     * @param email inserted email
     * @return if has a valid format or not
     */
    boolean isEmailValid(CharSequence email){return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();}

    /**
     * Sends the user to the image gallery for the user to choose one to put it as profile image
     * @param v
     */
    public void pickphoto(View v){startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"), SELECT_PHOTO);}
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case SELECT_PHOTO: {
                if (resultCode == RESULT_OK) {
                    selectedImage = imageReturnedIntent.getData();
                    try {
                        imageStream = getContentResolver().openInputStream(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    yourSelectedImage = BitmapFactory.decodeStream(imageStream);
                }
                if (yourSelectedImage != null) {
                    ivSigninAvatar.setImageBitmap(ImageManipulation.getOrientation(getPath(selectedImage), yourSelectedImage));
                }
            }
        }
    }

    /**
     * Transform the internal device path of the image, with a path to work with
     * @param uri device internal path
     * @return new path
     */
    public String getPath(Uri uri){
        if(uri == null){return null;}

        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
    }

    /**
     * Store the image in the FTP server and its name in the DB,
     * meanwhile a progress animation appears
     */
    private class BackgroundTasks extends AsyncTask<Void,Integer,Integer> {
        private Context parentContext;
        private String nick,pass,email,picName;
        private Bitmap bitmap;
        private ProgressDialog progressDialog;
        private User user;

        public BackgroundTasks(Context parentContext,String nick,String pass,String email,String picName,Bitmap bitmap){
            this.parentContext=parentContext;
            this.nick=nick;
            this.pass=pass;
            this.email=email;
            this.picName=picName;
            this.bitmap=bitmap;

            user=null;

            progressDialog=new ProgressDialog(parentContext);
            progressDialog.setTitle(parentContext.getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
        }
        private void hideKeyboard(){
            ((InputMethodManager)getSystemService(parentContext.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(btnSigninSignin.getWindowToken(), 0);
        }

        @Override protected void onPreExecute() {
            hideKeyboard();
            progressDialog.show();
        }
        @Override protected Integer doInBackground(Void... params) {
            Integer result;
            boolean results;

            publishProgress(R.string.validatingEmail);
            results=MainActivity.dbConnection.isValidEmail(email);
            if(results){
                publishProgress(R.string.validatingNickname);
                results=MainActivity.dbConnection.isValidNickname(nick);
                if(results){
                    publishProgress(R.string.signingUser);
                    MainActivity.dbConnection.signinUser(nick, pass, email, picName);

                    if(!picName.equals("")){
                        publishProgress(R.string.uploadingAvatar);
                        MainActivity.ftpConection.createConnection();
                        MainActivity.ftpConection.sendStream(ImageManipulation.bitmapToArray(bitmap), FTPConnection.AVATAR_IMAGES_DIRECTORY, picName);
                        MainActivity.ftpConection.closeConnection();
                    }

                    publishProgress(R.string.loggingUser);
                    user=MainActivity.dbConnection.login(email,pass);
                    return R.string.welcome;
                }
                else{
                    return R.string.nicknameInUse;
                }
            }
            else{
                return R.string.emailInUse;
            }
        }
        @Override protected void onProgressUpdate(Integer... values) {
            progressDialog.setMessage(getResources().getString(values[0]));
        }
        @Override protected void onPostExecute(Integer result) {
            progressDialog.dismiss();
            switch(result){
                case R.string.nicknameInUse:{
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.nicknameInUse),Toast.LENGTH_SHORT).show();
                    etSigninNickname.requestFocus();
                    break;
                }
                case R.string.emailInUse:{
                    etSigninEmail.requestFocus();
                    //for facebook login
                    Intent intent = getIntent();
                    if(intent != null){
                        String jsondata = intent.getStringExtra("jsonFbData");
                        try {
                            response = new JSONObject(jsondata);
                            intent = new Intent(Signin.this, Login.class);
                            intent.putExtra("mail",  response.get("email").toString());
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }else
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.emailInUse),Toast.LENGTH_SHORT).show();
                    break;
                }
                case R.string.welcome:{
                    MainActivity.user=user;
                    MainActivity.storeData.saveUser(user);

                    setResult(RESULT_OK, new Intent());
                    Intent goToMain = getIntent();
                    goToMain = new Intent(Signin.this, MainActivity.class);
                    goToMain.putExtras(getIntent());
                    startActivity(goToMain);
                    Toast.makeText(parentContext,getResources().getString(R.string.welcome)+" "+user.getNickname(),Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                }
            }

        }
    }
}

