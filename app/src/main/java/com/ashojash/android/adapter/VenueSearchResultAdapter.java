package com.ashojash.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.ashojash.android.R;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.struct.StructVenue;

import java.util.List;

public class VenueSearchResultAdapter extends RecyclerView.Adapter<VenueSearchResultAdapter.ViewHolder> {


    private Context context;
    List<StructVenue> structVenueList;
    private Intent intent;

    public VenueSearchResultAdapter(List<StructVenue> structVenueList, Context context, Intent intent) {
        super();
        //Getting all the cities
        this.structVenueList = structVenueList;
        this.context = context;
        this.intent = intent;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: creating view");
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_search_suggestion, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    private String TAG = AppController.TAG;

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final StructVenue structVenue = structVenueList.get(position);
        holder.txtVenueName.setText(structVenue.getName());
        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (intent != null) {
                    intent.putExtra("slug", structVenue.getSlug());
                    AppController.currentActivity.startActivity(intent);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return structVenueList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtVenueName;
        public ViewGroup rootLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "ViewHolder: instanciate");
            txtVenueName = (TextView) itemView.findViewById(R.id.txtVenueNameCardVenueSuggestion);
            rootLayout = (ViewGroup) itemView.findViewById(R.id.cardVenueSuggestion);
        }
    }

}
