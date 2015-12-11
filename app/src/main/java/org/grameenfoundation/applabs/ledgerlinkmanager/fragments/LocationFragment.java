package org.grameenfoundation.applabs.ledgerlinkmanager.fragments;


import android.app.Activity;
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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;

import org.grameenfoundation.applabs.ledgerlinkmanager.R;
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.DataHolder;

public class LocationFragment extends Fragment{
    private EditText physicalAdress;
    private Spinner selectRegion;
    private MenuItem cancelMenu, editMenu, saveMenu;

    private MapView mapView;
    private GoogleMap googleMap;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.location_information, container, false);

        // set up google maps
        mapView = (MapView) view.findViewById(R.id._map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume(); // Display the map immediately

        setupGoogleMaps(); // set up google maps

        setHasOptionsMenu(true);
        physicalAdress = (EditText) view.findViewById(R.id.PhysicalAddress);
        selectRegion = (Spinner) view.findViewById(R.id.RegionName);
        addRegionNames(selectRegion);

        disableEditing(); // disable imput fields
        return view;
    }

    // aet up the map
    private void setupGoogleMaps() {
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        googleMap = mapView.getMap();
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
        mapView.onResume();

        super.onResume();
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
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

    private void setDataToDataHolderClass() {
        if (physicalAdress.getText().toString().isEmpty() || physicalAdress.getText().toString().length() < 4) {
            physicalAdress.setError("Physical Address is Empty");
        } else {
            DataHolder.getInstance().setPhysicalAddress(physicalAdress.getText().toString());
        }
    }

    // enable Edit Boxes
    private void enableEditing() {
        physicalAdress.setEnabled(true);
        selectRegion.setEnabled(true);
    }

    // disable Edit Boxes
    private void disableEditing() {
        physicalAdress.setEnabled(false);
        selectRegion.setEnabled(false);
    }

    // clear Error messages
    private void clearErrorMessages() {
        physicalAdress.setError(null);
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

        switch (item.getItemId()) {
            case R.id.action_edit:
                cancelMenu.setVisible(true);
                saveMenu.setVisible(true);
                editMenu.setVisible(false);
                clearErrorMessages();
                enableEditing();
                break;

            case R.id.action_save:
                cancelMenu.setVisible(false);
                saveMenu.setVisible(false);
                editMenu.setVisible(true);
                setDataToDataHolderClass();
                disableEditing();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}