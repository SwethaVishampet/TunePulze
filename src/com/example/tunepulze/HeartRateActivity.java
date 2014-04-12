package com.example.tunepulze;

import java.util.concurrent.atomic.AtomicBoolean;

//import com.jwetherell.heart_rate_monitor.ImageProcessing;
//import com.jwetherell.heart_rate_monitor.HeartRateMonitor.TYPE;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;



public class HeartRateActivity extends FragmentActivity {
	
	 private static final String TAG = "HeartRateMonitor";
	    private static final AtomicBoolean processing = new AtomicBoolean(false);

	    private static SurfaceView preview = null;
	    private static SurfaceHolder previewHolder = null;
	    private static Camera camera = null;
	    
	    private static TextView text = null;

	    private static WakeLock wakeLock = null;

	    private static int averageIndex = 0;
	    private static final int averageArraySize = 4;
	    private static final int[] averageArray = new int[averageArraySize];

	    public static enum TYPE {
	        GREEN, RED
	    };

	    private static TYPE currentType = TYPE.GREEN;

	    public static TYPE getCurrent() {
	        return currentType;
	    }

	    private static int beatsIndex = 0;
	    private static final int beatsArraySize = 3;
	    private static final int[] beatsArray = new int[beatsArraySize];
	    private static double beats = 0;
	    private static long startTime = 0;

	    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.heart_rate);
		
		
		 preview = (SurfaceView) findViewById(R.id.preview);
	        previewHolder = preview.getHolder();
	        previewHolder.addCallback(surfaceCallback);
	        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

	        
	        text = (TextView) findViewById(R.id.text);

	        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
	        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");

	        
	}
//	@Override
//	public void onBackPressed() {
////		super.onBackPressed();
////		Intent tabi=new Intent(getApplicationContext(),BroadcastAppActivity.class);
////		tabi.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
////		startActivity(tabi);
//	}
	
	//From other App
	 @Override
	public void onConfigurationChanged(Configuration newConfig) {
	        super.onConfigurationChanged(newConfig);
	 }
	 
	 public void onResume() {
	        super.onResume();

	        wakeLock.acquire();

	        camera = Camera.open();

	        startTime = System.currentTimeMillis();
	  }
	 
	 @Override
	    public void onPause() {
	        super.onPause();

	        wakeLock.release();

	        camera.setPreviewCallback(null);
	        camera.stopPreview();
	        camera.release();
	        camera = null;
	    }

	    private static PreviewCallback previewCallback = new PreviewCallback() {

	        /**
	         * {@inheritDoc}
	         */
	        @Override
	        public void onPreviewFrame(byte[] data, Camera cam) {
	            if (data == null) throw new NullPointerException();
	            Camera.Size size = cam.getParameters().getPreviewSize();
	            if (size == null) throw new NullPointerException();

	            if (!processing.compareAndSet(false, true)) return;

	            int width = size.width;
	            int height = size.height;

	            int imgAvg = ImageProcessing.decodeYUV420SPtoRedAvg(data.clone(), height, width);
	             Log.i(TAG, "imgAvg="+imgAvg);
	            if (imgAvg == 0 || imgAvg == 255) {
	                processing.set(false);
	                return;
	            }

	            int averageArrayAvg = 0;
	            int averageArrayCnt = 0;
	            for (int i = 0; i < averageArray.length; i++) {
	                if (averageArray[i] > 0) {
	                    averageArrayAvg += averageArray[i];
	                    averageArrayCnt++;
	                }
	            }

	            int rollingAverage = (averageArrayCnt > 0) ? (averageArrayAvg / averageArrayCnt) : 0;
	            Log.d(TAG, "rollingAverage="+rollingAverage);
	            TYPE newType = currentType;
	            if (imgAvg < rollingAverage) {
	                newType = TYPE.RED;
	                if (newType != currentType) {
	                    beats++;
	                    //Log.d(TAG, "BEAT!! beats="+beats);
	                }
	            } else if (imgAvg > rollingAverage) {
	                newType = TYPE.GREEN;
	            }

	            if (averageIndex == averageArraySize) averageIndex = 0;
	            averageArray[averageIndex] = imgAvg;
	            averageIndex++;

	            // Transitioned from one state to another to the same
	            if (newType != currentType) {
	                currentType = newType;
	              
	            }

	            long endTime = System.currentTimeMillis();
	            double totalTimeInSecs = (endTime - startTime) / 1000d;
	            if (totalTimeInSecs >= 10) {
	                double bps = (beats / totalTimeInSecs);
	                int dpm = (int) (bps * 60d);
	                if (dpm < 50 || dpm > 110) {
	                    startTime = System.currentTimeMillis();
	                    beats = 0;
	                    processing.set(false);
	                    return;
	                }

	                // Log.d(TAG,
	                // "totalTimeInSecs="+totalTimeInSecs+" beats="+beats);

	                if (beatsIndex == beatsArraySize) beatsIndex = 0;
	                beatsArray[beatsIndex] = dpm;
	                beatsIndex++;

	                int beatsArrayAvg = 0;
	                int beatsArrayCnt = 0;
	                for (int i = 0; i < beatsArray.length; i++) {
	                    if (beatsArray[i] > 0) {
	                        beatsArrayAvg += beatsArray[i];
	                        beatsArrayCnt++;
	                    }
	                }
	                int beatsAvg = (beatsArrayAvg / beatsArrayCnt);
	                text.setText(String.valueOf(beatsAvg));
	                startTime = System.currentTimeMillis();
	                beats = 0;
	            }
	            processing.set(false);
	        }
	    };

	    private static SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

	        /**
	         * {@inheritDoc}
	         */
	        @Override
	        public void surfaceCreated(SurfaceHolder holder) {
	            try {
	                camera.setPreviewDisplay(previewHolder);
	                camera.setPreviewCallback(previewCallback);
	            } catch (Throwable t) {
	                Log.e("PreviewDemo-surfaceCallback", "Exception in setPreviewDisplay()", t);
	            }
	        }

	        /**
	         * {@inheritDoc}
	         */
	        @Override
	        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	            Camera.Parameters parameters = camera.getParameters();
	            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
	            Camera.Size size = getSmallestPreviewSize(width, height, parameters);
	            if (size != null) {
	                parameters.setPreviewSize(size.width, size.height);
	                Log.d(TAG, "Using width=" + size.width + " height=" + size.height);
	            }
	            camera.setParameters(parameters);
	            camera.startPreview();
	        }

	        /**
	         * {@inheritDoc}
	         */
	        @Override
	        public void surfaceDestroyed(SurfaceHolder holder) {
	            // Ignore
	        }
	    };

	    private static Camera.Size getSmallestPreviewSize(int width, int height, Camera.Parameters parameters) {
	        Camera.Size result = null;

	        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
	            if (size.width <= width && size.height <= height) {
	                if (result == null) {
	                    result = size;
	                } else {
	                    int resultArea = result.width * result.height;
	                    int newArea = size.width * size.height;

	                    if (newArea < resultArea) result = size;
	                }
	            }
	        }

	        return result;
	    }
}
