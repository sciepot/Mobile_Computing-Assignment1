package com.slava.localization;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    Database db;
    float pos_x;
    float pos_y;
    static boolean WAR_DRIVING_MODE = false;
    Button upload_button;
    Button delete_button;
    Button wardriving_button;
    Button localization_button;
    ImageView map_image;
    Bitmap map_bitmap;
    Paint red_dot;
    Paint blue_dot;
    // Scan permission
    Button yes_scan_button;
    Button no_scan_button;
    TextView scan_perm_text;
    ImageView popup_image;
    Intent scanned_ap;
    // constant to compare
    // the activity result code
    int SELECT_PICTURE = 200;
    int SCAN_AP = 300;
    int FIND_LOCATION = 666;
    void popup (boolean flag)
    {
        int num = flag ? View.VISIBLE : View.GONE;
        yes_scan_button.setVisibility(num);
        no_scan_button.setVisibility(num);
        scan_perm_text.setVisibility(num);
        popup_image.setVisibility(num);
        delete_button.setEnabled(!flag);
        wardriving_button.setEnabled(!flag);
        localization_button.setEnabled(!flag);
        map_image.setEnabled(!flag);
    }


@Override
    protected void onCreate(Bundle savedInstanceState) {
        db = new Database();

        scanned_ap = new Intent(MainActivity.this, ScannedAP.class);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        upload_button = findViewById(R.id.upload_button);
        map_image = findViewById(R.id.map_image);
        delete_button = findViewById(R.id.delete_button);
        wardriving_button = findViewById(R.id.wardriving_button);
        localization_button = findViewById(R.id.localization_button);
        yes_scan_button = findViewById(R.id.yes_button_scan);
        no_scan_button = findViewById(R.id.no_button_scan);
        scan_perm_text = findViewById(R.id.ask_perm);
        popup_image = findViewById(R.id.ask_popup);

        red_dot = new Paint();
        red_dot.setColor(Color.RED);
        red_dot.setStrokeWidth(5);

        blue_dot = new Paint();
        blue_dot.setColor(Color.BLUE);
        blue_dot.setStrokeWidth(5);

        delete_button.setVisibility(View.GONE);
        wardriving_button.setVisibility(View.GONE);
        localization_button.setVisibility(View.GONE);
        yes_scan_button.setVisibility(View.GONE);
        no_scan_button.setVisibility(View.GONE);
        scan_perm_text.setVisibility(View.GONE);
        popup_image.setVisibility(View.GONE);
        map_image.setEnabled(false);

        upload_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                imageChooser();
                delete_button.setVisibility(View.VISIBLE);
                wardriving_button.setVisibility(View.VISIBLE);
                upload_button.setVisibility(View.GONE);
            }
        });

        delete_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public  void onClick(View v) {
                map_image.setImageDrawable(null);
                upload_button.setVisibility(View.VISIBLE);
                delete_button.setVisibility(View.GONE);
                wardriving_button.setVisibility(View.GONE);
                localization_button.setVisibility(View.GONE);
            }
        });

        wardriving_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (WAR_DRIVING_MODE) {
                    wardriving_button.setTextColor(Color.WHITE);
                    map_image.setEnabled(false);
                    WAR_DRIVING_MODE = false;
                }
                else {
                    wardriving_button.setTextColor(Color.GRAY);
                    map_image.setEnabled(true);
                    WAR_DRIVING_MODE = true;
                }
            }
        });

        map_image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popup(true);
                pos_x = ((float) event.getX()) / ((float) map_image.getWidth()) * ((float) map_bitmap.getWidth());
                pos_y = ((float) event.getY()) / ((float) map_image.getHeight()) * ((float) map_bitmap.getHeight());
                return true;
            }
        });

        yes_scan_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                popup(false);
                //Bitmap tempBitmap = Bitmap.createBitmap(map_bitmap.getWidth(), map_bitmap.getHeight(), Bitmap.Config.RGB_565);
                startActivityForResult(scanned_ap, SCAN_AP);
            }
        });

        no_scan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup(false);
            }
        });

        localization_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(scanned_ap, FIND_LOCATION);
            }
        });
    }

    void imageChooser() {

        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    try {
                        map_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                        map_image.setImageBitmap(map_bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (requestCode == SCAN_AP) {
                if (data.getBooleanExtra("result", false))
                {
                    Bitmap temp_bitmap = map_bitmap.copy(Bitmap.Config.ARGB_8888, true);
                    Canvas canvas = new Canvas(temp_bitmap);
                    canvas.drawPoint(pos_x, pos_y, red_dot);
                    map_bitmap = temp_bitmap.copy(Bitmap.Config.ARGB_8888, false);
                    map_image.setImageBitmap(temp_bitmap);
                    localization_button.setVisibility(View.VISIBLE);
                    if (data.getBooleanExtra("not_empty", false))
                    {
                        HashMap<String, Float> values = (HashMap<String, Float>) data.getSerializableExtra("data");
                        db.put(values, pos_x, pos_y);
                    }
                }
            } else if (requestCode == FIND_LOCATION) {
                if (data.getBooleanExtra("result", false))
                {
                    if (data.getBooleanExtra("not_empty", false))
                    {
                        HashMap<String, Float> values = (HashMap<String, Float>) data.getSerializableExtra("data");
                        Position target = new Position(0, 0, values);
                        db.find(target);
                        Bitmap temp_bitmap = map_bitmap.copy(Bitmap.Config.ARGB_8888, true);
                        Canvas canvas = new Canvas(temp_bitmap);
                        canvas.drawPoint(pos_x, pos_y, blue_dot);
                        map_image.setImageBitmap(temp_bitmap);
                        localization_button.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }



    @Override
    protected void onResume()
    {
        super.onResume();
        //registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        //unregisterReceiver(mReceiver);
    }
}