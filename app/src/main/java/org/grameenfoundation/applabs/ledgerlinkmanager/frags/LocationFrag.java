package org.grameenfoundation.applabs.ledgerlinkmanager.frags;


import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.grameenfoundation.applabs.ledgerlinkmanager.JsonData;
import org.grameenfoundation.applabs.ledgerlinkmanager.R;
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.DataHolder;
import org.grameenfoundation.applabs.ledgerlinkmanager.models.VslaInfo;
import org.json.JSONException;
import org.json.JSONObject;


public class LocationFrag extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private LocationFragInterface locationFragInterface;
    private EditText inputPhysicalAdress;
    private Spinner selectRegion;
    private MapView mapView;
    private GoogleMap googleMap;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private GoogleApiClient mGoogleApiClient;
    private String coodinates;

    public LocationFrag() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.location_frag, container, false);
        // set up google maps
        mapView = (MapView) view.findViewById(R.id._map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume(); // Display the map immediately
        setupGoogleMaps(); // set up google maps
        if (checkPlayServices()) {
            buildGoogleApiClient();
        }

        inputPhysicalAdress = (EditText) view.findViewById(R.id.PhysicalAddress);
        selectRegion = (Spinner) view.findViewById(R.id.RegionName);
        addRegionNames(selectRegion);

        Boolean isEditing = JsonData.getInstance().isEditing();
        String jsonData = JsonData.getInstance().getVslaJsonStringData();
        if (isEditing) {
            processVslaInformation(jsonData);
        }
        return view;
    }
    /**
     * Add information attached to one vsla group into a singleton class. This is when a group is being edited
     */
    private void processVslaInformation(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            String physicalAddress = jsonObject.getString("PhysicalAddress");
            String RegionName = jsonObject.getString("RegionName");
            inputPhysicalAdress.setText(physicalAddress);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Validate input fields
    private boolean validateInputFields() {
        String physicalAddress = inputPhysicalAdress.getText().toString();
        DataHolder.getInstance().setPhysicalAddress(physicalAddress);
        if (physicalAddress.isEmpty()) {
            return false;
        }
        return true;
    }

    private void updateInfoToActivity() {
        String command = "next";
        int fragmentNumber = 2;
        if (validateInputFields()) {
            locationFragInterface.passInfoToActivity(command, fragmentNumber);
        } else {
            Toast.makeText(getActivity(), "Enter Valid Physical Address", Toast.LENGTH_LONG).show();
        }
    }

    private void setupGoogleMaps() {
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        googleMap = mapView.getMap();
    }

    // check if google play services exist on the phone
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getActivity(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                getActivity().finish();
            }
            return false;
        }
        return true;
    }

    // build the google api client
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
        checkPlayServices();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    // Add region names to the drop down spinner
    private void addRegionNames(final Spinner spinner) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.districts, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinner.setSelection(position);
                switch (position) {
                    case 0:
                        DataHolder.getInstance().setRegionName("9");
                        break;
                    case 1:
                        DataHolder.getInstance().setRegionName("10");
                        break;
                    case 2:
                        DataHolder.getInstance().setRegionName("11");
                        break;
                    case 3:
                        DataHolder.getInstance().setRegionName("12");
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onConnected(Bundle bundle) {

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        // if (mLastLocation != null) {
            /*double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();*/
        double latitude = 0.23333;
        double longitude = 32.67845;

        // save coordinates
        coodinates = String.valueOf(latitude) + " , " + String.valueOf(longitude);
        DataHolder.getInstance().setLocationCoordinates(coodinates);

        // plot the location on the map
        LatLng loc = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions().position(loc));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
        // }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LocationFragInterface) {
            locationFragInterface = (LocationFragInterface) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        locationFragInterface = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_location_frag, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem actionViewItem = menu.findItem(R.id.action_save);
        View v = MenuItemCompat.getActionView(actionViewItem);
        Button b = (Button) v.findViewById(R.id.btnSave);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateInfoToActivity();
            }
        });

        super.onPrepareOptionsMenu(menu);
    }


    public interface LocationFragInterface {
        void passInfoToActivity(String command, int fragmentNumber);
    }
}
