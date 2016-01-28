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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.grameenfoundation.applabs.ledgerlinkmanager.R;
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.DataHolder;

import java.util.ArrayList;
import java.util.Arrays;

public class TrainingFrag extends Fragment {

    public TrainingFragInterface trainingFragInterface;
    private String selectedOption = null;

    public TrainingFrag() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.training_frag, container, false);
        final ListView listView = (ListView) view.findViewById(R.id.trainingOptions);

        String[] options = new String[]{"Sensitization And Buy-In", "LedgerLink Training",
                "eKeys Training", "General Support", "Refresher Training"};

        ArrayList<String> optionsList = new ArrayList<>();
        optionsList.addAll(Arrays.asList(options));
        final ArrayAdapter<String> listAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.list_trainingoptions, optionsList);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String item = ((TextView) view).getText().toString();
                selectedOption = item;
            }
        });
        return view;
    }

    // Validate selected details
    private boolean validateInputFields() {
        DataHolder.getInstance().setSupportTrainingType(selectedOption);
        if (selectedOption == null) {
            return false;
        }
        return true;
    }

    private void updateInfoToActivity() {
        String command = "next";
        int fragmentNumber = 3;
        if (validateInputFields()) {
            trainingFragInterface.passInfoToActivity(command, fragmentNumber);
        } else {
            Toast.makeText(getActivity(), "Select Training Type", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_training_frag, menu);
        setHasOptionsMenu(true);
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TrainingFragInterface) {
            trainingFragInterface = (TrainingFragInterface) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        trainingFragInterface = null;
    }

    public interface TrainingFragInterface {
        void passInfoToActivity(String command, int fragmentNumber);
    }

}
