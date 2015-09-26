package org.grameenfoundation.applabs.ledgerlinkmanager.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.grameenfoundation.applabs.ledgerlinkmanager.VslaGroupDetails;
import org.grameenfoundation.applabs.ledgerlinkmanager.R;
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.DataHolder;
import org.grameenfoundation.applabs.ledgerlinkmanager.interfaces.IPhoneInformation;

public class PhoneInformationFrag extends Fragment implements IPhoneInformation {
    private Context context;
    private EditText groupPhoneNumber;
    private MenuItem cancelMenu, editMenu, saveMenu;

    public PhoneInformationFrag() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_phone_information, container, false);
        setHasOptionsMenu(true);
        initializeUIComponent(view);
        disableEditing();
        return view;
    }

    /**
     * initialize the UI component
     **/
    private void initializeUIComponent(View view) {
        groupPhoneNumber = (EditText) view.findViewById(R.id.groupPhoneNumber);
    }

    /**
     * Save data from the text fields to the singleton class
     */
    private void saveDataToDataHolderClass() {
        if (groupPhoneNumber != null) {
            DataHolder.getInstance().setGroupPhoneNumber(groupPhoneNumber.getText().toString());
        }
    }

    /**
     * Enable the edit boxes
     */
    private void enableEditing() {
        groupPhoneNumber.setEnabled(true);
    }

    /**
     * Disable Edit boxes
     */
    public void disableEditing() {
        groupPhoneNumber.setEnabled(false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = getActivity();
        ((VslaGroupDetails) context).phoneInformationInterface = this;
    }

    @Override
    public void passPhoneInformation(String issuedPhoneNumber) {
        try {
            groupPhoneNumber.setText(issuedPhoneNumber);

            /**  Then save the information to the data holder   **/
            saveDataToDataHolderClass();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_phone_information, menu);
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
            enableEditing();
        }
        if (id == R.id.action_save) {
            cancelMenu.setVisible(false);
            saveMenu.setVisible(false);
            editMenu.setVisible(true);
            saveDataToDataHolderClass();
            disableEditing();
        }
        return super.onOptionsItemSelected(item);

    }
}
