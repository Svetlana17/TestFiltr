package com.example.user.testfiltr;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;

public class FiltrGiroscope extends AppCompatActivity implements Runnable {
    private final boolean complementaryFilter = true;

    private static volatile float angle;
    private Thread thread;
    private boolean plotData = true;

    private final float errorOffset = 2.0f;//1.9875f;//corrects bias in gyro direction - 1.9875f -> specific to sensor
    private final int averageAmount = 2;
    private float gyroX = 0.0f;
    private float accelX = 0.0f;
    private float previousAngle = 0.0f;
    private float previousAccelX = 0.0f;
    private float previousGyroX = 0.0f;
    private final float complementaryRatio = 0.7f;//0.25 -> Gyro*0.25, Accel*0.75
    static Thread t;
    private SensorManager mSensorManager;
    Sensor sensprGiroscope;
    Sensor sensorAcc;
    GraphView graph;
    private double graph2LastXValue = 5d;
    private double graph2LastYValue = 5d;
    private double graph2LastZValue = 5d;
    private Double[] dataPoints;
    LineGraphSeries<DataPoint> series;
    LineGraphSeries<DataPoint> seriesX;
    LineGraphSeries<DataPoint> seriesZ;
    LineGraphSeries<DataPoint> seriesXX;
    LineGraphSeries<DataPoint> seriesYY;
    LineGraphSeries<DataPoint> seriesZZ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensprGiroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorAcc=mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    graph = (GraphView) findViewById(R.id.graph);
    series = new LineGraphSeries<DataPoint>(new DataPoint[]{
        new DataPoint(0, 0),
    });
        series.setColor(Color.GREEN);
        graph.addSeries(series);
    seriesX = new LineGraphSeries<DataPoint>(new DataPoint[]{
        new DataPoint(0, 0),
    });
        seriesX.setColor(Color.BLACK);

    seriesZ = new LineGraphSeries<DataPoint>(new DataPoint[]{
        new DataPoint(0, 0),
    });
        seriesZ.setColor(Color.RED);
    seriesXX = new LineGraphSeries<DataPoint>(new DataPoint[]{
        new DataPoint(0, 0),

    });
        seriesXX.setColor(Color.YELLOW);
//
    seriesZZ = new LineGraphSeries<DataPoint>(new DataPoint[]{
        new DataPoint(0, 0),
    });
        seriesZZ.setColor(Color.LTGRAY);

//
    seriesYY = new LineGraphSeries<DataPoint>(new DataPoint[]{
        new DataPoint(0, 0),
    });
        seriesYY.setColor(Color.MAGENTA);

        graph.addSeries(seriesX);
        graph.addSeries(series);
        graph.addSeries(seriesZ);
        graph.addSeries(seriesXX);
        graph.addSeries(seriesYY);
        graph.addSeries(seriesZZ);
        graph.getViewport().setXAxisBoundsManual(true);

        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(20);
    feedMultiple();

}
    private void feedMultiple() {

        if (thread != null) {
            thread.interrupt();
        }

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    plotData = true;
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    @Override
    public void run() {
        while(true){
            for (int i = 0; i < averageAmount; i++) {
                accelX += accelX;
                gyroX += gyroX;
            }

            accelX /= averageAmount;
            gyroX /= averageAmount;


            //ComplementaryFilter
            if (complementaryFilter) {
                //angle = ((complementaryRatio * (angle + gyroX) + (0.02f - complementaryRatio) * accelX));//complementary filter


               // angle = ((complementaryRatio * (angle + gyroX) + (0.3f * accelX)));//complementary filter

                angle=complementaryRatio*gyroX+(1-complementaryRatio)*accelX;
            } else {
                angle = accelX;
            }

            previousAngle = angle;
            previousGyroX = gyroX;
            previousAccelX = accelX;
            accelX = 0;
            gyroX = 0;
        }}
 //  }



    private void addDataPoint(double acceleration) {
        dataPoints[499] = acceleration;
    }



    public void start(){
        if(t == null){
            t = new Thread(this, "MPU");
            t.start();
        }
    }

    public static float returnAngle(){
        return angle;
    }





}
//}
