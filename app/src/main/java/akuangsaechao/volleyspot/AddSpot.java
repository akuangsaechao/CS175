package akuangsaechao.volleyspot;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
//import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class AddSpot extends AppCompatActivity {

    private ImageView volleySpotPicture;
    private Button addVolleySpot;
    private EditText volleySpotTitle, volleySpotLocation;
    public final static int PICK_PHOTO_CODE = 1046;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_spot);

        volleySpotPicture = findViewById(R.id.volleySpotImage);
        volleySpotTitle = findViewById(R.id.volleySpotTitle);
        volleySpotLocation = findViewById(R.id.volleySpotLocation);
        addVolleySpot = findViewById(R.id.addVolleySpot);

        volleySpotPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto(v);
            }
        });

        addVolleySpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = volleySpotTitle.getText().toString();
                String location = volleySpotLocation.getText().toString();

                addVolleySpot(title, location);

                Intent intent = new Intent(AddSpot.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

    }

    public void addVolleySpot(String title, String location){

        ContentValues contentValues = new ContentValues();

        contentValues.put(MyContentProvider.TITLE, title);
        contentValues.put(MyContentProvider.ADDRESS, location);

        Uri uri = getContentResolver().insert(MyContentProvider.URI, contentValues);

        Toast.makeText(AddSpot.this, uri.toString(), Toast.LENGTH_LONG).show();

    }

    public void onPickPhoto(View view) {
       // Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //if (intent.resolveActivity(getPackageManager()) != null) {
        //    startActivityForResult(intent, PICK_PHOTO_CODE);
        //}
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            try {
                Uri photoUri = data.getData();
                //Bitmap selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                //volleySpotPicture.setImageBitmap(selectedImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
