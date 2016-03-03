package org.grameenfoundation.applabs.ledgerlinkmanager.frags;


import android.content.Context;
import android.os.Bundle;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.grameenfoundation.applabs.ledgerlinkmanager.JsonData;
import org.grameenfoundation.applabs.ledgerlinkmanager.R;
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.DataHolder;
import org.json.JSONException;
import org.json.JSONObject;


public class LocationFrag extends Fragment {
    private LocationFragInterface locationFragInterface;
    private EditText inputPhysicalAdress;
    private Spinner selectRegion;
    private MapView mapView;
    private GoogleMap googleMap;

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
        mapView = (MapView) view.findViewById(R.id._map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        setupGoogleMaps();
        addLocation();
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

    private void setupGoogleMaps() {
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        googleMap = mapView.getMap();
    }

    private void addLocation() {
        double latitude = JsonData.getInstance().getLatitude();
        double longitude = JsonData.getInstance().getLongitude();
        String location = String.valueOf(latitude) + "," + String.valueOf(longitude);
        DataHolder.getInstance().setLocationCoordinates(location);
        LatLng currentLocation = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions().alpha(0.7f).flat(true).position(currentLocation)
                .title(DataHolder.getInstance().getVslaName()))
                .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 13));
    }

    /**
     * Add information attached to one vsla group into a singleton class. This is when a group is being edited
     */
    private void processVslaInformation(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            String physicalAddress = jsonObject.getString("PhysicalAddress");
            // String RegionName = jsonObject.getString("RegionName");
            int RegionId = Integer.valueOf(jsonObject.getString("RegionId"));
            /**
             * Best approach is to reseed the regions table so that IDs start at 1
             * this is a quick hack that maps the Ids stored in the regionId column in the
             * vsla table against the string array for regions
             * 9 --> 0
             * 10 --> 1
             * 11 --> 2
             * 12 --> 3
             *
             */
            switch (RegionId) {
                case 9:
                    selectRegion.setSelection(0);
                    break;
                case 10:
                    selectRegion.setSelection(1);
                    break;
                case 11:
                    selectRegion.setSelection(2);
                    break;
                case 12:
                    selectRegion.setSelection(3);
                    break;
            }
            inputPhysicalAdress.setText(physicalAddress);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

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

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
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
