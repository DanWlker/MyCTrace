package com.example.myctrace.ui.searchtestlocation;

import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myctrace.R;
import com.example.myctrace.dataclass.PPVLocation;
import com.example.myctrace.dataclass.PPVLocationWrapper;
import com.example.myctrace.uireusablecomponents.PPVLocation.PPVLocationAdapter;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kotlin.OptionalExpectation;

public class SearchTestLocation extends Fragment {

    private SearchTestLocationViewModel mViewModel;
    private ArrayList<PPVLocation> locations;
    private ArrayList<PPVLocation> allLocations;
    private String previousResult;
    PPVLocationAdapter adapter;

    EditText edTxtSearchLocation;
    ListView listViewPPVHolder;

    public static SearchTestLocation newInstance() {
        return new SearchTestLocation();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_test_location_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SearchTestLocationViewModel.class);


        // TODO: Use the ViewModel
        View view = getView();
        locations = new ArrayList<PPVLocation>();
        allLocations = new ArrayList<PPVLocation>();
        previousResult = "";

        //fetch data from api
        fetchData();

        edTxtSearchLocation = view.findViewById(R.id.edTxtSearchLocation);
        edTxtSearchLocation.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                // If the event is a key-down event on the "enter" button
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        (i == KeyEvent.KEYCODE_ENTER)) {
                    Log.d("Custom", "Enter is pressed on edit text.");
                    // Perform action on key press
                    displayResults(edTxtSearchLocation.getText().toString());
                    return true;
                }
                return false;
            }
        });

    }

    private void displayResults(String searchTerm) {

        if(searchTerm.equals("") || searchTerm.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a location.", Toast.LENGTH_LONG);
            Log.d("Custom", "Empty input");
            previousResult = "";
            adapter.clear();
            return;
        }

        if(previousResult.equals(searchTerm)) {
            Log.d("Custom", "Search term is same");
            return;
        }
        previousResult = searchTerm;

        ArrayList<PPVLocation> searchedLocations = new ArrayList<PPVLocation>();
        Log.d("Custom", "Adding to searched Locations");
        for(PPVLocation location : allLocations) {
            if(location.st.equals(searchTerm) || location.dist.equals(searchTerm)) {
                searchedLocations.add(location);
            }
        }

        if(searchedLocations.size() <= 0) {
            Log.d("Custom", "No results");
            Toast.makeText(getContext(), "No results", Toast.LENGTH_LONG).show();
            return;
        }

        Log.d("Custom", "Adding to adapter");
        adapter.clear();
        adapter.addAll(searchedLocations);

    }


    private void fetchData() {

        //show loading spinner
        ProgressDialog nDialog;
        nDialog = new ProgressDialog(getContext());
        nDialog.setMessage("Loading..");
        nDialog.setTitle("Fetching Data");
        nDialog.setIndeterminate(false);
        nDialog.setCancelable(false);
        nDialog.show();

        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "https://www.vaksincovid.gov.my/json/ppv.json";

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.d("Custom", response);
                        try {
                            JSONObject topLevel = new JSONObject(response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1));
                            JSONArray jArr = topLevel.getJSONArray("data");
                            Gson gson = new Gson();

                            for(int i=0; i < jArr.length(); ++i) {
                                JSONObject jsonObj = jArr.getJSONObject(i);
                                PPVLocation tempObj = gson.fromJson(jsonObj.toString(), PPVLocation.class);
                                locations.add(tempObj);
                                allLocations.add(tempObj);
                            }
                        } catch (JSONException e) {
                            Log.d("Custom", e.toString());
                        }
                        nDialog.dismiss();
                        Log.d("Custom", "Created all json objects");

                        listViewPPVHolder = getView().findViewById(R.id.listViewPPVHolder);
                        adapter = new PPVLocationAdapter(getContext(), locations);
                        listViewPPVHolder.setAdapter(adapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Error, failed to retrieve cases", Toast.LENGTH_LONG);
            }
        });

        queue.add(stringRequest);

    }

}