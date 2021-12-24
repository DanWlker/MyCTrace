package com.example.myctrace.ui.home;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myctrace.R;
import com.example.myctrace.databinding.FragmentHomeBinding;
import com.example.myctrace.ui.notifications.NotificationsFragment;
import com.example.myctrace.ui.todo.TodoFragment;
import com.example.myctrace.ui.toknow.ToknowFragment;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class HomeFragment extends Fragment {

    //private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    TextView tvConfirmedCase, tvRecoveredCase, tvVacProgress;
    MaterialCardView cvTodo, cvToknow;
    LinearProgressIndicator pbVac;

    ImageView riskIcon;
    TextView riskTitle, riskUpdate, riskDate;
    MaterialCardView cvRisk;

    ImageView vacIcon;
    TextView vacTitle, vacUpdate, vacDate;
    MaterialCardView cvVac;

    private DatabaseReference mDatabase;

    private String riskStatus = "low";
    private String vacStatus = "firstdose";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        bindElements();

        setRiskState(riskStatus);
        setVacStatus(vacStatus);

        cvTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment toDoFrag = new TodoFragment();

                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                ft.replace(R.id.nav_host_fragment_content_main, toDoFrag);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        cvToknow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment toKnowFrag = new ToknowFragment();

                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                ft.replace(R.id.nav_host_fragment_content_main, toKnowFrag);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        //set statistics by getting data from API
        fetchCovidAPI();
        fetchVaccineAPI();

        return root;
    }

    private void bindElements()
    {
        //risk elements
        cvRisk = binding.cvRisk;
        riskIcon = binding.riskIcon;
        riskTitle = binding.riskTitle;
        riskUpdate = binding.riskUpdated;
        riskDate = binding.riskDate;

        //vac elements
        cvVac = binding.cvVac;
        vacIcon = binding.vacIcon;
        vacTitle = binding.vacTitle;
        vacUpdate = binding.vacUpdated;
        vacDate = binding.vacDate;

        cvTodo = binding.cvTodo;
        cvToknow = binding.cvToknow;
        tvConfirmedCase = binding.tvConfirmedCase;
        tvRecoveredCase = binding.tvRecoveredCase;
        tvVacProgress = binding.tvVacProgress;
        pbVac = binding.pbVacProgress;
    }

    private void fetchCovidAPI()
    {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "https://disease.sh/v3/covid-19/countries/MY?yesterday=1";

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response.toString());

                            tvConfirmedCase.setText(jsonObject.getString("todayCases"));
                            tvRecoveredCase.setText(jsonObject.getString("todayRecovered"));
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "API GET FAILED", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        queue.add(stringRequest);
    }

    private void fetchVaccineAPI()
    {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "https://www.vaksincovid.gov.my/json/heatmap.json";

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response.toString());

                            JSONObject vacjson = jsonObject.getJSONArray("data").getJSONObject(0);
                            String vakcomplete = vacjson.getString("vakdosecomplete");
                            String population = vacjson.getString("pop");

                            double res = 100 * Double.valueOf(vakcomplete) / Double.valueOf(population);

                            tvVacProgress.setText(String.format("%.2f", res) + "%");
                            pbVac.setProgressCompat((int)Math.round(res), true);

                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        tvVacProgress.setText("Nope");
                    }
                }
        );

        queue.add(stringRequest);
    }

    private void setRiskState(String riskstatus)
    {
        if (riskstatus == "high")
        {
            cvRisk.setCardBackgroundColor(getResources().getColor(R.color.orange_secondary));
            riskIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_circle_orange));
            riskTitle.setTextColor(getResources().getColor(R.color.orange_primary));
            riskUpdate.setTextColor(getResources().getColor(R.color.orange_primary));
            riskDate.setTextColor(getResources().getColor(R.color.orange_primary));
        } else {
            cvRisk.setCardBackgroundColor(getResources().getColor(R.color.blue_secondary));
            riskIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_circle_blue));
            riskTitle.setTextColor(getResources().getColor(R.color.blue_primary));
            riskUpdate.setTextColor(getResources().getColor(R.color.blue_primary));
            riskDate.setTextColor(getResources().getColor(R.color.blue_primary));
        }
    }

    private void setVacStatus(String vacStatus)
    {
        if (vacStatus == "completed")
        {
            cvVac.setCardBackgroundColor(getResources().getColor(R.color.green_secondary));
            vacIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_user_green));
            vacTitle.setTextColor(getResources().getColor(R.color.green_primary));
            vacUpdate.setTextColor(getResources().getColor(R.color.green_primary));
            vacDate.setTextColor(getResources().getColor(R.color.green_primary));
        } else {
            cvVac.setCardBackgroundColor(getResources().getColor(R.color.orange_secondary));
            vacIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_user_orange));
            vacTitle.setTextColor(getResources().getColor(R.color.orange_primary));
            vacUpdate.setTextColor(getResources().getColor(R.color.orange_primary));
            vacDate.setTextColor(getResources().getColor(R.color.orange_primary));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}