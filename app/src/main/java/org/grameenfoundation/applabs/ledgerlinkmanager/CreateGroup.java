package org.grameenfoundation.applabs.ledgerlinkmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.grameenfoundation.applabs.ledgerlinkmanager.adapters.ViewPagerAdapter;
import org.grameenfoundation.applabs.ledgerlinkmanager.fragments.VslaFragment;
import org.grameenfoundation.applabs.ledgerlinkmanager.frags.LocationFrag;
import org.grameenfoundation.applabs.ledgerlinkmanager.frags.SubmitDataFrag;
import org.grameenfoundation.applabs.ledgerlinkmanager.frags.TrainingFrag;
import org.grameenfoundation.applabs.ledgerlinkmanager.frags.VslaFrag;
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.Constants;
import org.grameenfoundation.applabs.ledgerlinkmanager.interfaces.VslaInterface;
import org.grameenfoundation.applabs.ledgerlinkmanager.interfaces.LocationInterface;
import org.grameenfoundation.applabs.ledgerlinkmanager.interfaces.PhoneInterface;
import org.grameenfoundation.applabs.ledgerlinkmanager.models.VslaInfo;
import org.json.JSONException;
import org.json.JSONObject;


public class CreateGroup extends AppCompatActivity implements VslaFrag.VslaFragInterface,
        LocationFrag.LocationFragInterface, TrainingFrag.TrainingFragInterface {
    /*private Constants constants = new Constants();

    public VslaInterface vslaInterface;
    public PhoneInterface phoneInformationInterface;
    public LocationInterface locationInterface;
    private String vslaName;
    private String representativeName;
    private String representativePost;
    private String repPhoneNumber;
    private String grpBankAccount;
    private String physAddress;
    private String regionName;
    private String grpPhoneNumber;
    private String locCoordinates;
    private String numberOfCycles;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_group);
        loadDefaultFragment();

/*
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        final String serverUrl = sharedPreferences.getString("baseurl", "NA");

        Intent intent = getIntent();

        if (intent != null) {
            searchForVSLADetails(intent.getIntExtra("VslaId", 0), serverUrl);
        }
        InitializeUIComponents();*/
    }

    private void loadDefaultFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, new VslaFrag());
        fragmentTransaction.commit();
    }

    @Override
    public void passInfoToActivity(String command, int fragmentNumber) {

        if (command.equalsIgnoreCase("next")) { // NEXT FRAGMENT
            moveToNextFragment(fragmentNumber);
        } else { // PREVIOUS FRAGMENT
            moveToPreviousFragment(fragmentNumber);
        }
    }

    private void moveToNextFragment(int fragmentNumber) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (fragmentNumber == 1) {
            transaction.replace(R.id.frame_container, new LocationFrag());
            transaction.addToBackStack("location");
            transaction.commit();
        } else if (fragmentNumber == 2) {
            transaction.replace(R.id.frame_container, new TrainingFrag());
            transaction.addToBackStack("training");
            transaction.commit();
        } else if (fragmentNumber == 3) {
            transaction.replace(R.id.frame_container, new SubmitDataFrag());
            transaction.addToBackStack("submit");
            transaction.commit();
        }
    }

    private void moveToPreviousFragment(int fragmentNumber) {

    }
/* // intitialize the UI components
    private void InitializeUIComponents() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id._tabLayout);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), CreateGroup.this));
        tabLayout.setupWithViewPager(viewPager);
    }

    // if in Editing mode, change the title of the actionbar
    public void changeActionBarTitle(String title) {
        if (title != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    // Get all group details from the server based on tthe supplied Unique Id
    public void searchForVSLADetails(int VslaId, String url) {
        String request_url = url + constants.vslaInformation + "/" + String.valueOf(VslaId);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                request_url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    if (response != JSONObject.NULL) {
                        JSONObject vslaDetails = response.getJSONObject("vslaInformationResult");
                        vslaName = vslaDetails.getString("VslaName");
                        representativeName = vslaDetails.getString("representativeName");
                        representativePost = vslaDetails.getString("representativePosition");
                        repPhoneNumber = vslaDetails.getString("repPhoneNumber");
                        grpBankAccount = vslaDetails.getString("GroupAccountNumber");
                        physAddress = vslaDetails.getString("PhysicalAddress");
                        regionName = vslaDetails.getString("RegionName");
                        grpPhoneNumber = vslaDetails.getString("grpPhoneNumber");
                        locCoordinates = vslaDetails.getString("GpsLocation");
                        numberOfCycles = vslaDetails.getString("numberOfCycles");
                        setGroupDataToInterfaces();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
        VolleySingleton.getIntance().addToRequestQueue(jsonObjectRequest);

    }

    // Add data to interfaces
    private void setGroupDataToInterfaces() {

        if (vslaInterface != null) {
            vslaInterface.passGroupInformation(vslaName, grpPhoneNumber,
                    representativeName, representativePost, repPhoneNumber, grpBankAccount, numberOfCycles);
        }
        if (phoneInformationInterface != null) {

            phoneInformationInterface.passPhoneInformation(grpPhoneNumber);
        }
        if (locationInterface != null) {
            locationInterface.passLocationInformation(physAddress, regionName, locCoordinates);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }*/
}
