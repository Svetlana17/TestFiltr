package com.example.user.testfiltr;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Timer;
import java.util.TimerTask;

public class GiroscopeActivFiltr extends AppCompatActivity   implements SensorEventListener {




        private float[] output;

        private float[] rotationMatrix = new float[9];

//    public static final float EPSILON = 0.000000001f;
//    private static final float NS2S = 1.0f / 1000000000.0f;
//    private long timestamp;
//    private boolean initState = true;

        public static final int TIME_CONSTANT = 30;
        public static final float FILTER_COEFFICIENT = 0.5f;
        private Timer fuseTimer = new Timer();

        Button buttonS;
        Button button;
        private SensorManager mSensorManager;
        Sensor sensorAccelerometr;
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
        float xx;
        float x;
        float yy;
        float zz;



        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_giroscope_activ_filtr);

            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            sensorAccelerometr = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorGiroscope=mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
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



            //series.appendData(new DataPoint(graph2LastYValue, y), true, 20);
            seriesX.appendData(new DataPoint(graph2LastXValue, x), true, 20);
            // seriesZ.appendData(new DataPoint(graph2LastZValue, z), true, 20);
            seriesXX.appendData(new DataPoint(graph2LastXValue, fusedOrientation[0]), true, 20);
            // seriesYY.appendData(new DataPoint(graph2LastYValue,    fusedOrientation[1]), true, 20);
            // seriesZZ.appendData(new DataPoint(graph2LastZValue, zz), true, 20);
            graph.addSeries(seriesX);
            //  graph.addSeries(seriesZ);
            graph.addSeries(seriesXX);
            //  graph.addSeries(series);
            // graph.addSeries(seriesYY);
            // graph.addSeries(seriesZZ);
        }

        private void addDataPoint(double acceleration) {
            dataPoints[499] = acceleration;
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

            thread.start();
        }

        @Override
        protected void onPause() {
            super.onPause();

            if (thread != null) {
                thread.interrupt();
            }
            mSensorManager.unregisterListener((SensorEventListener) this);

        }

        //    @Override
        public void onSensorChanged(final SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE)

                if (plotData) {
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
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        @Override
        protected void onResume() {
            super.onResume();
            mSensorManager.registerListener((SensorEventListener) this, sensorGiroscope, SensorManager.SENSOR_DELAY_NORMAL);
        }

        @Override
        protected void onDestroy() {
            mSensorManager.unregisterListener((SensorEventListener) GiroscopeActivFiltr.this);
            thread.interrupt();
            super.onDestroy();
        }



        // angular speeds from gyro
        private float[] gyro = new float[3];

        // rotation matrix from gyro data
        private float[] gyroMatrix = new float[9];

        // orientation angles from gyro matrix
      //  private float[] gyroOrientation = new float[3];

        // magnetic field vector
        //private float[] magnet = new float[3];

        // accelerometer vector
        private float[] accel = new float[3];
        private float[] gir = new float[3];


        private float[] fusedOrientation = new float[3];

        private float[] selectedOrientation = fusedOrientation;


//    public enum Mode {
//            ACC_MAG, GYRO, FUSION
//        }


        public GiroscopeActivFiltr() {

//            gyroOrientation[0] = 0.0f;
//            gyroOrientation[1] = 0.0f;
//            gyroOrientation[2] = 0.0f;
//
//            // initialise gyroMatrix with identity matrix
            gyroMatrix[0] = 1.0f;
            gyroMatrix[1] = 0.0f;
            gyroMatrix[2] = 0.0f;
            gyroMatrix[3] = 0.0f;
            gyroMatrix[4] = 1.0f;
            gyroMatrix[5] = 0.0f;
            gyroMatrix[6] = 0.0f;
            gyroMatrix[7] = 0.0f;
            gyroMatrix[8] = 1.0f;

            // wait for one second until gyroscope and magnetometer/accelerometer
            // data is initialised then scedule the complementary filter task
            fuseTimer.scheduleAtFixedRate(new calculateFusedOrientationTask(), 1000, TIME_CONSTANT);

        }

        public double getAzimuth() {
            return selectedOrientation[0] * 180 / Math.PI;
        }

        public double getPitch() {
            return selectedOrientation[1] * 180 / Math.PI;
        }

        public double getRoll() {
            return selectedOrientation[2] * 180 / Math.PI;
        }

//        public void setMagnet(float[] sensorValues) {
//            System.arraycopy(sensorValues, 0, magnet, 0, 3);
//        }

        public void setAccel(float[] sensorValues) {
            System.arraycopy(sensorValues, 0, accel, 0, 3);

        }

        public void setGir(float[] sensorValues) {
            System.arraycopy(sensorValues, 0, gir, 0, 3);

        }


        class calculateFusedOrientationTask extends TimerTask {
            public void run() {
                float oneMinusCoeff = 1.0f - FILTER_COEFFICIENT;



                fusedOrientation[0] = FILTER_COEFFICIENT * gir[0] + oneMinusCoeff * accel[0];

                fusedOrientation[1] = FILTER_COEFFICIENT * gir[1] + oneMinusCoeff * accel[1];

                fusedOrientation[2] = FILTER_COEFFICIENT * gir[2] + oneMinusCoeff * accel[2];
            }

        }




}
