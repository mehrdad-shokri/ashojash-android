package com.ashojash.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.ashojash.android.R;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.struct.StructMenu;
import com.ashojash.android.ui.UiUtils;

import java.util.List;

public class VenueMenusAdapter extends RecyclerView.Adapter<VenueMenusAdapter.ViewHolder> {
    private Context context;
    List<StructMenu> structMenuList;
    private Intent intent;

    public VenueMenusAdapter(List<StructMenu> structReviewList, Context context, Intent intent) {
        super();
        //Getting all the cities
        this.structMenuList = structReviewList;
        this.context = context;
        this.intent = intent;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_menu, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    private String TAG = AppController.TAG;

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final StructMenu structMenu = structMenuList.get(position);
        holder.txtMenuItemName.setText(structMenu.getName());
        holder.txtMenuItemPrice.setText(context.getString(R.string.item_price).replace("{{itemPrice}}", UiUtils.toPersianNumber(UiUtils.formatCurrency(String.valueOf(structMenu.getPrice())))));
        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppController.currentActivity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return structMenuList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ViewGroup rootLayout;
        public TextView txtMenuItemName;
        public TextView txtMenuItemPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            rootLayout = (ViewGroup) itemView.findViewById(R.id.cardBasicMenu);
            txtMenuItemName = (TextView) itemView.findViewById(R.id.txtMenuItemNameMenuBasicCard);
            txtMenuItemPrice = (TextView) itemView.findViewById(R.id.txtMenuPriceMenuBasicCard);
        }
    }
}
