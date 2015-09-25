package org.grameenfoundation.applabs.ledgerlinkmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.grameenfoundation.applabs.ledgerlinkmanager.adapters.RecyclerViewAdapter;
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.DataHolder;
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.RecyclerViewListDivider;
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.SharedPreferencesUtils;
import org.grameenfoundation.applabs.ledgerlinkmanager.models.VslaDataModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SearchResults extends AppCompatActivity {
    private ArrayList<VslaDataModel> _vslaDataModel;
    private RecyclerViewAdapter recyclerViewAdapter;
    private CardView empty_view;
    Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        DataHolder.getInstance().clearDataHolder();

        empty_view = (CardView) findViewById(R.id.empty_view);
        FloatingActionButton NewGroupFab = (FloatingActionButton) findViewById(R.id.add_new_group_Fab);
        NewGroupFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchResults.this, VslaGroupDetails.class));
            }
        });
        _vslaDataModel = new ArrayList<>();

        Intent intent = getIntent();
        if (intent != null) {
            String jsonResponseString = intent.getStringExtra("jsonIntent");
            new JsonProcessingAsycTask().execute(jsonResponseString);
        }


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyleView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        recyclerViewAdapter = new RecyclerViewAdapter(_vslaDataModel);
        recyclerViewAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(recyclerViewAdapter);
        RecyclerView.ItemDecoration itemDecoration = new
                RecyclerViewListDivider(this, RecyclerViewListDivider.VERTICAL_LIST);
        recyclerView.addItemDecoration(itemDecoration);

        recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                int vslaId = _vslaDataModel.get(position).getVslaId();
                Intent intent = new Intent(SearchResults.this, VslaGroupDetails.class);
                SharedPreferencesUtils.saveSharedPreferences(activity, "IsEditing", "1");
                SharedPreferencesUtils.saveSharedPreferences(activity, "vslaId", String.valueOf(vslaId));
                intent.putExtra("VslaId", vslaId);
                startActivity(intent);
                finish();
            }
        });

    }

    /**
     * Asynchronous task to process the list of groups off the main UI
     */
    private class JsonProcessingAsycTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonString = params[0];
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONArray jsonArray = jsonObject.getJSONArray("searchForVslaResult");
                if (jsonArray.length() != 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        VslaDataModel dataSet = new VslaDataModel();
                        String VSLAName = obj.getString("VslaName");
                        String PhysicalAddress = obj.getString("PhysicalAddress");
                        String ResponsiblePerson = obj.getString("GroupRepresentativeName");
                        int vslaId = obj.getInt("VslaId");
                        dataSet.setGroupName(VSLAName);
                        dataSet.setPhysicalAddress(PhysicalAddress);
                        dataSet.setMemberName(ResponsiblePerson);
                        dataSet.setVslaId(vslaId);
                        _vslaDataModel.add(dataSet);
                    }
                } else {
                    SharedPreferencesUtils.saveSharedPreferences(activity, "IsEditing", "0");
                    SharedPreferencesUtils.saveSharedPreferences(activity, "vslaId", "-1");
                    return "-1";

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == "-1") {
                empty_view.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_group_search_results, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_add_group) {
            SharedPreferencesUtils.saveSharedPreferences(activity, "IsEditing", "0");
            SharedPreferencesUtils.saveSharedPreferences(activity, "vslaId", "-1");
            startActivity(new Intent(SearchResults.this, VslaGroupDetails.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
