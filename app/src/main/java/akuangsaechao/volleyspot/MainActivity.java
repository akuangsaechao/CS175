package akuangsaechao.volleyspot;

import android.support.v4.app.Fragment;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import akuangsaechao.volleyspot.fragments.Add;
import akuangsaechao.volleyspot.fragments.List;
import akuangsaechao.volleyspot.fragments.Map;
import akuangsaechao.volleyspot.fragments.Profile;


public class MainActivity extends AppCompatActivity implements Add.OnFragmentInteractionListener, List.OnFragmentInteractionListener, Map.OnFragmentInteractionListener, Profile.OnFragmentInteractionListener, VolleySpotListFragment.OnHeadlineSelectedListener {

    boolean initialize = false;
    boolean firstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!initialize)
            getAllVolleySpots();

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.action_list:
                                selectedFragment = List.newInstance();
                                break;
                            case R.id.action_map:
                                selectedFragment = Map.newInstance();
                                break;
                            case R.id.action_add:
                                selectedFragment = Add.newInstance();
                                break;
                            case R.id.action_profile:
                                selectedFragment = Profile.newInstance();
                                break;

                        }
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, selectedFragment);
                        transaction.commit();
                        return true;
                    }
                });

        if (firstTime) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, List.newInstance());
            transaction.commit();
            firstTime = false;
        }

    }

    public static HashMap<Integer, Item> volleySpotList = new HashMap<>();

    public void getAllVolleySpots() {
        String URL = "content://akuangsaechao.myapplication.myprovider/volleyspots";
        Uri devices = Uri.parse(URL);
        Cursor c = managedQuery(devices, null, null, null, "title");

        if (c.moveToFirst()) {
            do {
                String ID = c.getString(c.getColumnIndex(MyContentProvider._ID));
                String title = c.getString(c.getColumnIndex(MyContentProvider.TITLE));
                String address = c.getString(c.getColumnIndex(MyContentProvider.ADDRESS));
                byte[] image = c.getBlob(c.getColumnIndex(MyContentProvider.IMAGE));
                Bitmap bitmap = null;
                if (image != null)
                    bitmap = DbBitmapUtility.getImage(image);

                String[] addressBreakDown = address.split(" ");

                String zipCode = addressBreakDown[addressBreakDown.length - 1];

                Item item = new Item();

                item.id = ID;
                item.title = title;
                item.address = address;
                item.image = bitmap;

                volleySpotList.put(Integer.parseInt(ID), item);

                MainActivity.GeoCoderFromAddress geoCoderFromAddress = new MainActivity.GeoCoderFromAddress(address, Integer.parseInt(ID));
                //GeoCoderFromAddress geoCoderFromAddress = new GeoCoderFromAddress("427 S. 5th Street, San Jose, CA 95112");
                geoCoderFromAddress.execute();

                MainActivity.WeatherAPI weatherAPI = new MainActivity.WeatherAPI(zipCode, Integer.parseInt(ID));
                //WeatherAPI weatherAPI = new WeatherAPI("95112");
                weatherAPI.execute();

            } while (c.moveToNext());
        }

        initialize = true;

    }

    public class GeoCoderFromAddress extends AsyncTask<Void, Void, StringBuilder> {

        String place;
        int id;

        public GeoCoderFromAddress(String place, int id) {
            super();
            this.place = place;
            this.id = id;
        }

        @Override
        protected StringBuilder doInBackground(Void... params) {
            try {
                HttpURLConnection conn = null;
                StringBuilder jsonResults = new StringBuilder();
                //String googleMapUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&key=AIzaSyDvcHktVxizrhdJ-TS1txRMOgGuxO0hPOI";
                String after = place.trim().replaceAll(" ", "+");
                String googleMapUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=" + after + "&key=AIzaSyDvcHktVxizrhdJ-TS1txRMOgGuxO0hPOI";
                URL url = new URL(googleMapUrl);
                conn = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(conn.getInputStream());
                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    jsonResults.append(buff, 0, read);
                }
                return jsonResults;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(StringBuilder result) {
            super.onPostExecute(result);
            try {

                JSONObject jsonObj = new JSONObject(result.toString());
                JSONArray resultJsonArray = jsonObj.getJSONArray("results");
                JSONObject before_geometry_jsonObj = resultJsonArray.getJSONObject(0);
                JSONObject geometry_jsonObj = before_geometry_jsonObj.getJSONObject("geometry");
                JSONObject location_jsonObj = geometry_jsonObj.getJSONObject("location");

                String lat_helper = location_jsonObj.getString("lat");
                String lng_helper = location_jsonObj.getString("lng");

                double lat = Double.valueOf(lat_helper);
                double lng = Double.valueOf(lng_helper);

                putGeoCode(lat, lng, id);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class WeatherAPI extends AsyncTask<Void, Void, StringBuilder> {

        String place;
        int id;

        public WeatherAPI(String place, int id) {
            super();
            this.place = place;
            this.id = id;
        }

        @Override
        protected StringBuilder doInBackground(Void... params) {
            try {
                HttpURLConnection conn = null;
                StringBuilder jsonResults = new StringBuilder();
                String googleMapUrl = "https://api.openweathermap.org/data/2.5/weather?zip=" + place + ",us&units=imperial&APPID=7424bb17c1738362907a47e05e8686ee";
                URL url = new URL(googleMapUrl);
                conn = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(conn.getInputStream());
                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    jsonResults.append(buff, 0, read);
                }
                return jsonResults;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(StringBuilder result) {
            super.onPostExecute(result);
            try {

                JSONObject jsonObj = new JSONObject(result.toString());
                JSONObject location_jsonObj = jsonObj.getJSONObject("main");
                String temperature = location_jsonObj.getString("temp");

                putWeather(temperature, id);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void putGeoCode(double lat, double lng, int id) {

        Item item = volleySpotList.get(id);
        item.latitude = lat;
        item.longitude = lng;
        volleySpotList.put(id, item);

    }

    public void putWeather(String temperature, int id) {

        Item item = volleySpotList.get(id);
        item.temperature = temperature;
        volleySpotList.put(id, item);

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onArticleSelected(int position) {
        List newFragment = List.newInstance(position);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
