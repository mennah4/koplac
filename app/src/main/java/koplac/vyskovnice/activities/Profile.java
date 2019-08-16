package koplac.vyskovnice.activities;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;

import koplac.vyskovnice.MainActivity;
import koplac.vyskovnice.R;
import koplac.vyskovnice.tools.FTPConnection;
import koplac.vyskovnice.tools.ImageManipulation;
import koplac.vyskovnice.tools.InternetCheck;

import java.io.InputStream;

/**
 * Gives functionality and defines the profile screen
 * @author Antonio Manuel Sanchez Amat
 * @author Juan Jose Espinosa Sanchez
 */
public class Profile extends AppCompatActivity {

    private static final int SELECT_PHOTO = 100;

    private TextView tvNickname,tvEmail;
    private ImageView ivAvatar;
    private EditText etNewName;
    private Button btnChangePassword,btnChangeAvatar,btnClimbedPeaks,btnLogOut, btnChangeName;
    private InternetCheck ic;


    ImageButton closePopup;
    public PopupWindow popupWindow;
    View.OnClickListener pressedButton;
    /**
     * Triggers at Activitys beginning, instances all Activities elements.
     * @param savedInstanceState
     */
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ic=new InternetCheck();


        tvNickname = (TextView) findViewById(R.id.textname);
        tvEmail = (TextView) findViewById(R.id.textemail);
        ivAvatar = (ImageView) findViewById(R.id.photo);
        btnChangeAvatar=(Button)findViewById(R.id.btnChangeProfilePicture);
        btnChangePassword=(Button)findViewById(R.id.btnChangePassword);
        btnClimbedPeaks=(Button)findViewById(R.id.btnClimbedPeaks);
        btnLogOut=(Button)findViewById(R.id.btnProfileLogOut);

        tvNickname.setText(MainActivity.user.getNickname());
        tvEmail.setText(MainActivity.user.getEmail());
        if(MainActivity.user.getPicture()!=null){
            ivAvatar.setImageBitmap(BitmapFactory.decodeByteArray(MainActivity.user.getPicture(), 0, MainActivity.user.getPicture().length));
        }




    }

    /**
     * Definition of menu layout
     * @param menu
     * @return
     */
    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile,menu);
        return true;
    }

    /**
     * Behavior of the menu elements
     * @param item
     * @return
     */
    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            //Change password
            case R.id.maChangePassword:{
                this.changePassword(this.btnChangePassword);
                break;
            }
            //Change image
            case R.id.maChangeAvatar:{
                this.changeAvatar(this.btnChangeAvatar);
                break;
            }
            //Users climbed peaks history
            case R.id.maClimbedPeaks:{
                this.climbedPeaks(this.btnClimbedPeaks);
                break;
            }
            //close session
            case R.id.maLogOut:{
                this.logout(this.btnLogOut);
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Button that sends the user to the change password screen
     * @param v  listener object that uses the element
     */
    public void changePassword(View v){
        startActivity(new Intent(Profile.this, ChangePassword.class));
    }
    public void changeUserName(View v){
        startActivity(new Intent(Profile.this, ChangeUserName.class));
    }

    /**
     * Button that sends the user to the image gallery to choose one
     * @param v  listener object that uses the element
     */
    public void changeAvatar(View v){
        startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"), SELECT_PHOTO);
    }

    /**
     * Button that sends the user to the historic of climbed peaks screen
     * @param v  listener object that uses the element
     */
    public void climbedPeaks(View v){
        Bundle extras;
        Intent i;

        if(ic.getConnectivityStatus(Profile.this)!=0){
            extras=new Bundle();
            extras.putInt("Type", PeaksActivity.USER);
            i=new Intent(Profile.this,PeaksActivity.class);
            i.putExtras(extras);
            startActivity(i);
        }
        else{
            Toast.makeText(this,getResources().getString(R.string.noInternet),Toast.LENGTH_SHORT).show();
        }

    }





    /**
     *Button that close the user session, and shows a warning screen asking if the user is
     * sure or not.
     * @param v listener object that uses the element.
     */
    public void logout(View v) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(R.string.logOut);
        adb.setMessage(R.string.areYouSureYouWantToLogOut);
        adb.setCancelable(false);
        adb.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                MainActivity.user = null;
                MainActivity.storeData.deleteUser();
                LoginManager.getInstance().logOut();
                finish();
            }
        });
        adb.setNegativeButton(R.string.no,new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = adb.create();
        alert.show();
    }

    /**
     * Shows the image gallery for the user to choose one, to put it in the profile
     * @param requestCode variable name
     * @param resultCode variable value
     * @param data the returned image
     */
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        byte[] picture;
        String pictureName;
        Bitmap bitmap,yourSelectedImage;
        InputStream imageStream;
        Uri selectedImage;
        BackgroundTasks bgt;

        selectedImage=null;
        yourSelectedImage=null;
        switch (requestCode) {
            case SELECT_PHOTO:{
                if (resultCode == RESULT_OK) {
                    selectedImage = data.getData();
                    try{
                        imageStream = getContentResolver().openInputStream(selectedImage);
                        yourSelectedImage = BitmapFactory.decodeStream(imageStream);
                    }
                    catch(Exception e){e.printStackTrace();}

                }
                if (yourSelectedImage != null) {
                    ivAvatar.setImageBitmap(ImageManipulation.getOrientation(getPath(selectedImage), yourSelectedImage));

                    if(MainActivity.ic.getConnectivityStatus(this)!=0){
                        bitmap = ((BitmapDrawable) ivAvatar.getDrawable()).getBitmap();
                        pictureName="img-"+MainActivity.user.getNickname()+".png";
                        picture=ImageManipulation.bitmapToArray(bitmap);

                        bgt=new BackgroundTasks(Profile.this,MainActivity.user.getId(),pictureName,picture);
                        bgt.execute();
                    }
                    else{
                        Toast.makeText(this,getResources().getString(R.string.noInternet),Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
        }
    }

    /**
     * Transform the internal device path of the image, with a path to work with
     * @param uri device internal path
     * @return new path
     */
    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }

    /**
     * Store the image in the FTP server and its name in the DB,
     * meanwhile a progress animation appears
     */
    private class BackgroundTasks extends AsyncTask<Void,Integer,Boolean> {
        private final Context parentContext;
        private final int userId;
        private final String picName;
        private final byte[] image;
        private final ProgressDialog progressDialog;

        public BackgroundTasks(final Context parentContext,int userId,String picName,byte[] image){
            this.parentContext=parentContext;
            this.userId=userId;
            this.picName=picName;
            this.image=image;

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
            boolean resultFTPUpload,resultDBUpdate;

            result=false;

            publishProgress(R.string.updatingAvatar);
            MainActivity.ftpConection.createConnection();
            resultFTPUpload=MainActivity.ftpConection.uploadAvatar(image, FTPConnection.AVATAR_IMAGES_DIRECTORY, picName);
            resultDBUpdate=MainActivity.dbConnection.changeImage(userId,picName);
            MainActivity.ftpConection.closeConnection();
            if(resultFTPUpload && resultDBUpdate){
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
                MainActivity.user.setPicture(image);
                MainActivity.storeData.saveUser(MainActivity.user);
                Toast.makeText(parentContext,getResources().getString(R.string.avatarChangedSuccessfully),Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(parentContext,getResources().getString(R.string.avatarChangeFailure),Toast.LENGTH_SHORT).show();
            }
        }
    }
}
