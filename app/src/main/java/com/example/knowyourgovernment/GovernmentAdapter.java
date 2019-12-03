package com.example.knowyourgovernment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GovernmentAdapter extends RecyclerView.Adapter<GovernmentHolder> {

 public static final String TAG = "GovernmentAdapter";
    private ArrayList<Government> governmentArr;
    private MainActivity mainAct;


    public GovernmentAdapter(ArrayList<Government> Government, MainActivity ma){
        this.governmentArr = Government;
        mainAct = ma;
  }



    @Override
    public void onBindViewHolder(@NonNull GovernmentHolder holder, int position) {
        Government government = governmentArr.get(position);
        holder.party.setText(government.getOffice());
        holder.name.setText(String.format("%s (%s)",government.getName(),government.getParty()));

    }

@Override
  public GovernmentHolder onCreateViewHolder(@Nullable final ViewGroup parent, int viewType){
      View itemView = LayoutInflater.from(parent.getContext())
              .inflate(R.layout.politic_list, parent, false);

      itemView.setOnClickListener(mainAct);
      itemView.setOnLongClickListener(mainAct);

      return new GovernmentHolder(itemView);
  }



    @Override
    public int getItemCount() {
        return governmentArr.size();
    }

}
