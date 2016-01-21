package org.grameenfoundation.applabs.ledgerlinkmanager.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.grameenfoundation.applabs.ledgerlinkmanager.R;
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.DataHolder;

public class TrainingFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {
    private RadioGroup radioGroup;
    private MenuItem cancelMenu;
    private MenuItem editMenu;
    private MenuItem saveMenu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.training_modules, container, false);
        setHasOptionsMenu(true);
        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(this);
        disableRadionGroup();
        return view;
    }

    private void showFlashMessage(String toastMessage) {
        Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
        String _groupSupportType = radioButton.getText().toString();
        DataHolder.getInstance().setSupportTrainingType(_groupSupportType);
    }

    // enable radio buttons
    private void enableRadioGroup() {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            (radioGroup.getChildAt(i)).setEnabled(true);
        }
    }

    // disable radio buttons
    private void disableRadionGroup() {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            (radioGroup.getChildAt(i)).setEnabled(false);
        }
    }

    // Save data to data holder
    private void saveSelectionToDataHolder() {
        if (DataHolder.getInstance().getSupportTrainingType() == null) {
            showFlashMessage("Select Training Module");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_training_information, menu);

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

        switch (item.getItemId()) {

            case R.id.action_edit:
                cancelMenu.setVisible(true);
                saveMenu.setVisible(true);
                editMenu.setVisible(false);
                enableRadioGroup();
                break;

            case R.id.action_save:
                cancelMenu.setVisible(false);
                saveMenu.setVisible(false);
                editMenu.setVisible(true);
                disableRadionGroup();
                saveSelectionToDataHolder();
                break;

        }

        return super.onOptionsItemSelected(item);
    }
}
