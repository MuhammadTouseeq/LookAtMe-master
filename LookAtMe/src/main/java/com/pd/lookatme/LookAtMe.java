package com.pd.lookatme;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;

import java.io.IOException;


public class LookAtMe extends VideoView {

    private String status = "";
    private CameraSource cameraSource;
    private Context activityContext;

    public void init(Context activityContext) {
        this.activityContext = activityContext;
        createCameraSource();
    }

    public void init(Context activityContext, String mode, String cameraFace){
        this.activityContext = activityContext;
        createCameraSource(mode,cameraFace);
    }

    public void init(Context activityContext, String mode){
        this.activityContext = activityContext;
        createCameraSource(mode);
    }

    public void resume(){
        if (cameraSource != null) {
            try {
                if (ActivityCompat.checkSelfPermission(activityContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) activityContext, new String[]{Manifest.permission.CAMERA}, 1);
                    Toast.makeText(activityContext, "Grant Permission and restart app", Toast.LENGTH_SHORT).show();
                }
                cameraSource.start();
                //Log.d("some8","Starting first");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void paused(){
        if (cameraSource != null) {
            cameraSource.stop();
            //Log.d("some6","stopped here");
        }
        if (this.isPlaying()) {
            this.pause();
            //Log.d("some7","Paused() here");
        }
    }

    public void destroy(){
        if (cameraSource != null) {
            cameraSource.release();
        }
    }

    public LookAtMe(Context context) {
        super(context);
    }

    public LookAtMe(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LookAtMe(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LookAtMe(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void pause() {
        super.pause();

        //Log.d("some1","Paused due to pause()");
    }

    @Override
    public boolean isPlaying() {
        return super.isPlaying();
    }

    @Override
    public void start() {
        super.start();
    }

    public String getStatus(){
        return status;
    }

    public void setLookMe(){
        //Log.d("this is found","camerasource is " + cameraSource);

        try {
            if (ActivityCompat.checkSelfPermission(activityContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) activityContext, new String[]{Manifest.permission.CAMERA}, 1);

                Toast.makeText(activityContext, "Grant Permission and restart app", Toast.LENGTH_SHORT).show();
            }
            cameraSource.start();
            Log.d("ReadThis", "camerasource started, outside");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class EyesTracker extends Tracker<Face> {

        private final float THRESHOLD = 0.75f;

        private EyesTracker() {

        }

        @Override
        public void onUpdate(Detector.Detections<Face> detections, Face face) {
            if (face.getIsLeftEyeOpenProbability() > THRESHOLD || face.getIsRightEyeOpenProbability() > THRESHOLD) {
               // Log.d("some9", "Eyes Detecting");

                if (!isPlaying())
                    start();

                status = "Eyes Detected and open, so video continues";
                //Log.d("some3", "Started due to eyes detected");
            }
            else {
                if (isPlaying()){
                   // Log.d("some4","Paused due to eyes closed");
                    pause();
                }

                status = "Eyes Detected and closed, so video paused";
            }
        }

        @Override
        public void onMissing(Detector.Detections<Face> detections) {
            super.onMissing(detections);
            status = "Face Not Detected!";
            pause();
            //Log.d("some5","Face is Missing");
        }

        @Override
        public void onDone() {
            super.onDone();
        }
    }

    private void createCameraSource() {
        FaceDetector detector = new FaceDetector.Builder(activityContext).setTrackingEnabled(true).setClassificationType(FaceDetector.ALL_CLASSIFICATIONS).setMode(FaceDetector.FAST_MODE).build();

        detector.setProcessor(new LargestFaceFocusingProcessor(detector, new EyesTracker()));

        cameraSource = new CameraSource.Builder(activityContext, detector).setRequestedPreviewSize(1024, 768).setFacing(CameraSource.CAMERA_FACING_FRONT).setRequestedFps(30.0f).build();

        try {
            if (ActivityCompat.checkSelfPermission(activityContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) activityContext, new String[]{Manifest.permission.CAMERA}, 1);
                Toast.makeText(activityContext, "Grant Permission and restart app", Toast.LENGTH_SHORT).show();
            }
            else
                cameraSource.start();
            //Log.d("ReadThis", "camerasource started, inside");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createCameraSource(String mode) {

        // Let the mode be fast or accurate

        FaceDetector detector;

        if(mode.toLowerCase().equals("accurate"))
            detector = new FaceDetector.Builder(activityContext).setTrackingEnabled(true).setClassificationType(FaceDetector.ALL_CLASSIFICATIONS).setMode(FaceDetector.ACCURATE_MODE).build();
        else
            detector = new FaceDetector.Builder(activityContext).setTrackingEnabled(true).setClassificationType(FaceDetector.ALL_CLASSIFICATIONS).setMode(FaceDetector.FAST_MODE).build();

        detector.setProcessor(new LargestFaceFocusingProcessor(detector, new EyesTracker()));

        cameraSource = new CameraSource.Builder(activityContext, detector).setRequestedPreviewSize(1024, 768).setFacing(CameraSource.CAMERA_FACING_FRONT).setRequestedFps(30.0f).build();

        try {
            if (ActivityCompat.checkSelfPermission(activityContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) activityContext, new String[]{Manifest.permission.CAMERA}, 1);
                Toast.makeText(activityContext, "Grant Permission and restart app", Toast.LENGTH_SHORT).show();
            }
            else
                cameraSource.start();
            //Log.d("ReadThis", "camerasource started, inside");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createCameraSource(String mode, String cameraFace) {

        // Let the mode be fast or accurate
        // Let the cameraFace be front or back

        FaceDetector detector;

        if(mode.toLowerCase().equals("accurate"))
            detector = new FaceDetector.Builder(activityContext).setTrackingEnabled(true).setClassificationType(FaceDetector.ALL_CLASSIFICATIONS).setMode(FaceDetector.ACCURATE_MODE).build();
        else
            detector = new FaceDetector.Builder(activityContext).setTrackingEnabled(true).setClassificationType(FaceDetector.ALL_CLASSIFICATIONS).setMode(FaceDetector.FAST_MODE).build();

        detector.setProcessor(new LargestFaceFocusingProcessor(detector, new EyesTracker()));

        if(cameraFace.toLowerCase().equals("back"))
            cameraSource = new CameraSource.Builder(activityContext, detector).setRequestedPreviewSize(1024, 768).setFacing(CameraSource.CAMERA_FACING_BACK).setRequestedFps(30.0f).build();
        else
            cameraSource = new CameraSource.Builder(activityContext, detector).setRequestedPreviewSize(1024, 768).setFacing(CameraSource.CAMERA_FACING_FRONT).setRequestedFps(30.0f).build();

        try {
            if (ActivityCompat.checkSelfPermission(activityContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) activityContext, new String[]{Manifest.permission.CAMERA}, 1);
                Toast.makeText(activityContext, "Grant Permission and restart app", Toast.LENGTH_SHORT).show();
            }
            else
                cameraSource.start();
            //Log.d("ReadThis", "camerasource started, inside");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
