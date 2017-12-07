package akuangsaechao.volleyspot;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
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



        LatLng sydney = new LatLng(-34, 151);

        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));
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
        protected void onCancelled() {
            super.onCancelled();
            this.cancel(true);
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
                String a = "";
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
                double lat = Double.valueOf(lat_helper);
                String lng_helper = location_jsonObj.getString("lng");
                double lng = Double.valueOf(lng_helper);
                point = new LatLng(lat, lng);

                mMap.addMarker(new MarkerOptions().position(point).title("Marker Title").snippet("Marker Description"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
