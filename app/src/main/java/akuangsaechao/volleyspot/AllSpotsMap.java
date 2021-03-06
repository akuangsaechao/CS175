package akuangsaechao.volleyspot;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class AllSpotsMap extends AppCompatActivity implements OnMapReadyCallback, SensorEventListener {

    private GoogleMap mMap;
    private int mapLocation = -1;
    private SensorManager mSensorManager;
    private Sensor mTemperature;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_spots_map);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mapLocation = extras.getInt("MapLocation");
        } else {
            mapLocation = -1;
        }

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mTemperature = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mTemperature, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        float ambient_temperature = event.values[0];
        Toast.makeText(AllSpotsMap.this, "Ambient Temperature:\n " + String.valueOf(ambient_temperature), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
