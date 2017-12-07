package com.dadabit.everythingexchange.utils;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.ContentValues.TAG;

public class CameraHelper {

    private Context mContext;

    private String cameraId;


    private TextureView mTextureView;

    private final CameraManager mCameraManager;
    private CameraDevice cameraDevice;
    private CameraCaptureSession mCameraCaptureSessions;
    private CameraCharacteristics mCameraCharacteristics;
    private CaptureRequest.Builder captureRequestBuilder;
    private CaptureRequest.Builder captureRequestBuilderImageReader;
    private Size imageDimension;
    private ImageReader imageReader;
    private File file;

    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;

    private CamCallback mCallback;


    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }



    public CameraHelper(Context mContext) {
        Log.d("@@@", "CameraHelper.create");
        this.mContext = mContext;
        this.mCameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);

    }

    public void setCallback(CamCallback mCallback){
        this.mCallback = mCallback;
    }

    public void setTextureView(TextureView mTextureView){
        Log.d("@@@", "CameraHelper.setTextureView");
        this.mTextureView = mTextureView;



        if (mTextureView.getSurfaceTexture() == null ){
            Log.d("@@@", "mTextureView.getSurfaceTexture() == null");

            mTextureView.setSurfaceTextureListener(
                    new TextureView.SurfaceTextureListener() {
                        @Override
                        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                            Log.d("@@@", "CameraHelper.SurfaceTextureListener.onSurfaceTextureAvailable");
                            openCamera();
                        }
                        @Override
                        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                            Log.d("@@@", "CameraHelper.SurfaceTextureListener.onSurfaceTextureSizeChanged");
                        }
                        @Override
                        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                            Log.d("@@@", "CameraHelper.SurfaceTextureListener.onSurfaceTextureDestroyed");
                            closeCamera();
                            return false;
                        }
                        @Override
                        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                        }
                    }
            );
        } else {
            Log.d("@@@", "mTextureView.getSurfaceTexture() != null");
            openCamera();

        }
    }


    private TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            Log.d("@@@", "CameraHelper.SurfaceTextureListener.onSurfaceTextureAvailable");
            openCamera();
        }
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            Log.d("@@@", "CameraHelper.SurfaceTextureListener.onSurfaceTextureSizeChanged");
        }
        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            Log.d("@@@", "CameraHelper.SurfaceTextureListener.onSurfaceTextureDestroyed");
            return false;
        }
        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };



    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            //This is called when the camera is open
            Log.d("@@@", "CameraHelper.stateCallback.onOpened");
            cameraDevice = camera;
            createCameraPreview();
        }
        @Override
        public void onDisconnected(CameraDevice camera) {
            Log.d("@@@", "CameraHelper.stateCallback.onDisconnected");
            cameraDevice.close();
            mCallback.onError("Camera device is no longer available for use.");
            mCallback.onCameraDisconnected();
        }
        @Override
        public void onError(CameraDevice camera, int error) {
            Log.e("@@@", "CameraHelper.stateCallback.onError");
            cameraDevice.close();
            cameraDevice = null;


            switch (error){
                case CameraDevice.StateCallback.ERROR_CAMERA_DEVICE:
                    notifyError("Camera device has encountered a fatal error.");
                    break;
                case CameraDevice.StateCallback.ERROR_CAMERA_DISABLED:
                    notifyError("Camera device could not be opened due to a device policy.");
                    break;
                case CameraDevice.StateCallback.ERROR_CAMERA_IN_USE:
                    notifyError("Camera device is in use already.");
                    break;
                case CameraDevice.StateCallback.ERROR_CAMERA_SERVICE:
                    notifyError("Camera service has encountered a fatal error.");
                    break;
                case CameraDevice.StateCallback.ERROR_MAX_CAMERAS_IN_USE:
                    notifyError("Camera device could not be opened because there are too many other open camera devices.");
                    break;
            }
        }
    };


    private ImageReader.OnImageAvailableListener onImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            Log.d("@@@", "OLD_CamManager.ImageReader.OnImageAvailableListener.onImageAvailable");

            if(mCallback != null){
                mCallback.onPicture(Utils.imageToBytes(reader.acquireLatestImage()));
            }
        }
    };


    private void openCamera() {
        Log.d("@@@", "CameraHelper.openCamera");

        startBackgroundThread();

        try {

            if (cameraId == null){

                cameraId = mCameraManager.getCameraIdList()[0];

                mCameraCharacteristics = mCameraManager.getCameraCharacteristics(cameraId);

                StreamConfigurationMap map = mCameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                assert map != null;
                imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];

                // Check permission for camera and let user grant the permission
                if (ActivityCompat
                        .checkSelfPermission(mContext, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat
                        .checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
//                mCallback.onCheckPermission();
//                ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                    mCallback.onError("NO PERMISSION");
                    return;
                }

            }

            mCameraManager.openCamera(cameraId, stateCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            notifyError(e.getMessage());
        }
        Log.e(TAG, "openCamera X");
    }

    public void closeCamera(){
        Log.e("@@@", "closeCamera");
        if (cameraDevice != null){
            cameraDevice.close();
            cameraDevice = null;
        }
        stopBackgroundThread();


    }


    private void createCameraPreview() {

        try {
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());

            Surface surface = new Surface(texture);

            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);

            imageReader = ImageReader.newInstance(640,480,ImageFormat.JPEG,1);

            imageReader.setOnImageAvailableListener(
                    new ImageReader.OnImageAvailableListener() {
                        @Override
                        public void onImageAvailable(ImageReader reader) {
                            Log.d("@@@", "CameraHelper.ImageReader.OnImageAvailableListener.onImageAvailable");

                            if(mCallback != null){
                                mCallback.onPicture(Utils.imageToBytes(reader.acquireLatestImage()));
                            }
                        }
                    },
                    mBackgroundHandler);


            captureRequestBuilderImageReader = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureRequestBuilderImageReader.addTarget(imageReader.getSurface());

            cameraDevice.createCaptureSession(
                    Arrays.asList(surface, imageReader.getSurface()),
                    new CameraCaptureSession.StateCallback(){
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            //The camera is already closed
                            if (null == cameraDevice) {
                                return;
                            }
                            // When the session is ready, start displaying the preview.
                            mCameraCaptureSessions = cameraCaptureSession;


                            if(mCallback != null){
                                mCallback.onCameraReady();
                            }
                        }
                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

                            notifyError("Configuration change");

                        }
                    }, mBackgroundHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
            notifyError(e.getMessage());
        }


    }

    public<T> void setCaptureSetting(CaptureRequest.Key<T> key, T value){
        if(captureRequestBuilder!=null) {
            captureRequestBuilder.set(key, value);
        }
    }

    public void startPreview(){
        try {
            mCameraCaptureSessions.setRepeatingRequest(
                    captureRequestBuilder.build(),
                    null,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            notifyError(e.getMessage());
        }
    }

//    private void setAspectRatioTextureView(TextureView outputSurface, int surfaceWidth, int surfaceHeight) {
//
//
//        int rotation = ((Activity)mContext).getWindowManager().getDefaultDisplay().getRotation();
//        int newWidth = surfaceWidth, newHeight = surfaceHeight;
//
//        switch (rotation) {
//            case Surface.ROTATION_0:
//                newWidth = surfaceWidth;
//                newHeight = (surfaceWidth * previewSize.getWidth() / previewSize.getHeight());
//                break;
//
//            case Surface.ROTATION_180:
//                newWidth = surfaceWidth;
//                newHeight = (surfaceWidth * previewSize.getWidth() / previewSize.getHeight());
//                break;
//
//            case Surface.ROTATION_90:
//                newWidth = surfaceHeight;
//                newHeight = (surfaceHeight * previewSize.getWidth() / previewSize.getHeight());
//                break;
//
//            case Surface.ROTATION_270:
//                newWidth = surfaceHeight;
//                newHeight = (surfaceHeight * previewSize.getWidth() / previewSize.getHeight());
//                break;
//        }
//
//        outputSurface.setLayoutParams(new FrameLayout.LayoutParams(newWidth, newHeight, Gravity.CENTER));
//        rotatePreview(outputSurface, rotation, newWidth, newHeight);
//
//
//    }



    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    protected void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void takePic(){
        Log.d("@@@", "OLD_CamManager.takePicture");

        if(null == cameraDevice) {
            notifyError("cameraDevice is null");
            Log.e(TAG, "cameraDevice is null");
            return;
        }

        captureRequestBuilderImageReader.set(
                CaptureRequest.JPEG_ORIENTATION,
                mCameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION));
        try {
            mCameraCaptureSessions.capture(
                    captureRequestBuilderImageReader.build(),
                    new CameraCaptureSession.CaptureCallback() {
                        @Override
                        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                            super.onCaptureCompleted(session, request, result);
                            Log.d("@@@", "CameraHelper.onCaptureCompleted");

                        }
                    }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            notifyError(e.getMessage());
        }


    }

    public void savePic(Image image, File file){

//        file = new File(Environment.getDataDirectory()+"/pic.jpg");

        this.file = file;

        try {
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.capacity()];
            buffer.get(bytes);
            save(bytes);
        } catch (IOException e) {
            notifyError(e.getMessage());
            e.printStackTrace();
        } finally {
            if (image != null) {
                image.close();
            }
        }

    }
    private void save(byte[] bytes) throws IOException {
        OutputStream output = null;
        try {
            output = new FileOutputStream(file);
            output.write(bytes);
        } finally {
            if (null != output) {
                output.close();

                Log.d("@@@", "-----------"+ file.getAbsolutePath());


            }
        }
    }



    private void notifyError(String message) {
        Log.e("@@@", "CameraHelper.ERROR: "+message);

        if (mCallback != null) {
            mCallback.onError(message);
        }

    }


    public interface CamCallback {

        void onCameraReady();
        void onPicture(byte[] image);
        void onError(String message);
        void onCameraDisconnected();



    }

}
