package org.grameenfoundation.applabs.ledgerlinkmanager.trainingOptions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import org.grameenfoundation.applabs.ledgerlinkmanager.CreateGroup;
import org.grameenfoundation.applabs.ledgerlinkmanager.R;
import org.grameenfoundation.applabs.ledgerlinkmanager.TrainingOptionsData;
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.DataHolder;

public class EkeysTraining extends AppCompatActivity {
    private CheckBox cbDayOne, cbDayTwo, cbDayThree, cbDayFour, cbDayFive, cbDaySix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ekeys_training_activity);
        cbDayOne = (CheckBox) findViewById(R.id.dayone);
        cbDayTwo = (CheckBox) findViewById(R.id.daytwo);
        cbDayThree = (CheckBox) findViewById(R.id.daythree);
        cbDayFour = (CheckBox) findViewById(R.id.dayfour);
        cbDayFive = (CheckBox) findViewById(R.id.dayfive);
        cbDaySix = (CheckBox) findViewById(R.id.daysix);
    }

    private String selectedModules() {
        StringBuilder result = new StringBuilder();
        if (cbDayOne.isChecked()) {
            result.append("Introduction to eKeys, ");
        }
        if (cbDayTwo.isChecked()) {
            result.append("Phone & Mobile Money Usage, ");
        }
        if (cbDayThree.isChecked()) {
            result.append("Airtel Weza Review, ");
        }
        if (cbDayFour.isChecked()) {
            result.append("eKeys Review, ");
        }
        if (cbDayFive.isChecked()) {
            result.append("Group Submission and sensitization,");
        }
        if (cbDaySix.isChecked()) {
            result.append("eKeys Assessment");
        }
        return result.toString() != null ? result.toString() : null;
    }

    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        MenuItem actionViewItem = menu.findItem(R.id.action_save);
        View v = MenuItemCompat.getActionView(actionViewItem);
        Button b = (Button) v.findViewById(R.id.btnSave);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = selectedModules();
                if (result == null || result.trim().equals("")) {
                    Toast.makeText(getApplicationContext(), "Select Atleast one of the options", Toast.LENGTH_SHORT).show();
                } else {
                    TrainingOptionsData.getInstance().setStarredFragment("3");
                    DataHolder.getInstance().setSupportTrainingType(result);
                    Intent intent = new Intent(EkeysTraining.this, CreateGroup.class);
                    startActivity(intent);

                }
            }
        });
        return super.onPrepareOptionsPanel(view, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_ekeys_training, menu);
        return true;
    }
}
