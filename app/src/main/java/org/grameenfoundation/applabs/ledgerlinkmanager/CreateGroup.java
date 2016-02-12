package org.grameenfoundation.applabs.ledgerlinkmanager;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.grameenfoundation.applabs.ledgerlinkmanager.frags.LocationFrag;
import org.grameenfoundation.applabs.ledgerlinkmanager.frags.SubmitDataFrag;
import org.grameenfoundation.applabs.ledgerlinkmanager.frags.TrainingFrag;
import org.grameenfoundation.applabs.ledgerlinkmanager.frags.VslaFrag;

public class CreateGroup extends AppCompatActivity implements VslaFrag.VslaFragInterface, LocationFrag.LocationFragInterface, TrainingFrag.TrainingFragInterface,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_group);
        if (checkPlayServices()) {
            buildGoogleApiClient();
        }
        String starredFrag = TrainingOptionsData.getInstance().getStarredFragment();
        if (starredFrag == null) {
            loadDefaultFragment();
        } else {
            reloadTrainingFragment();
        }
    }

    private void loadDefaultFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        VslaFrag vslaFrag = new VslaFrag();
        fragmentTransaction.replace(R.id.frame_container, vslaFrag);
        fragmentTransaction.commit();
    }

    /**
     * Re-load the training fragment after selecting the
     * training options delivered.
     */
    private void reloadTrainingFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        TrainingFrag trainingFrag = new TrainingFrag();
        transaction.replace(R.id.frame_container, trainingFrag);
        transaction.commit();
    }

    @Override
    public void passInfoToActivity(String command, int fragmentNumber) {
        if (command.equalsIgnoreCase("next")) {
            moveToNextFragment(fragmentNumber);
        }
    }

    private void moveToNextFragment(int fragmentNumber) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (fragmentNumber == 1) {
            LocationFrag locationFrag = new LocationFrag();
            transaction.replace(R.id.frame_container, locationFrag);
            transaction.addToBackStack("locationInformation");
            transaction.commit();
        } else if (fragmentNumber == 2) {
            TrainingFrag trainingFrag = new TrainingFrag();
            transaction.replace(R.id.frame_container, trainingFrag);
            transaction.addToBackStack("trainingInformation");
            transaction.commit();
        } else if (fragmentNumber == 3) {
            SubmitDataFrag submitDataFrag = new SubmitDataFrag();
            transaction.replace(R.id.frame_container, submitDataFrag);
            transaction.addToBackStack("submitInformation");
            transaction.commit();
        }
    }

    private boolean checkPlayServices() {
        final int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(this, "Please Install Google Play Services.", Toast.LENGTH_LONG).show();
            }
            return false;
        }
        return true;
    }

    protected synchronized void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    protected void onStart() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        super.onStart();
    }

    protected void onStop() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkPlayServices();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                double _latutude = mLastLocation.getLatitude();
                double _longitude = mLastLocation.getLongitude();
                JsonData.getInstance().setLatitude(_latutude);
                JsonData.getInstance().setLongitude(_longitude);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "Failed to get Location", Toast.LENGTH_SHORT).show();
    }
}
