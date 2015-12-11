package com.ashojash.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.ashojash.android.R;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.struct.StructCity;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.List;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {
    private ImageLoader imageLoader;
    private Context context;
    List<StructCity> structCityList;
    private Intent intent;

    public CityAdapter(List<StructCity> structCityList, Context context, Intent intent) {
        super();
        //Getting all the cities
        this.structCityList = structCityList;
        this.context = context;
        this.intent = intent;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_city, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final StructCity structCity = structCityList.get(position);
        imageLoader = AppController.getInstance().getImageLoader();
        imageLoader.get(structCity.getImageUrl(), ImageLoader.getImageListener(holder.imageView, R.drawable.city_list_loader, android.R.drawable.ic_dialog_alert));
        holder.imageView.setImageUrl(structCity.getImageUrl(), imageLoader);
        holder.textCityName.setText(structCity.getName());
        holder.imgIconic.setImageDrawable(new IconicsDrawable(context).icon(GoogleMaterial.Icon.gmd_keyboard_arrow_left).sizeDp(12).color(AppController.context.getResources().getColor(R.color.text_primary)));
        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                save to preferences
                AppController.editor.putString("current_city_slug", structCity.getSlug());
                AppController.editor.commit();
                AppController.currentActivity.startActivity(intent);
                AppController.currentActivity.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return structCityList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public NetworkImageView imageView;
        public TextView textCityName;
        public ImageView imgIconic;
        public ViewGroup rootLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (NetworkImageView) itemView.findViewById(R.id.imgCityListCityImg);
            textCityName = (TextView) itemView.findViewById(R.id.txtCityListCityName);
            imgIconic = (ImageView) itemView.findViewById(R.id.imgIconic);
            rootLayout = (ViewGroup) itemView.findViewById(R.id.cardCity);
        }
    }
}
