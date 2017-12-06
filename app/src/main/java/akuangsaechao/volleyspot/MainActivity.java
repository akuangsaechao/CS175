package akuangsaechao.volleyspot;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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


    }
}
