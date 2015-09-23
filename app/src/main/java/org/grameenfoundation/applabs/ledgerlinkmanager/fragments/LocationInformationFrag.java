package org.grameenfoundation.applabs.ledgerlinkmanager.fragments;


import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.grameenfoundation.applabs.ledgerlinkmanager.VslaGroupDetails;
import org.grameenfoundation.applabs.ledgerlinkmanager.R;
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.DataHolder;
import org.grameenfoundation.applabs.ledgerlinkmanager.interfaces.LocationInformationInterface;

public class LocationInformationFrag extends Fragment implements LocationInformationInterface, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, AdapterView.OnItemSelectedListener {
    private GoogleApiClient mGoogleApiClient;
    private MapView mapView;
    private GoogleMap _googleMap;
    private EditText PhysicalAddress;
    private Spinner RegionName;
    private MenuItem cancelMenu, editMenu, saveMenu;
    private String locationCoodinates;
    private String[] disticts = {"Busia", "Bugiri", "Iganga", "Namayingo"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (!checkGooglePlayServices()) {
            getActivity().finish();
        }
        View view = inflater.inflate(R.layout.fragment_location_information, container, false);
        setHasOptionsMenu(true);
        mapView = (MapView) view.findViewById(R.id.googleMap);
        mapView.onCreate(savedInstanceState);

        setUpGoogleMaps();

        createGoogleApiClient();

        intializeUIComponents(view);

        disableEditing();
        return view;
    }
    /**  Set up Google maps */
    private void setUpGoogleMaps() {
        _googleMap = mapView.getMap();
        _googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        _googleMap.setMyLocationEnabled(true);
    }

    /**   Intialize the User interface components */
    private void intializeUIComponents(View view) {
        PhysicalAddress = (EditText) view.findViewById(R.id.PhysicalAddress);
        RegionName = (Spinner) view.findViewById(R.id.RegionName);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, disticts);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        RegionName.setAdapter(spinnerAdapter);
        RegionName.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        RegionName.setSelection(position);
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

    private void setDataToDataHolderClass() {
        if (PhysicalAddress.getText().toString().isEmpty() || PhysicalAddress.getText().toString().length() < 4) {
            PhysicalAddress.setError("Physical Address is Empty");
        } else {
            DataHolder.getInstance().setPhysicalAddress(PhysicalAddress.getText().toString());
        }
        DataHolder.getInstance().setLocationCoordinates(locationCoodinates);
    }

    /**
     * Enable Edit Boxes
     */
    private void enableEditing() {
        PhysicalAddress.setEnabled(true);
        RegionName.setEnabled(true);
    }

    /**
     * Disable Edit Boxes
     */
    private void disableEditing() {
        PhysicalAddress.setEnabled(false);
        RegionName.setEnabled(false);
    }

    /** Clear Error messages  */
    private void clearErrorMessages() {
        PhysicalAddress.setError(null);
    }

    protected void createGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    /** Check if google play services are available */
    private boolean checkGooglePlayServices() {
        int REQUEST_CODE_RECOVER_PLAY_SERVICES = 200;
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (status != ConnectionResult.SUCCESS) {
            GooglePlayServicesUtil.getErrorDialog(status, getActivity(), REQUEST_CODE_RECOVER_PLAY_SERVICES);
            return false;
        }
        return true;
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
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mCurrentLocation != null) {
            float latitude = (float) mCurrentLocation.getLatitude();
            float longitude = (float) mCurrentLocation.getLongitude();
            locationCoodinates = String.valueOf(latitude) + " , " + String.valueOf(longitude);
            LatLng _currentLocation = new LatLng(latitude, longitude);
            _googleMap.addMarker(new MarkerOptions().position(_currentLocation));
            _googleMap.moveCamera(CameraUpdateFactory.newLatLng(_currentLocation));
            _googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(_currentLocation, 15));

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Context context = getActivity();
        ((VslaGroupDetails) context).locationInformationInterface = this;

    }

    @Override
    public void passLocationInformation(String physicalAddress, String regionName, String locationCordinates) {
        PhysicalAddress.setText(physicalAddress);

        /**  Then save information to the data holder **/
        setDataToDataHolderClass();
        DataHolder.getInstance().setLocationCoordinates(locationCoodinates);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_location_information, menu);
        if (menu != null) {
            cancelMenu = menu.findItem(R.id.action_cancel);
            editMenu = menu.findItem(R.id.action_edit);
            saveMenu = menu.findItem(R.id.action_save);
        }
        cancelMenu.setVisible(false);
        saveMenu.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit) {
            cancelMenu.setVisible(true);
            saveMenu.setVisible(true);
            editMenu.setVisible(false);
            clearErrorMessages();
            enableEditing();
        }
        if (id == R.id.action_save) {
            cancelMenu.setVisible(false);
            saveMenu.setVisible(false);
            editMenu.setVisible(true);
            setDataToDataHolderClass();
            disableEditing();
        }
        return super.onOptionsItemSelected(item);

    }
}
