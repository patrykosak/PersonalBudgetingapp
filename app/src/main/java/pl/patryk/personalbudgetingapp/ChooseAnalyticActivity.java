package pl.patryk.personalbudgetingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
//implements SensorEventListener
public class ChooseAnalyticActivity extends AppCompatActivity {
    private CardView todayCardView, weekCardView, monthCardView;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private boolean isAccelerometerSensorAvailable, firstTime=true;
    //private SensorEventListener sensorEventListener;
    private float acelVal, acelLast, shake;
    private TextView saveInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_analytic);

        todayCardView = findViewById(R.id.todayCardView);
        weekCardView = findViewById(R.id.weekCardView);
        monthCardView = findViewById(R.id.monthCardView);

        saveInfo = findViewById(R.id.saveInfo);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(sensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);

        acelVal = SensorManager.GRAVITY_EARTH;
        acelLast = SensorManager.GRAVITY_EARTH;
        shake = 0.00f;

       if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!=null){

           isAccelerometerSensorAvailable = true;
     }
        else{
          saveInfo.setVisibility(View.GONE);
      }

        todayCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseAnalyticActivity.this, DailyAnalyticsActivity.class);
                startActivity(intent);
            }
        });

        weekCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseAnalyticActivity.this, WeeklyAnalyticsActivity.class);
                startActivity(intent);
            }
        });

        monthCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseAnalyticActivity.this, MonthlyAnalyticsActivity.class);
                startActivity(intent);
            }
        });
    }

    private final SensorEventListener sensorEventListener = new SensorEventListener(){
        @Override
        public void onSensorChanged(SensorEvent event) {

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            acelLast = acelVal;
            acelVal = (float) Math.sqrt((double) (x*x+y*y+z*z));
            float delta = acelVal - acelLast;
            shake = shake*0.9f + delta;
            if(shake > 0.01){
                if(firstTime) {
                    Intent intent = new Intent(ChooseAnalyticActivity.this, VideoActivity.class);
                    startActivity(intent);
                    firstTime=false;
                }
            }


        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

    };




    @Override
    protected void onResume(){
        super.onResume();

        if(isAccelerometerSensorAvailable){
            sensorManager.registerListener(sensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isAccelerometerSensorAvailable){
            sensorManager.unregisterListener(sensorEventListener);
        }
    }
}