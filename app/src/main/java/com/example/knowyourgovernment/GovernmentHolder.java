package com.example.knowyourgovernment;

import android.widget.TextView;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;


public class GovernmentHolder extends RecyclerView.ViewHolder  {

    TextView office;
    TextView name;
    TextView party;
    TextView address;
    TextView phone;
    TextView email;
    TextView website;

    GovernmentHolder(View view){
        super(view);
        office = view.findViewById(R.id.office);
        name = view.findViewById(R.id.name);
        party = view.findViewById(R.id.party);
        address = view.findViewById(R.id.address);
        phone = view.findViewById(R.id.phonenumber);
        email = view.findViewById(R.id.email);
        website = view.findViewById(R.id.website);


    }


}
