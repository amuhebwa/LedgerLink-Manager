package org.grameenfoundation.applabs.ledgerlinkmanager;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import org.grameenfoundation.applabs.ledgerlinkmanager.frags.LocationFrag;
import org.grameenfoundation.applabs.ledgerlinkmanager.frags.SubmitDataFrag;
import org.grameenfoundation.applabs.ledgerlinkmanager.frags.TrainingFrag;
import org.grameenfoundation.applabs.ledgerlinkmanager.frags.VslaFrag;


public class CreateGroup extends AppCompatActivity implements VslaFrag.VslaFragInterface,
        LocationFrag.LocationFragInterface, TrainingFrag.TrainingFragInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_group);
        loadDefaultFragment();
    }

    private void loadDefaultFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        VslaFrag vslaFrag = new VslaFrag();
        fragmentTransaction.replace(R.id.frame_container, vslaFrag);
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

    private void moveToPreviousFragment(int fragmentNumber) {

    }
}
