package com.ashojash.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.ashojash.android.R;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.Menu;

import java.util.List;

import static com.ashojash.android.util.UiUtil.formatCurrency;
import static com.ashojash.android.util.UiUtil.toPersianNumber;

public class VenueMenusAdapter extends RecyclerView.Adapter<VenueMenusAdapter.ViewHolder> {
    private static final Context CONTEXT = AppController.context;
    List<Menu> menuList;

    private OnCardClickListener onCardClickListener;


    public void setOnItemClickLister(OnCardClickListener onCardClickListener) {
        this.onCardClickListener = onCardClickListener;
    }

    public VenueMenusAdapter(List<Menu> menuList) {
        super();
        //Getting all the cities
        this.menuList = menuList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_menu_basic, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Menu menu = menuList.get(position);
        holder.txtMenuItemName.setText(menu.name);
        holder.txtMenuItemPrice.setText(CONTEXT.getString(R.string.item_price).replace("{{itemPrice}}", toPersianNumber(formatCurrency(menu.price))));
        if (onCardClickListener != null)
            holder.rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                AppController.currentActivity.startActivity(intent);
                    onCardClickListener.onClick(menu);
                }
            });
    }

    @Override
    public int getItemCount() {
        return menuList.size();
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
