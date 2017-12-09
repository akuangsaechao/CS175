package akuangsaechao.volleyspot;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import akuangsaechao.volleyspot.fragments.List;

public class SpotDetails extends AppCompatActivity implements SensorEventListener {

    public int mapLocation;

    ImageView image;
    TextView title, address, description, weather, temperature;
    Button button;
    private SensorManager mSensorManager;
    private Sensor mTemperature;
    String ambientTemp, titleString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_details);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mapLocation = extras.getInt("MapLocation");
        } else {
            mapLocation = -1;
        }

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mTemperature = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        image = findViewById(R.id.volleySpotImage);
        title = findViewById(R.id.volleySpotTitle);
        address = findViewById(R.id.volleySpotAddress);
        description = findViewById(R.id.volleySpotDescription);
        weather = findViewById(R.id.volleySpotWeather);
        temperature = findViewById(R.id.volleySpotAmbientTemp);
        button = findViewById(R.id.setVolleySpot);

        initialize();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyPreferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("SPOT", titleString);
                editor.commit();
            }
        });

    }

    public void initialize(){

        Item item = List.items.get(mapLocation);
        image.setImageResource(android.R.color.transparent);
        image.setBackgroundResource(0);
        image.setImageBitmap(item.image);
        title.setText(item.title);
        titleString = item.title;
        address.setText(item.address);
        description.setText(item.description);
        weather.setText(item.temperature + (char) 0x00B0);

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
        ambientTemp = Float.toString(ambient_temperature);
        temperature.setText(ambientTemp + (char) 0x00B0);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
