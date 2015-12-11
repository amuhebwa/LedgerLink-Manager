package org.grameenfoundation.applabs.ledgerlinkmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.grameenfoundation.applabs.ledgerlinkmanager.adapters.ViewPagerAdapter;
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.Constants;
import org.grameenfoundation.applabs.ledgerlinkmanager.interfaces.IGroupInformation;
import org.grameenfoundation.applabs.ledgerlinkmanager.interfaces.ILocationInformation;
import org.grameenfoundation.applabs.ledgerlinkmanager.interfaces.IPhoneInformation;
import org.json.JSONException;
import org.json.JSONObject;


public class CreateGroup extends AppCompatActivity {
    private Constants constants = new Constants();

    public IGroupInformation iGroupInformation;
    public IPhoneInformation phoneInformationInterface;
    public ILocationInformation iLocationInformation;
    private String vslaName;
    private String representativeName;
    private String representativePost;
    private String repPhoneNumber;
    private String grpBankAccount;
    private String physAddress;
    private String regionName;
    private String grpPhoneNumber;
    private String locCoordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_group);


        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        final String serverUrl = sharedPreferences.getString("baseurl", "NA");

        Intent intent = getIntent();

        if (intent != null) {
            searchForVSLADetails(intent.getIntExtra("VslaId", 0), serverUrl);
        }
        InitializeUIComponents();
    }

    // intitialize the UI components
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

        if (iGroupInformation != null) {
            iGroupInformation.passGroupInformation(vslaName, grpPhoneNumber,
                    representativeName, representativePost, repPhoneNumber, grpBankAccount);
        }
        if (phoneInformationInterface != null) {

            phoneInformationInterface.passPhoneInformation(grpPhoneNumber);
        }
        if (iLocationInformation != null) {
            iLocationInformation.passLocationInformation(physAddress, regionName, locCoordinates);

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
    }
}
