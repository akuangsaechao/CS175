package akuangsaechao.volleyspot;

import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    Button volleyListButton, volleyMapButton, volleyAddButton, volleyProfileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        volleyListButton = findViewById(R.id.VolleyList);
        volleyMapButton = findViewById(R.id.VolleyMap);
        volleyAddButton = findViewById(R.id.VolleyAdd);
        volleyProfileButton = findViewById(R.id.VolleyProfile);

        volleyListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VolleySpots.class);
                startActivity(intent);
            }
        });

        volleyMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AllSpotsMap.class);
                startActivity(intent);
            }
        });

        volleyAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddSpot.class);
                startActivity(intent);
            }
        });

        volleyProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Profile.class);
                startActivity(intent);
            }
        });

        getAllVolleySpots();

    }

    public static int _id;
    public static HashMap<Integer, Item> volleySpotList = new HashMap<>();
    public static ArrayList<Item> itemArray = new ArrayList<>();

    public void getAllVolleySpots() {
        String URL = "content://akuangsaechao.myapplication.myprovider/volleyspots";
        Uri devices = Uri.parse(URL);
        Cursor c = managedQuery(devices, null, null, null, "title");

        if (c.moveToFirst()) {
            do {
                String ID = c.getString(c.getColumnIndex(MyContentProvider._ID));
                _id = Integer.parseInt(ID);
                String title = c.getString(c.getColumnIndex(MyContentProvider.TITLE));
                String address = c.getString(c.getColumnIndex(MyContentProvider.ADDRESS));
                byte[] image = c.getBlob(c.getColumnIndex(MyContentProvider.IMAGE));

                Bitmap bitmap = DbBitmapUtility.getImage(image);

                String[] addressBreakDown = address.split(" ");

                String zipCode = addressBreakDown[addressBreakDown.length - 1];

                Item item = new Item();

                item.id = ID;
                item.title = title;
                item.address = address;
                item.image = bitmap;

                volleySpotList.put(_id, item);

                MainActivity.GeoCoderFromAddress geoCoderFromAddress = new MainActivity.GeoCoderFromAddress(address);
                //GeoCoderFromAddress geoCoderFromAddress = new GeoCoderFromAddress("427 S. 5th Street, San Jose, CA 95112");
                geoCoderFromAddress.execute();

                MainActivity.WeatherAPI weatherAPI = new MainActivity.WeatherAPI(zipCode);
                //WeatherAPI weatherAPI = new WeatherAPI("95112");
                weatherAPI.execute();

            } while (c.moveToNext());
        }

        makeList();
    }

    public class GeoCoderFromAddress extends AsyncTask<Void, Void, StringBuilder> {

        String place;

        public GeoCoderFromAddress(String place) {
            super();
            this.place = place;
        }

        @Override
        protected StringBuilder doInBackground(Void... params) {
            try {
                HttpURLConnection conn = null;
                StringBuilder jsonResults = new StringBuilder();
                String googleMapUrl = "http://maps.googleapis.com/maps/api/geocode/json?address=" + this.place + "&sensor=false";
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

                putGeoCode(lat, lng);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class WeatherAPI extends AsyncTask<Void, Void, StringBuilder> {

        String place;

        public WeatherAPI(String place) {
            super();
            this.place = place;
        }

        @Override
        protected StringBuilder doInBackground(Void... params) {
            try {
                HttpURLConnection conn = null;
                StringBuilder jsonResults = new StringBuilder();
                String googleMapUrl = "http://api.openweathermap.org/data/2.5/weather?zip=" + place + ",us&units=imperial&APPID=7424bb17c1738362907a47e05e8686ee";
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

                putWeather(temperature);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void putGeoCode(double lat, double lng){

        Item item = volleySpotList.get(_id);
        item.latitude = lat;
        item.longitude = lng;
        volleySpotList.put(_id, item);

    }

    public void putWeather(String temperature){

        Item item = volleySpotList.get(_id);
        item.temperature = temperature;
        volleySpotList.put(_id, item);

    }

    public void makeList(){

        for (int index : volleySpotList.keySet()){

            itemArray.add(volleySpotList.get(index));

        }

    }

}
