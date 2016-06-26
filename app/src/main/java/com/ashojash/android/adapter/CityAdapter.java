package com.ashojash.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.ashojash.android.R;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.City;
import com.ashojash.android.ui.UiUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.List;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {
    List<City> cityList;
    private static final Context context = AppController.context;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onClick(City city);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public CityAdapter(List<City> cityList) {
        super();
        this.cityList = cityList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_city, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final City city = cityList.get(position);
        Glide.with(context).load(UiUtils.setUrlWidth(city.photo.url, 80)).centerCrop().placeholder(R.drawable.city_list_loader).error(new IconicsDrawable(AppController.context).icon(GoogleMaterial.Icon.gmd_error).sizeDp(12).color(AppController.context.getResources().getColor(R.color.text_primary))).diskCacheStrategy(DiskCacheStrategy.RESULT).into(holder.imageView);
        holder.textCityName.setText(city.name);
        holder.imgIconic.setImageDrawable(new IconicsDrawable(context).icon(GoogleMaterial.Icon.gmd_keyboard_arrow_left).sizeDp(12).color(AppController.context.getResources().getColor(R.color.text_primary)));
        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null)
                    onItemClickListener.onClick(city);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textCityName;
        public ImageView imgIconic;
        public ViewGroup rootLayout;
        private int imageViewWidth;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imgCityListCityImg);
            textCityName = (TextView) itemView.findViewById(R.id.txtCityListCityName);
            imgIconic = (ImageView) itemView.findViewById(R.id.imgIconic);
            rootLayout = (ViewGroup) itemView.findViewById(R.id.cardCity);
            imageViewWidth = imageView.getWidth();
        }
    }
}
