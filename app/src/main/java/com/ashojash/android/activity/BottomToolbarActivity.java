package com.ashojash.android.activity;

/*
* Checked for bus and json
* */
public class BottomToolbarActivity extends ToolbarActivity {




    /*private void setupBottomToolbar() {
//        toolbarBottom = (Toolbar) findViewById(R.id.toolbarBottomMainActivity);
//        toolbarBottom.inflateMenu(R.menu.menu_bottom);
//        LinearLayout searchLayout = (LinearLayout) findViewById(R.id.toolbarBottomSearch);
        final LinearLayout profileLayout = (LinearLayout) findViewById(R.id.toolbarBottomProfile);
        LinearLayout listLayout = (LinearLayout) findViewById(R.id.toolbarBottomList);
        if (!AuthUtils.isUserLoggedIn()) {

            profileLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (AppController.currentActivity.getClass().getSimpleName().equals(GuestProfileActivity.class.getSimpleName()))
                        return;
                    Intent intent = new Intent(AppController.currentActivity, GuestProfileActivity.class);
                    AppController.currentActivity.startActivity(intent);
                    finish();
                }
            });
        } else {
            profileLayout.setVisibility(View.GONE);
        }

        listLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppController.currentActivity.getClass().getSimpleName().equals(MainActivity.class.getSimpleName()))
                    return;
                Intent intent = new Intent(AppController.currentActivity, MainActivity.class);
                AppController.currentActivity.startActivity(intent);
                finish();
            }
        });
    }*/

    /*protected void setupBottomToolbarLayoutActive(int which) {
//        ImageView imgSearch = (ImageView) findViewById(R.id.imgToolbarBottomSearch);
//        imgSearch.setImageDrawable(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_search).color(getResources().getVenue(R.color.text_secondary)));
        ImageView imgProfile = (ImageView) findViewById(R.id.imgToolbarBottomProfile);
        imgProfile.setImageDrawable(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_account_circle).color(getResources().getColor(R.color.text_secondary)));

        ImageView imgList = (ImageView) findViewById(R.id.imgToolbarBottomList);
        imgList.setImageDrawable(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_view_list).color(getResources().getColor(R.color.text_secondary)));
        TextView txtList = (TextView) findViewById(R.id.txtToolbarBottomList);
        TextView txtProfile = (TextView) findViewById(R.id.txtToolbarBottomProfile);
        if (AuthUtils.isUserLoggedIn()) txtProfile.setText(R.string.action_me);
        else txtProfile.setText(R.string.action_profile);
        switch (which) {
            case 0:
                imgList.setColorFilter(Color.BLUE);
                txtList.setTextColor(Color.BLUE);
                break;
            case 2:
                imgProfile.setColorFilter(Color.BLUE);
                txtProfile.setTextColor(Color.BLUE);
        }
    }*/
}
