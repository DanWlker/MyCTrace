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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
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
import com.example.myctrace.ui.todo.TodoFragment;
import com.example.myctrace.ui.toknow.ToknowFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private DatabaseReference mbase;

    //initializing layout components
    TextView tvConfirmedCase, tvRecoveredCase, tvVacProgress,
                tvDeathCase, tvActiveCase, tvUname, riskTitle,
                riskUpdate, riskDate, vacTitle, vacUpdate, vacDate;
    MaterialCardView cvTodo, cvToknow, cvRisk, cvVac;
    LinearProgressIndicator pbVac;
    ImageView riskIcon, vacIcon;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //bind layout elements
        bindElements();

        //setting firebase reference
        mbase = FirebaseDatabase.getInstance()
                .getReference("user")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        //get username
        setUsername();

        //update risk and vaccination status
        checkRisk();
        checkVac();

        //set statistics by getting data from API
        fetchCovidAPI();
        fetchVaccineAPI();

        //Things-to-do card onclick listener
        cvTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment toDoFrag = new TodoFragment();
                //switch to to do fragment
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
                //switch to to know fragment
                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                ft.replace(R.id.nav_host_fragment_content_main, toKnowFrag);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        return root;
    }

    private void setUsername() {
        //database listener
        mbase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) { return; }

                String uname;
                //check if username exist in database
                if (task.getResult().child("uname").exists()) {
                    uname = String.valueOf((task.getResult().child("uname").getValue()));
                } else {
                    uname = "User";
                }

                //set username to textview
                tvUname.setText("Hello, " + uname);
            }
        });
    }

    private void checkRisk() {
        //database listener
        mbase.child("riskInfo").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful()) { return; }

                String riskStatus, dateTime;

                //check if risk status exist
                if(task.getResult().child("riskStatus").exists()) {
                    riskStatus = String.valueOf(task.getResult().child("riskStatus").getValue());
                } else {
                    riskStatus = "No Data";
                }

                //check if risk date exist
                if(task.getResult().child("riskStatus").exists()) {
                    dateTime = String.valueOf(task.getResult().child("dateTime").getValue());
                } else {
                    dateTime = "0";
                }

                //convert from epoch time to readable time format
                Long dateTimeLong = Long.valueOf(dateTime);
                Date date = new Date(dateTimeLong*1000);
                DateFormat format = new SimpleDateFormat("dd MMMM yyyy");
                format.setTimeZone(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
                String formatted = format.format(date);

                //set textviews respectively
                riskTitle.setText(riskStatus + " Risk");
                setRiskState(riskStatus);

                if(dateTime.equals("0")) {
                    riskDate.setText("No Asssement Date");
                } else {
                    riskDate.setText(formatted);
                }
            }
        });
    }

    private void checkVac() {
        //database listener
        mbase.child("vacInfo").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful())
                    return;

                String dosage = "No Vaccination";
                String dateTime = "0";

                //check if vaccination data exist
                //if second dose info exist, user is fully vaccinated
                //else if only first dose exist, user is partially vaccinated
                if(task.getResult().child("secondDose").exists()) {
                    dosage = "Fully Vaccinated";
                    dateTime = String.valueOf(task.getResult().child("secondDose").getValue());
                } else if(task.getResult().child("firstDose").exists()) {
                    dosage = "First Dose";
                    dateTime = String.valueOf(task.getResult().child("firstDose").getValue());
                }

                //convert from epoch time to readable time format
                Long dateTimeLong = Long.valueOf(dateTime);
                Date date = new Date(dateTimeLong*1000);
                DateFormat format = new SimpleDateFormat("dd MMMM yyyy");
                format.setTimeZone(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
                String formatted = format.format(date);

                //set text views respectively
                vacTitle.setText(dosage);
                setVacStatus(dosage);

                if(dosage.equals("No Vaccination"))
                    vacDate.setText("No Vaccination Date");
                else
                    vacDate.setText(formatted);
            }
        });
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

        //user components
        tvUname = binding.tvUName;

        //clickable cards
        cvTodo = binding.cvTodo;
        cvToknow = binding.cvToknow;

        //APIs elements
        tvConfirmedCase = binding.tvConfirmedCase;
        tvRecoveredCase = binding.tvRecoveredCase;
        tvActiveCase = binding.tvActiveCase;
        tvDeathCase = binding.tvDeathCase;
        tvVacProgress = binding.tvVacProgress;
        pbVac = binding.pbVacProgress;
    }

    private void fetchCovidAPI()
    {
        //GET data from API
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

                            //set textviews respectively
                            tvConfirmedCase.setText(jsonObject.getString("todayCases"));
                            tvRecoveredCase.setText(jsonObject.getString("todayRecovered"));
                            tvDeathCase.setText(jsonObject.getString("todayDeaths"));
                            tvActiveCase.setText(jsonObject.getString("active"));

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
        //GET data from API
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

                            //index 0 of json array is for "whole malaysia"
                            JSONObject vacjson = jsonObject.getJSONArray("data").getJSONObject(0);

                            //calculating vaccination progress with dosecomplete per population
                            String vakcomplete = vacjson.getString("vakdosecomplete");
                            String population = vacjson.getString("pop");
                            double res = 100 * Double.valueOf(vakcomplete) / Double.valueOf(population);

                            //set textviews respectively
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
                        tvVacProgress.setText("API GET FAILED");
                    }
                }
        );
        queue.add(stringRequest);
    }

    private void setRiskState(String status)
    {
        if (status.equals("High"))
        {
            //if risk status is high, set all respective elements as orange
            cvRisk.setCardBackgroundColor(getResources().getColor(R.color.orange_secondary));
            riskIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_circle_orange));
            riskTitle.setTextColor(getResources().getColor(R.color.orange_primary));
            riskUpdate.setTextColor(getResources().getColor(R.color.orange_primary));
            riskDate.setTextColor(getResources().getColor(R.color.orange_primary));
        } else {
            //else, set all respective elements as blue
            cvRisk.setCardBackgroundColor(getResources().getColor(R.color.blue_secondary));
            riskIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_circle_blue));
            riskTitle.setTextColor(getResources().getColor(R.color.blue_primary));
            riskUpdate.setTextColor(getResources().getColor(R.color.blue_primary));
            riskDate.setTextColor(getResources().getColor(R.color.blue_primary));
        }
    }

    private void setVacStatus(String dosage)
    {
        if (dosage == "Fully Vaccinated")
        {
            //if user is fully vaccinated, set all respective elements as green
            cvVac.setCardBackgroundColor(getResources().getColor(R.color.green_secondary));
            vacIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_user_green));
            vacTitle.setTextColor(getResources().getColor(R.color.green_primary));
            vacUpdate.setTextColor(getResources().getColor(R.color.green_primary));
            vacDate.setTextColor(getResources().getColor(R.color.green_primary));
        } else {
            //else, set all respective elements as orange
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