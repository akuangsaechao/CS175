package akuangsaechao.volleyspot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

public class AllSpotsMap extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_spots_map);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        makeMap();
    }

    public void makeMap(){

        for(int index : MainActivity.volleySpotList.keySet()){



            LatLng latLng = new LatLng(MainActivity.volleySpotList.get(index).latitude, MainActivity.volleySpotList.get(index).longitude);

            mMap.addMarker(new MarkerOptions().position(latLng).title(MainActivity.volleySpotList.get(index).title).snippet(MainActivity.volleySpotList.get(index).temperature));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(12).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


        }
    }

}
