package com.ashojash.android.webserver;

import com.ashojash.android.db.VenueDb;
import com.ashojash.android.struct.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonParser {
    public static List<StructCity> parseCityJsonObject(JSONObject object) throws JSONException {
        List<StructCity> cityList = new ArrayList<>();
        JSONArray jsonArray = object.getJSONArray("data");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            StructCity structCity = new StructCity();
            structCity.setName(jsonObject.getString("name"));
            structCity.setName(jsonObject.getString("name"));
            structCity.setImageUrl(jsonObject.getString("image_url"));
            structCity.setSlug(jsonObject.getString("slug"));
            cityList.add(structCity);
        }
        return cityList;
    }

    public static List<StructVenue> parseVenuesJsonObject(JSONObject object) throws JSONException {
        List<StructVenue> venueList = new ArrayList<>();
        JSONArray jsonArray = object.getJSONArray("data");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            venueList.add(parseVenueJsonObject(jsonObject));
        }
        return venueList;
    }


    public static StructVenue parseVenueJsonObject(JSONObject jsonObject) throws JSONException {
        StructVenue structVenue = new StructVenue();
        structVenue.setName(jsonObject.getString("name"));
        structVenue.setScore((float) jsonObject.getDouble("score"));
        structVenue.setCost(jsonObject.getInt("cost"));
        structVenue.setInstagram(jsonObject.getString("instagram"));
        structVenue.setPhone(jsonObject.getString("phone"));
        structVenue.setMobile(jsonObject.getString("mobile"));
        structVenue.setAddress(jsonObject.getString("address"));
        structVenue.setLat(jsonObject.getDouble("lat"));
        structVenue.setLng(jsonObject.getDouble("lng"));
        structVenue.setImageUrl(jsonObject.getString("image_url"));
        structVenue.setSlug(jsonObject.getString("slug"));
        structVenue.setUrl(jsonObject.getString("url"));
        VenueDb.createOrUpdate(structVenue);
        return structVenue;
    }

    public static StructUser parseUserJsonObject(JSONObject user) {
        StructUser object = new StructUser();
        try {
            object.setEmail(user.getString("email"));
            object.setName(user.getString("name"));
            object.setUsername(user.getString("username"));
            object.setImageUrl(user.getString("image_url"));
        } catch (JSONException e) {
            return null;
        }
        return object;
    }

    public static List<StructReview> parseVenueReviews(JSONArray response) throws JSONException {
        List<StructReview> venueReviews = new ArrayList<>();
        for (int i = 0; i < response.length(); i++)
            venueReviews.add(parseVenueReview(response.getJSONObject(i)));
        return venueReviews;
    }

    public static StructReview parseVenueReview(JSONObject object) throws JSONException {
        StructReview structReview = new StructReview();
        structReview.setComment(object.getString("comment"));
        structReview.setCost(object.getInt("cost"));
        structReview.setQuality(object.getInt("quality"));
        structReview.setDecor(object.getInt("decor"));
        structReview.setUserImageUrl(object.getString("user_image_url"));
        return structReview;
    }

    public static boolean parseUserRegistrationIsGoogleEmail(JSONObject data) throws JSONException {
        return data.getBoolean("is_google_user");
    }

    private static String[] convertJsonArrayToStringArray(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.length() == 0) return null;
        String[] errors = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++)
            errors[i] = jsonArray.optString(i);
        return errors;
    }

    public static String convertJsonArrayToString(JSONArray jsonArray) {
        String strings[] = convertJsonArrayToStringArray(jsonArray);
        if (strings == null) return null;
        StringBuilder builder = new StringBuilder();
        for (String s : strings) {
            builder.append(s).append("\n");
        }
        return builder.toString();
    }

    public static List<StructMenu> parseVenueMenus(JSONArray response) throws JSONException {
        List<StructMenu> venueReviews = new ArrayList<>();
        for (int i = 0; i < response.length(); i++)
            venueReviews.add(parseVenueMenu(response.getJSONObject(i)));
        return venueReviews;
    }

    public static StructMenu parseVenueMenu(JSONObject object) throws JSONException {
        StructMenu structReview = new StructMenu();
        structReview.setIngredients(object.getString("ingredients"));
        structReview.setName(object.getString("name"));
        structReview.setPrice(object.getInt("price"));
        return structReview;
    }

    public static List<StructPhoto> parseVenuePhotos(JSONArray response) throws JSONException {
        ArrayList<StructPhoto> photos = new ArrayList<>();
        for (int i = 0; i < response.length(); i++)
            photos.add(parseVenuePhoto(response.getJSONObject(i)));
        return photos;
    }

    public static StructPhoto parseVenuePhoto(JSONObject object) throws JSONException {
        StructPhoto photo = new StructPhoto();
        photo.setPhotoUrl(object.getString("image_url"));
        photo.setUsername(object.getString("username"));
        return photo;
    }
}
