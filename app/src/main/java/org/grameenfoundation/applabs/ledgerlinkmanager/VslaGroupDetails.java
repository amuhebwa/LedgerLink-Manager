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


public class VslaGroupDetails extends AppCompatActivity {
    private Constants constants = new Constants();

    public IGroupInformation IGroupInformation;
    public IPhoneInformation phoneInformationInterface;
    public ILocationInformation ILocationInformation;

    private String vslaName, groupRepresentativeName, groupRepresentativePost, groupRepresentativePhoneNumber, groupBankAccount,
            physicalAddress, regionName, groupPhoneNumber, locationCoordinates;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_group);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final String serverUrl = sharedPreferences.getString("LedgerLinkBaseUrl", "NA");
        Intent intent = getIntent();
        if (intent != null) {
            int VslaId = intent.getIntExtra("VslaId", 0);

                searchForVSLADetails(VslaId, serverUrl);


        }
        InitializeUIComponents();
    }

    /**
     * Intitialize the UI components
     */
    private void InitializeUIComponents() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id._tabLayout);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), VslaGroupDetails.this));
        tabLayout.setupWithViewPager(viewPager);
    }

    /**
     * If in Editing mode, change the title of the actionbar
     */
    public void changeActionBarTitle(String title) {
        if (title != null) {
            getSupportActionBar().setTitle(title);
        }
    }
    /**
     * Get all group details from the server based on tthe supplied Unique Id
     */
    public void searchForVSLADetails(int VslaId, String url) {
        String request_url = url + constants.SINGLEVSLADETAILS + "/" + String.valueOf(VslaId);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, request_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject vslaDetails = response.getJSONObject("getSingleVslaDetailsResult");
                            vslaName = vslaDetails.getString("VslaName");
                            groupRepresentativeName = vslaDetails.getString("GroupRepresentativeName");
                            groupRepresentativePost = vslaDetails.getString("GroupRepresentativePosition");
                            groupRepresentativePhoneNumber = vslaDetails.getString("GroupRepresentativePhonenumber");
                            groupBankAccount = vslaDetails.getString("GroupAccountNumber");
                            physicalAddress = vslaDetails.getString("PhysicalAddress");
                            regionName = vslaDetails.getString("RegionName");
                            groupPhoneNumber = vslaDetails.getString("VslaPhoneMsisdn");
                            locationCoordinates = vslaDetails.getString("GpsLocation");

                            setGroupDataToInterfaces();

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

    /**
     * Add data to interfaces
     */
    private void setGroupDataToInterfaces() {
        if (IGroupInformation != null) {
            IGroupInformation.passGroupInformation(vslaName, groupPhoneNumber, groupRepresentativeName, groupRepresentativePost,
                    groupRepresentativePhoneNumber, groupBankAccount);
        }
        if (phoneInformationInterface != null) {

            phoneInformationInterface.passPhoneInformation(groupPhoneNumber);
        }
        if (ILocationInformation != null) {
            ILocationInformation.passLocationInformation(physicalAddress, regionName, locationCoordinates);

        }
    }
}
