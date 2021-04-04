package com.example.galor_nadav_hw1;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.galor_nadav_hw1.compass.Compass;
import com.example.galor_nadav_hw1.compass.SOTWFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private EditText editEmail, editPass;
    private TextView loginText, brightness, sotwLabel, vibsCounter;;
    private Compass compass;
    private ImageView arrowView;
    private float currentAzimuth;
    private SOTWFormatter sotwFormatter;
    private Button logBTN,regBTN;
    private SensorManager brightnessSM;
    private SensorEventListener listener;
    private Sensor light;
    private RelativeLayout loginLayout;
    private Vibrator v;
    private ImageButton vibBTN;
    private int vibs_counter;

    //Firebase
    private FirebaseAuth auth;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        logBTN = findViewById( R.id.logBTN );
        regBTN = findViewById( R.id.regBTN );
        editEmail = findViewById( R.id.editUser );
        editPass = findViewById( R.id.editPassword );
        loginText = findViewById( R.id.logInText );
        loginLayout = findViewById( R.id.editTextLayout );

        //compass sensor
        sotwFormatter = new SOTWFormatter(this);
        arrowView =(ImageView) findViewById(R.id.main_image_hands);
        sotwLabel =(TextView) findViewById(R.id.sotw_label);
        setupCompass();

        //brightness sensor
        brightness = findViewById(R.id.auth2);
        brightnessSM = (SensorManager) getSystemService(SENSOR_SERVICE);
        light = brightnessSM.getDefaultSensor(Sensor.TYPE_LIGHT);
        loginLayout.setVisibility(View.VISIBLE);


        //vibration
        vibBTN = findViewById( R.id.vibBTN );
        vibsCounter = findViewById( R.id.auth3 );
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibs_counter = 0;
        vibBTN.setOnClickListener(view -> {
                // Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(500);

            }
            vibs_counter += 1;
            vibsCounter.setText("Vibration Counter: " + vibs_counter);
            if (vibs_counter > 4 && vibs_counter < 6) editEmail.setVisibility(View.VISIBLE);
            else editEmail.setVisibility(View.INVISIBLE);
        });


        listener = new SensorEventListener() {

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }

            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                    String valueStr = String.valueOf(event.values[0]);
                    brightness.setText("Brightness: " + valueStr);
                    int grayShade = (int) event.values[0];
                    if (grayShade > 255) grayShade = 255;
//                if(Float.parseFloat(valueStr) >40.0 && Float.parseFloat(valueStr)  < 50.0) brightnessBOL = true;
                    if (Float.parseFloat(valueStr) < 50.0) loginLayout.setVisibility(View.VISIBLE);
                    else loginLayout.setVisibility(View.INVISIBLE);

                    brightness.setTextColor(Color.rgb(255 - grayShade, 255 - grayShade, 255 - grayShade));
                    brightness.setBackgroundColor(Color.rgb(grayShade, grayShade, grayShade));
                }
            }
        };

        brightnessSM.registerListener(listener, light, SensorManager.SENSOR_DELAY_FASTEST);

        loginText.setText( "Login 4 way authentication");


        //Firebase
        auth = FirebaseAuth.getInstance();

        logBTN.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                loginFunc(v);
            }
        } );
        regBTN.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                regFunc(v);
            }
        } );
    }

    public void loginFunc(View view) {
            auth.signInWithEmailAndPassword(editEmail.getText().toString(), editPass.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(MainActivity.this, "login ok.",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, ProfileView.class);
                                startActivity(intent);
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(MainActivity.this, "login failed.",
                                        Toast.LENGTH_SHORT).show();

                            }

                            // ...
                        }
                    });

    }

    public void regFunc(View view){
        if (editEmail.getText().toString().isEmpty() || editPass.getText().toString().isEmpty())
            Toast.makeText( MainActivity.this,"Please fill the lines.",
                    Toast.LENGTH_SHORT).show();

        else
            auth.createUserWithEmailAndPassword(editEmail.getText().toString(),editPass.getText().toString())
                    .addOnCompleteListener(this, task -> {
                        if(task.isSuccessful()){
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText( MainActivity.this,"REG ok.",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent( MainActivity.this, ProfileView.class );
                            startActivity( intent );
                        }else{
                            // If sign in fails, display a message to the user.
                            Toast.makeText( MainActivity.this,"REG failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
    }
    @Override
    protected void onStart() {
        super.onStart();
        compass.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        compass.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        compass.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        compass.stop();
    }

    private void setupCompass() {
        compass = new Compass(this);
        Compass.CompassListener cl = getCompassListener();
        compass.setListener(cl);
    }

    private void adjustArrow(float azimuth) {
        Animation an = new RotateAnimation(-currentAzimuth, -azimuth,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        currentAzimuth = azimuth;

        an.setDuration(500);
        an.setRepeatCount(0);
        an.setFillAfter(true);

        arrowView.startAnimation(an);
    }

    private void adjustSotwLabel(float azimuth) {
        sotwLabel.setText(sotwFormatter.format(azimuth));
        if (azimuth>300 || azimuth < 50) logBTN.setVisibility(View.VISIBLE);
        else  logBTN.setVisibility(View.INVISIBLE);
    }


    private Compass.CompassListener getCompassListener() {
        return new Compass.CompassListener() {
            @Override
            public void onNewAzimuth(final float azimuth) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adjustArrow(azimuth);
                        adjustSotwLabel(azimuth);
                    }
                });
            }
        };
    }


}

