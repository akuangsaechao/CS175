package akuangsaechao.volleyspot;

import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AllSpotsMap extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mMap;

    Double latitude, longitute;

    LatLng point;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_spots_map);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        GeoCoderFromAddress geoCoderFromAddress = new GeoCoderFromAddress("427 S. 5th Street, San Jose, CA 95112");
        geoCoderFromAddress.execute();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void getAllVolleySpots() {
        String URL = "content://akuangsaechao.myapplication.myprovider/volleyspots";
        Uri devices = Uri.parse(URL);
        Cursor c = managedQuery(devices, null, null, null, "name");

        if (c.moveToFirst()) {
            do {
                String ID = c.getString(c.getColumnIndex(MyContentProvider._ID));
                String title = c.getString(c.getColumnIndex(MyContentProvider.TITLE));
                String address = c.getString(c.getColumnIndex(MyContentProvider.ADDRESS));

            } while (c.moveToNext());
        }
    }

    public void initializeMap() {

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

                point = new LatLng(lat, lng);

                mMap.addMarker(new MarkerOptions().position(point).title("Marker Title").snippet("Marker Description"));
                CameraPosition cameraPosition = new CameraPosition.Builder().target(point).zoom(12).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

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

                mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
                mTemperature= mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

                

                LatLng sydney = new LatLng(-34.852, 151.211);
                mMap.addMarker(new MarkerOptions().position(sydney).title(temperature));
                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private SensorManager mSensorManager;
    private Sensor mTemperature;

}
