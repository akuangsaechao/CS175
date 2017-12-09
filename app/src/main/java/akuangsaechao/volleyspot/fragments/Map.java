package akuangsaechao.volleyspot.fragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import akuangsaechao.volleyspot.MainActivity;
import akuangsaechao.volleyspot.R;
import akuangsaechao.volleyspot.VolleySpots;

public class Map extends Fragment implements OnMapReadyCallback, SensorEventListener {

    private OnFragmentInteractionListener mListener;
    private GoogleMap mMap;
    private int mapLocation = -1;
    private SensorManager mSensorManager;
    private Sensor mTemperature;
    MapView mapView;
    public Map() {
    }

    public static Map newInstance() {
        Map fragment = new Map();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            mapLocation = extras.getInt("MapLocation");
        } else {
            mapLocation = -1;
        }

        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mTemperature = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        mapView = view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {

        }
        makeMap();
    }

    public void makeMap() {

        if (mapLocation == -1) {

            for (int index : MainActivity.volleySpotList.keySet()) {
                LatLng latLng = new LatLng(MainActivity.volleySpotList.get(index).latitude, MainActivity.volleySpotList.get(index).longitude);
                mMap.addMarker(new MarkerOptions().position(latLng).title(MainActivity.volleySpotList.get(index).title).snippet(MainActivity.volleySpotList.get(index).temperature));
            }

            LatLng latLng = new LatLng(37.33333, -121.9);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(12).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        } else {
            LatLng latLng = new LatLng(VolleySpots.items.get(mapLocation).latitude, VolleySpots.items.get(mapLocation).longitude);
            mMap.addMarker(new MarkerOptions().position(latLng).title(VolleySpots.items.get(mapLocation).title).snippet(VolleySpots.items.get(mapLocation).temperature));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(12).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mTemperature, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        float ambient_temperature = event.values[0];
        //Toast.makeText(getActivity(), "Ambient Temperature:\n " + String.valueOf(ambient_temperature), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
