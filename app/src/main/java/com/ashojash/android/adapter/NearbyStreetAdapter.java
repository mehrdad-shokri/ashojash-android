package com.ashojash.android.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.ashojash.android.R;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.Street;
import java.util.List;

public class NearbyStreetAdapter extends RecyclerView.Adapter<NearbyStreetAdapter.ViewHolder> {

  List<Street> streetList;
  private OnCardClickListener listener;

  public NearbyStreetAdapter(List<Street> streetList) {
    this.streetList = streetList;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v =
        LayoutInflater.from(parent.getContext())
            .inflate(R.layout.card_street_nearby, parent, false);
    NearbyStreetAdapter.ViewHolder viewHolder = new NearbyStreetAdapter.ViewHolder(v);
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(final NearbyStreetAdapter.ViewHolder holder, int position) {
    final Street street = streetList.get(position);
    holder.txtStreetName.setText(street.name);
    if (listener != null) {
      holder.rootView.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          listener.onClick(street);
        }
      });
    }
    if (street.distance == 0) {
      holder.txtDistance.setVisibility(View.GONE);
    } else {
      String lessThan = AppController.context.getResources().getString(R.string.less_than);
      String more_than = AppController.context.getResources().getString(R.string.more_than);
      if (street.distance < 200) {
        holder.txtDistance.setText(lessThan + " 200 متر");
      } else if (street.distance < 500) {
        holder.txtDistance.setText(lessThan + " 500 متر");
      } else {
        holder.txtDistance.setText(more_than + " 500 متر");
      }
    }
  }

  public void setOnItemSelectionListener(OnCardClickListener listener) {
    this.listener = listener;
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    public TextView txtStreetName;
    public TextView txtDistance;
    public ViewGroup rootView;

    public ViewHolder(View itemView) {
      super(itemView);
      txtStreetName = (TextView) itemView.findViewById(R.id.txtStreetName);
      txtDistance = (TextView) itemView.findViewById(R.id.txtStreetDistance);
      rootView = (ViewGroup) itemView.findViewById(R.id.rootView);
    }
  }

  @Override public int getItemCount() {
    return streetList.size();
  }
}

