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

import org.grameenfoundation.applabs.ledgerlinkmanager.adapters.DataAdapter;
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.DataHolder;
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.RecyclerViewListDivider;
import org.grameenfoundation.applabs.ledgerlinkmanager.helpers.SharedPrefs;
import org.grameenfoundation.applabs.ledgerlinkmanager.models.VslaInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SearchResults extends AppCompatActivity {
    private ArrayList<VslaInfo> vslaInfo;
    private CardView emptyView;
    Map<Integer, String> jsonObjectMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_results);
        jsonObjectMap = new HashMap<>();
        DataHolder.getInstance().clearDataHolder();
        vslaInfo = new ArrayList<>();

        emptyView = (CardView) findViewById(R.id.empty_view);
        FloatingActionButton NewGroupFab = (FloatingActionButton) findViewById(R.id.add_new_group_Fab);

        NewGroupFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchResults.this, CreateGroup.class));
            }
        });


        Intent intent = getIntent();
        if (intent != null) {
            String jsonResponseString = intent.getStringExtra("jsonIntent");
            new JsonProcessingAsycTask().execute(jsonResponseString);
        }


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyleView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        DataAdapter dataAdapter = new DataAdapter(vslaInfo);
        dataAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(dataAdapter);
        RecyclerView.ItemDecoration itemDecoration = new RecyclerViewListDivider(this, RecyclerViewListDivider.VERTICAL_LIST);
        recyclerView.addItemDecoration(itemDecoration);
        dataAdapter.setOnItemClickListener(new DataAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                int vslaId = vslaInfo.get(position).getVslaId();
                JsonData.getInstance().setIsEditing(true);
                JsonData.getInstance().setVslaId(String.valueOf(vslaId));
                String jsonString = jsonObjectMap.get(vslaId);
                JsonData.getInstance().setVslaJsonStringData(jsonString);

                Intent intent = new Intent(SearchResults.this, CreateGroup.class);
                startActivity(intent);
                finish();
            }
        });

    }

    // asynchronous task to process JSON request
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
                JSONArray jsonArray = jsonObject.getJSONArray("searchVslaResult");

                if (jsonArray.length() != 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        VslaInfo dataSet = new VslaInfo();
                        String VSLAName = obj.getString("VslaName");
                        String PhysicalAddress = obj.getString("PhysicalAddress");
                        String ResponsiblePerson = obj.getString("representativeName");
                        int vslaId = obj.getInt("VslaId");
                        dataSet.setGroupName(VSLAName);
                        dataSet.setPhysicalAddress(PhysicalAddress);
                        dataSet.setMemberName(ResponsiblePerson);
                        dataSet.setVslaId(vslaId);
                        vslaInfo.add(dataSet);
                        jsonObjectMap.put(vslaId, obj.toString());// Add the whole json object to the hashmap
                    }
                } else {
                    JsonData.getInstance().setIsEditing(false);
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
                emptyView.setVisibility(View.VISIBLE);
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

        if (item.getItemId() == R.id.action_add_group) {
            JsonData.getInstance().setIsEditing(false);
            startActivity(new Intent(SearchResults.this, CreateGroup.class));
            return true;

        }

        return super.onOptionsItemSelected(item);
    }


}
