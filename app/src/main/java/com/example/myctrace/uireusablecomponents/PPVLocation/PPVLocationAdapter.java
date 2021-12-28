package com.example.myctrace.uireusablecomponents.PPVLocation;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.myctrace.R;
import com.example.myctrace.dataclass.PPVLocation;

import java.util.ArrayList;

public class PPVLocationAdapter extends ArrayAdapter<PPVLocation> {
    public PPVLocationAdapter(Context context, ArrayList<PPVLocation> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        PPVLocation ppvLocation = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.component_ppv_location, parent, false);
        }

        // Lookup view for data population
        TextView txtViewLocation = (TextView) convertView.findViewById(R.id.txtViewLocation);
        TextView txtViewDestination = (TextView) convertView.findViewById(R.id.txtViewDestination);
        TextView txtViewState = (TextView) convertView.findViewById(R.id.txtViewState);

        // Populate the data into the template view using the data object
        Log.d("Custom", "Populating view");
        txtViewLocation.setText(ppvLocation.ppvc);
        txtViewDestination.setText(ppvLocation.dist);
        txtViewState.setText(ppvLocation.st);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ppvLocation.linky.equals("") || ppvLocation.linky.isEmpty()) {
                    return;
                }

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ppvLocation.linky));
                getContext().startActivity(browserIntent);
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }
}
