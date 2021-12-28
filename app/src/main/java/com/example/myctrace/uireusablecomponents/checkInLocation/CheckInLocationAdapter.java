package com.example.myctrace.uireusablecomponents.checkInLocation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myctrace.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

// FirebaseRecyclerAdapter is a class provided by
// FirebaseUI. it provides functions to bind, adapt and show
// database contents in a Recycler View
public class CheckInLocationAdapter extends FirebaseRecyclerAdapter<
        CheckInLocationModal, CheckInLocationAdapter.checkInLocationsViewholder> {

    public CheckInLocationAdapter(
            @NonNull FirebaseRecyclerOptions<CheckInLocationModal> options)
    {
        super(options);
    }

    // Function to bind the view in Card view(here
    // "checkInLocation.xml") iwth data in
    // model class(here "checkInLocation.class")
    @Override
    protected void
    onBindViewHolder(@NonNull checkInLocationsViewholder holder,
                     int position, @NonNull CheckInLocationModal model)
    {

        holder.location.setText(model.getLocation());

        holder.dateTime.setText(model.getDateTime());

    }


    @NonNull
    @Override
    public checkInLocationsViewholder
    onCreateViewHolder(@NonNull ViewGroup parent,
                       int viewType)
    {
        View view
                = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_component_location_card, parent, false);
        return new CheckInLocationAdapter.checkInLocationsViewholder(view);
    }


    class checkInLocationsViewholder
            extends RecyclerView.ViewHolder {
        TextView location, dateTime;
        public checkInLocationsViewholder(@NonNull View itemView)
        {
            super(itemView);

            location = itemView.findViewById(R.id.txtViewLocationName);
            dateTime = itemView.findViewById(R.id.txtViewDateTime);
        }
    }
}
