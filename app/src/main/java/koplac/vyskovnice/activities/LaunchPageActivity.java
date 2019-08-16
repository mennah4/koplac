package koplac.vyskovnice.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import koplac.vyskovnice.MainActivity;
import koplac.vyskovnice.R;

;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LaunchPageActivity extends AppCompatActivity {

    private Button btnGoLogin;
    private Button btnGoSignin;
    private TextView txtStatus;
    private ImageView logo;
    CallbackManager callbackManager= CallbackManager.Factory.create();

    private View mContentView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_launch_page);




        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);


        mContentView = findViewById(R.id.fullscreen_content);
        btnGoLogin=(Button)findViewById(R.id.login_launch);
        btnGoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(LaunchPageActivity.this, Login.class);
                startActivity(myIntent);
            }
        });




        btnGoSignin=(Button)findViewById(R.id.signin_launch);
        btnGoSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(LaunchPageActivity.this, Signin.class);
                startActivity(myIntent);
            }
        });


        facebookSDKInitialize();
        txtStatus = (TextView) findViewById(R.id.txtView);
        LoginButton loginButton = (LoginButton)findViewById(R.id.fb_login_button);
        loginButton.setReadPermissions("email");
        getLoginDetails(loginButton);



        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.

    }
    /*
        Initialize the facebook sdk and then callback manager will handle the login responses.
     */
    protected void facebookSDKInitialize() {

        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();
    }

    protected void getLoginDetails(LoginButton login_button){
        // Callback registration
        login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                txtStatus.setText("Login success\n");
                getUserInfo(loginResult);

            }

            @Override
            public void onCancel() {
                txtStatus.setText("Login cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                txtStatus.setText("Login error: "+ error.getMessage());
            }
        });
    }

    /*
    To get the facebook user's own profile information via  creating a new request.
    When the request is completed, a callback is called to handle the success condition.
 */

    protected void getUserInfo(LoginResult login_result){

        GraphRequest data_request = GraphRequest.newMeRequest(
                login_result.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject json_object,
                            GraphResponse response) {

                        Intent intent = new Intent(LaunchPageActivity.this, Signin.class);
                        intent.putExtra("jsonFbData",json_object.toString());
                        startActivity(intent);
                    }
                });
        Bundle permission_param = new Bundle();
        permission_param.putString("fields", "id,name,email,picture.width(120).height(120)");
        data_request.setParameters(permission_param);
        data_request.executeAsync();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.e("data",data.toString());
    }





}
