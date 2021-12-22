package com.example.myctrace.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    TextView tvConfirmedCase, tvRecoveredCase, tvVacProgress;
    MaterialCardView cvRisk, cvVac, cvTodo, cvToknow;
    LinearProgressIndicator pbVac;
    String population = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.tvUName;
        cvRisk = binding.cvRisk;
        cvVac = binding.cvVac;
        cvTodo = binding.cvTodo;
        cvToknow = binding.cvToknow;
        tvConfirmedCase = binding.tvConfirmedCase;
        tvRecoveredCase = binding.tvRecoveredCase;
        tvVacProgress = binding.tvVacProgress;
        pbVac = binding.pbVacProgress;

//        cvRisk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(getActivity(), RiskAssessmentActivity.class);
//                startActivity(i);
//            }
//        });

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

        fetchCovidAPI();
        fetchVaccineAPI();

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
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
                            population = jsonObject.getString("population");

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}