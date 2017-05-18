package ksmori.hu.ait.spades;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.example.games.basegameutils.BaseGameUtils;

import ksmori.hu.ait.spades.view.FragmentTagged;
import ksmori.hu.ait.spades.view.PlayerInitFragment;
import ksmori.hu.ait.spades.view.SignInFragment;

public class StartActivity extends AppCompatActivity implements
        View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private static final String TAG = "StartActivity";

    private static int RC_SIGN_IN = 9001;

    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInFlow = true;
    private boolean mSignInClicked = false;

    boolean mExplicitSignOut = false;
    boolean mInSignInFlow = false; // set to true when you're in the middle of the
    // sign in flow, to know you should not attempt
    // to connect in onStart()

    private GoogleApiClient mGoogleApiClient;

    private Message mActiveMessage; // the latest message to finish or cancel publishing
    private MessageListener mMessageListener; // receiving messages: don't forget to subscribe!
    private FragmentTagged mActiveFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        displayFragment(new SignInFragment());

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .addApi(Nearby.MESSAGES_API)
                .enableAutoManage(this, this) // shows opt-in dialog before connecting to Nearby
                .build();


        mMessageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {
                final String messageAsString = new String(message.getContent());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(StartActivity.this, "found message: "+messageAsString,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onLost(Message message) {
                final String messageAsString = new String(message.getContent());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(StartActivity.this, "lost sight of message: "+messageAsString,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

    }

    private void displayFragment(FragmentTagged fragment) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment foundFragment = fm.findFragmentByTag(fragment.getTAG());
        fragment = (foundFragment!=null ? (FragmentTagged) foundFragment : fragment);
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fl_start_fragment_container, fragment, fragment.getTAG());
        ft.commit();
        mActiveFragment = fragment;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mInSignInFlow && !mExplicitSignOut) {
            // auto sign in
            mGoogleApiClient.connect();
        }
        //TODO IS THIS WHAT WE WANT?
        //TODO NO; IT WAS A TEMPORARY THING TO STOP CRASHES
        //mGoogleApiClient.connect(GoogleApiClient.SIGN_IN_MODE_OPTIONAL);
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient.isConnected()) {
            unpublish();
            Nearby.Messages.unsubscribe(mGoogleApiClient,mMessageListener);
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // show sign-out button, hide the sign-in button

        Nearby.Messages.subscribe(mGoogleApiClient,mMessageListener);
        displayFragment(new PlayerInitFragment());
        Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();

        // (your code here: update UI, enable functionality that depends on sign in, etc)
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {


        Toast.makeText(this, makeErrorMessage(connectionResult), Toast.LENGTH_SHORT).show();

        if (mResolvingConnectionFailure) {
            // Already resolving
            return;
        }

        // If the sign in button was clicked or if auto sign-in is enabled,
        // launch the sign-in flow
        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;

            // Attempt to resolve the connection failure using BaseGameUtils.
            // The R.string.signin_other_error value should reference a generic
            // error string in your strings.xml file, such as "There was
            // an issue with sign in, please try again later."
            if (!BaseGameUtils.resolveConnectionFailure(this,
                    mGoogleApiClient, connectionResult,
                    RC_SIGN_IN, R.string.signin_other_error)) {
                mResolvingConnectionFailure = false;
            }
        }

    }

    // A trivial method that provides the name corresponding to an ConnectionResult error code
    @NonNull
    private String makeErrorMessage(ConnectionResult connectionResult) {
        String errorName;
        switch (connectionResult.getErrorCode()) {
            case ConnectionResult.API_UNAVAILABLE :
                errorName = "ConnectionResult.API_UNAVAILABLE";
                break;
            case ConnectionResult.CANCELED :
                errorName = "ConnectionResult.CANCELED";
                break;
            case ConnectionResult.DEVELOPER_ERROR :
                errorName = "ConnectionResult.DEVELOPER_ERROR";
                break;
            case ConnectionResult.INTERNAL_ERROR :
                errorName = "ConnectionResult.INTERNAL_ERROR";
                break;
            case ConnectionResult.INTERRUPTED :
                errorName = "ConnectionResult.INTERRUPTED";
                break;
            case ConnectionResult.INVALID_ACCOUNT :
                errorName = "ConnectionResult.INVALID_ACCOUNT";
                break;
            case ConnectionResult.LICENSE_CHECK_FAILED :
                errorName = "ConnectionResult.LICENSE_CHECK_FAILED";
                break;
            case ConnectionResult.NETWORK_ERROR :
                errorName = "ConnectionResult.NETWORK_ERROR";
                break;
            case ConnectionResult.RESOLUTION_REQUIRED :
                errorName = "ConnectionResult.RESOLUTION_REQUIRED";
                break;
            case ConnectionResult.RESTRICTED_PROFILE :
                errorName = "ConnectionResult.RESTRICTED_PROFILE";
                break;
            case ConnectionResult.SERVICE_DISABLED :
                errorName = "ConnectionResult.SERVICE_DISABLED";
                break;
            case ConnectionResult.SERVICE_INVALID :
                errorName = "ConnectionResult.SERVICE_INVALID";
                break;
            case ConnectionResult.SERVICE_MISSING :
                errorName = "ConnectionResult.SERVICE_MISSING";
                break;
            case ConnectionResult.SERVICE_MISSING_PERMISSION :
                errorName = "ConnectionResult.SERVICE_MISSING_PERMISSION";
                break;
            case ConnectionResult.SERVICE_UPDATING :
                errorName = "ConnectionResult.SERVICE_UPDATING";
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED :
                errorName = "ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED";
                break;
            case ConnectionResult.SIGN_IN_FAILED :
                errorName = "ConnectionResult.SIGN_IN_FAILED";
                break;
            case ConnectionResult.SIGN_IN_REQUIRED :
                errorName = "ConnectionResult.SERVICE_INVALID";
                break;
            case ConnectionResult.SUCCESS :
                errorName = "ConnectionResult.SUCCESS";
                break;
            case ConnectionResult.TIMEOUT :
                errorName = "ConnectionResult.SERVICE_TIMEOUT";
                break;

            default:
                errorName = "code "+connectionResult.getErrorCode();
        }
        return "Error: "+errorName;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_in_button) {
            // start the asynchronous sign in flow
            mSignInClicked = true;
            mGoogleApiClient.connect();
        }
        else if (view.getId() == R.id.sign_out_button) {
            // sign out.
            mExplicitSignOut = true;
            mSignInClicked = false;
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                Games.signOut(mGoogleApiClient);
                unpublish();
                Nearby.Messages.unsubscribe(mGoogleApiClient,mMessageListener);
                mGoogleApiClient.disconnect();
            }

            // show sign-in button, hide the sign-out button
            displayFragment(new SignInFragment());
        }
        else if (view.getId() == R.id.btn_temporary){
            publish("Hostname = " + ((PlayerInitFragment) mActiveFragment).getTmpText());
        }
    }

    private void unpublish() {
        Log.v(TAG, "Unpublishing.");
        if (mActiveMessage != null) {
            Nearby.Messages.unpublish(mGoogleApiClient, mActiveMessage);
            mActiveMessage = null;
        }
    }
    private void publish(String message) {
        Toast.makeText(this,"Publishing message: " + message,Toast.LENGTH_SHORT).show();
        mActiveMessage = new Message(message.getBytes());
        Nearby.Messages.publish(mGoogleApiClient, mActiveMessage);
    }
}
