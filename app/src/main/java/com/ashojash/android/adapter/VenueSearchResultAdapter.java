package com.ashojash.android.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.ashojash.android.R;
import com.ashojash.android.model.Venue;

import java.util.List;

public class VenueSearchResultAdapter extends RecyclerView.Adapter<VenueSearchResultAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onClick(Venue venue);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    List<Venue> venueList;

    public VenueSearchResultAdapter(List<Venue> venueList) {
        super();
        this.venueList = venueList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_search_suggestion, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Venue venue = venueList.get(position);
        holder.txtVenueName.setText(venue.name);
        if (onItemClickListener != null)
            holder.rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onClick(venue);
                }
            });
    }


    @Override
    public int getItemCount() {
        return venueList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtVenueName;
        public ViewGroup rootLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            txtVenueName = (TextView) itemView.findViewById(R.id.txtVenueNameCardVenueSuggestion);
            rootLayout = (ViewGroup) itemView.findViewById(R.id.cardVenueSuggestion);
        }
    }

}
