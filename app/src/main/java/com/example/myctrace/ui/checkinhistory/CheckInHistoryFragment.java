package com.example.myctrace.ui.checkinhistory;

import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.myctrace.R;
import com.example.myctrace.uireusablecomponents.checkInLocation.CheckInLocationAdapter;
import com.example.myctrace.uireusablecomponents.checkInLocation.CheckInLocationModal;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class CheckInHistoryFragment extends Fragment {

    private CheckInHistoryViewModel mViewModel;

    EditText edTxtDate;
    Calendar myCalendar = Calendar.getInstance();

    //Firebase stuff
    DatabaseReference mbase;
    private RecyclerView recyclerView;
    CheckInLocationAdapter adapter;

    public static CheckInHistoryFragment newInstance() {
        return new CheckInHistoryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.check_in_history_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CheckInHistoryViewModel.class);
        // TODO: Use the ViewModel
        View view = getView();

        //Firebase stuff
        mbase = FirebaseDatabase.getInstance()
                .getReference("user")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("checkIns");

        edTxtDate = view.findViewById(R.id.editTextDate);
        recyclerView = view.findViewById(R.id.checkInHistoryRecycler);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);

        search("");

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                myCalendar.set(Calendar.YEAR, i);
                myCalendar.set(Calendar.MONTH, i1);
                myCalendar.set(Calendar.DAY_OF_MONTH, i2);
                updateDisplay();
            }
        };

        edTxtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(
                        getActivity(),
                        date,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });

        edTxtDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                search(String.valueOf(charSequence));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void search(String searchTerm) {

        if(adapter != null) {
            adapter.stopListening();
        }

        FirebaseRecyclerOptions<CheckInLocationModal> options;

        if(searchTerm.equals("")) {
            options = new FirebaseRecyclerOptions.Builder<CheckInLocationModal>()
                   .setIndexedQuery(mbase.orderByKey().limitToLast(20), mbase.getRef(), CheckInLocationModal.class)
                    //.setQuery(mbase, CheckInLocationModal.class)
                    .build();
        } else {
            options = new FirebaseRecyclerOptions.Builder<CheckInLocationModal>()
                    .setIndexedQuery(mbase.orderByKey()
                            .startAt(searchTerm)
                            .endAt(searchTerm+"\uf8ff")
                            .limitToLast(20), mbase.getRef(), CheckInLocationModal.class)
                    //.setQuery(mbase, CheckInLocationModal.class)
                    .build();
        }
        //Firebase stuff
        adapter = new CheckInLocationAdapter(options);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void updateDisplay() {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
        edTxtDate.setText(format.format(myCalendar.getTime()));
    }

    @Override
    public void onStart()
    {
        super.onStart();
        adapter.startListening();
    }


    @Override
    public void onStop()
    {
        super.onStop();
        adapter.stopListening();
    }

}