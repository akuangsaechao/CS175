package akuangsaechao.volleyspot;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
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

public class VolleySpotDetailFragment extends Fragment{

    final static String ARG_POSITION = "position";
    int mCurrentPosition = -1;
    GoogleMap googleMap;
    MapView mMapView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
        }

        View rootView = inflater.inflate(R.layout.fragment_volley_spot_detail, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
            }
        });

        GeoCoderFromAddress geoCoderFromAddress = new GeoCoderFromAddress("6417 Aliso Way, Sacramento, CA 95828");
        geoCoderFromAddress.execute();

        WeatherAPI weatherAPI = new WeatherAPI("95112");
        weatherAPI.execute();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle args = getArguments();
        if (args != null) {
            updateArticleView(args.getInt(ARG_POSITION));
        } else if (mCurrentPosition != -1) {
            updateArticleView(mCurrentPosition);
        }
    }

    public void updateArticleView(int position) {
        if (mCurrentPosition != position) {
            mCurrentPosition = position;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_POSITION, mCurrentPosition);
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

                LatLng point = new LatLng(lat, lng);

                googleMap.addMarker(new MarkerOptions().position(point).title("Marker Title").snippet("Marker Description"));
                CameraPosition cameraPosition = new CameraPosition.Builder().target(point).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

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

                LatLng sydney = new LatLng(-34.852, 151.211);
                googleMap.addMarker(new MarkerOptions().position(sydney).title(temperature));
                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
