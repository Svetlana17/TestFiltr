package com.example.user.testfiltr;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;

public class GiroscopeActivity extends AppCompatActivity implements SensorEventListener {

        //  private static final String tag = LowPassFilter.class.getSimpleName();


        private float[] output;
    private final int averageAmount = 2;
        Button buttonS;
        Button button;
        private SensorManager mSensorManager;
        Sensor sensorGiroscope;
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
        private Thread thread;
        private boolean plotData = true;
        float xf;
        float x;
        float yy;
        float zz;
    private float gyroX = 0.0f;
    private float accelX = 0.0f;
        protected float timeConstant;
        private float altha = 0.8f;
        private boolean state;
        private int timer = 0;
        Button mbutton;

        protected long startTime;
        protected long timestamp;
        protected int count;
     private final float complementaryRatio = 0.9f;//0.25 -> Gyro*0.25, Accel*0.75
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            mbutton=(Button)findViewById(R.id.button);
            button = findViewById(R.id.button);
            state = false;
            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            sensorGiroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

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




        public void addEntry(SensorEvent event) {
            float[] values = event.values;
            float x = values[0];
            System.out.println(x);
            float y = values[1];
            System.out.println(y);
            float z = values[2];
            System.out.println(z);

            graph2LastXValue += 1d;
            graph2LastYValue += 1d;
            graph2LastZValue += 1d;


            // output[i] = output[i] + ALPHA * (input[i] - output[i]);
            //

          //  xx =xx+altha*(x-xx);
          //  xf=((complementaryRatio * (xf + values[0])));

            xf=complementaryRatio*x+(1-complementaryRatio)*accelX;

//            yy=yy+altha*(y-yy);
//            zz=zz+altha*(z-zz);
            //  yy = (float) (On_1 + altha * (y - On_1));
            //  zz = (float) (On_1 + altha * (z - On_1));

          //  series.appendData(new DataPoint(graph2LastYValue, y), true, 20);
             seriesX.appendData(new DataPoint(graph2LastXValue, x), true, 20);
            // seriesZ.appendData(new DataPoint(graph2LastZValue, z), true, 20);
            seriesXX.appendData(new DataPoint(graph2LastXValue, xf), true, 20);
           // seriesYY.appendData(new DataPoint(graph2LastYValue, yy), true, 20);
            // seriesZZ.appendData(new DataPoint(graph2LastZValue, zz), true, 20);
            //  graph.addSeries(seriesX);
            //  graph.addSeries(seriesZ);
            //  graph.addSeries(seriesXX);
            graph.addSeries(series);
            graph.addSeries(seriesYY);
            // graph.addSeries(seriesZZ);
        }



            private void feedMultiple () {

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

                thread.start();
            }

            @Override
            protected void onPause () {
                super.onPause();

                if (thread != null) {
                    thread.interrupt();
                }
                mSensorManager.unregisterListener((SensorEventListener) this);

            }

            //    @Override
            public void onSensorChanged ( final SensorEvent event){
                if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE)
                    //accelVals = lowPass( event.values.clone(), accelVals );
                    if (plotData) {
//            addEntry(event);
                        //
                        new Thread(new Runnable() {

                            @Override
                            public void run() {

                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        addEntry(event);
                                    }
                                });


                            }

                        }).start();

                        //
                        plotData = false;
                    }
            }

            //    @Override
            public void onAccuracyChanged (Sensor sensor,int accuracy){

            }

            @Override
            protected void onResume () {
                super.onResume();
                mSensorManager.registerListener((SensorEventListener) this, sensorGiroscope, SensorManager.SENSOR_DELAY_NORMAL);
            }

            @Override
            protected void onDestroy () {
                mSensorManager.unregisterListener((SensorEventListener) GiroscopeActivity.this);
                thread.interrupt();
                super.onDestroy();
            }


            public void onClick (View v){
                Intent intent = new Intent(this, GiroscopeActivity.class);
                startActivity(intent);
                finish();
            }
        }