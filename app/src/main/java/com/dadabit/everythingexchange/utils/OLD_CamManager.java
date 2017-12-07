package com.dadabit.everythingexchange.utils;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Size;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;

public class OLD_CamManager {

    private CamCallback mCallback;

    private SparseArray<String> camerasList;
    private String currentCamera;

    private Size previewSize;



    private CameraManager mCameraManager;

    private ImageReader mImageReader;

    private CameraDevice mCameraDevice;

    private ImageReader imageReader;


    private CameraCaptureSession mCameraCaptureSession;
    private CameraCharacteristics mCameraCharacteristics;

    private CaptureRequest.Builder captureRequestBuilder;
    private CaptureRequest.Builder captureRequestBuilderImageReader;


    private Size imageSize;

    private String mCameraID;

    private HandlerThread backgroundThread;
    private Handler backgroundHandler;

    private Context mContext;

    public OLD_CamManager(Context mContext) {
        this.mContext = mContext;
        this.mCameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
    }

    public void setCallback(CamCallback mCallback) {
        this.mCallback = mCallback;
    }


    public SparseArray<String> getCamerasList(){
        camerasList = new SparseArray<>();
        try {
            String[] camerasAvailable = mCameraManager.getCameraIdList();
            CameraCharacteristics cam;
            Integer characteristic;
            for (String id : camerasAvailable){
                cam = mCameraManager.getCameraCharacteristics(id);
                characteristic = cam.get(CameraCharacteristics.LENS_FACING);
                if (characteristic!=null){
                    switch (characteristic){
                        case CameraCharacteristics.LENS_FACING_FRONT:
                            camerasList.put(CameraCharacteristics.LENS_FACING_FRONT, id);
                            break;

                        case CameraCharacteristics.LENS_FACING_BACK:
                            camerasList.put(CameraCharacteristics.LENS_FACING_BACK, id);
                            break;

                        case CameraCharacteristics.LENS_FACING_EXTERNAL:
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                camerasList.put(CameraCharacteristics.LENS_FACING_EXTERNAL, id);
                            }
                            break;
                    }
                }
            }
            return camerasList;
        } catch (CameraAccessException e) {
            notifyError(e.getMessage());
            return null;
        }
    }



    public void selectCamera(String id) {
        if(camerasList == null){
            getCamerasList();
        }

        currentCamera = camerasList.indexOfValue(id)<0?null:id;
        if(currentCamera == null) {
            notifyError("Camera id not found.");
            return;
        }

        try {

            mCameraCharacteristics = mCameraManager.getCameraCharacteristics(currentCamera);

            StreamConfigurationMap map =
                    mCameraCharacteristics
                            .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

            if(map != null) {
                previewSize = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)), new CompareSizesByArea());
                imageReader = ImageReader.newInstance(previewSize.getWidth(), previewSize.getHeight(), ImageFormat.JPEG, 1);
                imageReader.setOnImageAvailableListener(onImageAvailableListener, backgroundHandler);
            }
            else{
                notifyError("Could not get configuration map.");
            }
        } catch (CameraAccessException e) {
            notifyError(e.getMessage());
        }
    }


    public void open(final int templateType, final TextureView textureView){

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            notifyError("You don't have the required permissions.");
            return;
        }

        startBackgroundThread();

        try {

            mCameraManager
                    .openCamera(
                            currentCamera,
                            new CameraDevice.StateCallback() {
                                @Override
                                public void onOpened(@NonNull CameraDevice camera) {

                                    mCameraDevice = camera;

                                    setupPreview(templateType, textureView);

                                }

                                @Override
                                public void onDisconnected(@NonNull CameraDevice camera) {
                                    if(mCallback != null){
                                        mCallback.onError("Camera device is no longer available for use.");
                                        mCallback.onCameraDisconnected();
                                    }
                                }

                                @Override
                                public void onError(@NonNull CameraDevice camera, int error) {

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
                            },
                            backgroundHandler);

        } catch (CameraAccessException e) {
            notifyError(e.getMessage());
        }

    }

    private void setupPreview(final int templateType, final TextureView outputSurface) {


        Log.d("@@@", "OLD_CamManager.setupPreview");


        if(outputSurface.isAvailable()){

            Log.d("@@@", "OLD_CamManager.setupPreview_________isAvailable");
            initPreview(templateType, outputSurface);
        }
        else{

            Log.d("@@@", "OLD_CamManager.setupPreview_________notAvailable");
            outputSurface.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                    setAspectRatioTextureView(outputSurface, width, height);
                    initPreview(templateType, outputSurface);
                }

                public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {}
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {return false;}
                public void onSurfaceTextureUpdated(SurfaceTexture surface) {}
            });
        }


    }

    private void setAspectRatioTextureView(TextureView outputSurface, int surfaceWidth, int surfaceHeight) {


        int rotation = ((Activity)mContext).getWindowManager().getDefaultDisplay().getRotation();
        int newWidth = surfaceWidth, newHeight = surfaceHeight;

        switch (rotation) {
            case Surface.ROTATION_0:
                newWidth = surfaceWidth;
                newHeight = (surfaceWidth * previewSize.getWidth() / previewSize.getHeight());
                break;

            case Surface.ROTATION_180:
                newWidth = surfaceWidth;
                newHeight = (surfaceWidth * previewSize.getWidth() / previewSize.getHeight());
                break;

            case Surface.ROTATION_90:
                newWidth = surfaceHeight;
                newHeight = (surfaceHeight * previewSize.getWidth() / previewSize.getHeight());
                break;

            case Surface.ROTATION_270:
                newWidth = surfaceHeight;
                newHeight = (surfaceHeight * previewSize.getWidth() / previewSize.getHeight());
                break;
        }

        outputSurface.setLayoutParams(new FrameLayout.LayoutParams(newWidth, newHeight, Gravity.CENTER));
        rotatePreview(outputSurface, rotation, newWidth, newHeight);


    }

    private void rotatePreview(TextureView mTextureView, int rotation, int viewWidth, int viewHeight) {
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        mTextureView.setTransform(matrix);
    }

    private void initPreview(int templateType, TextureView textureView) {

//        Surface surface = new Surface(textureView.getSurfaceTexture());

        SurfaceTexture texture = textureView.getSurfaceTexture();
        assert texture != null;

//            texture.setDefaultBufferSize(1920, 1080);
        texture.setDefaultBufferSize(1920, 1080);

        Surface surface = new Surface(texture);


        try {
            captureRequestBuilder = mCameraDevice.createCaptureRequest(templateType);
            captureRequestBuilder.addTarget(surface);

            captureRequestBuilderImageReader = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureRequestBuilderImageReader.addTarget(imageReader.getSurface());

            mCameraDevice.createCaptureSession(
                    Arrays.asList(
                            surface,
                            imageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            mCameraCaptureSession = session;
                            if(mCallback != null){
                                mCallback.onCameraReady();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                            notifyError("Could not configure capture session.");
                        }
                    },
                    backgroundHandler);

        } catch (CameraAccessException e) {
            notifyError(e.getMessage());
        }

    }


    public void startPreview(){
        try {
            mCameraCaptureSession.setRepeatingRequest(
                    captureRequestBuilder.build(),
                    null,
                    backgroundHandler);
        } catch (CameraAccessException e) {
            notifyError(e.getMessage());
        }
    }



    public void takePicture(){

        captureRequestBuilderImageReader.set(
                CaptureRequest.JPEG_ORIENTATION,
                mCameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION));
        try {
            mCameraCaptureSession.capture(
                    captureRequestBuilderImageReader.build(), null, backgroundHandler);
        } catch (CameraAccessException e) {
            notifyError(e.getMessage());
        }
    }




    private void startBackgroundThread() {

        backgroundThread = new HandlerThread("ete.Camera");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());

    }


    private void stopBackgroundThread() {
        backgroundThread.quitSafely();
        try {
            backgroundThread.join();
            backgroundThread = null;
            backgroundHandler = null;
        } catch (InterruptedException e) {
            notifyError(e.getMessage());
        }
    }

    private ImageReader.OnImageAvailableListener onImageAvailableListener =
            new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    if(mCallback != null){
                        mCallback.onPicture(imageReader.acquireLatestImage());
                    }
                }
            };


    public ImageReader getImageReader(){

        return mImageReader =
                ImageReader.newInstance(
                        imageSize.getWidth(),
                        imageSize.getHeight(),
                        ImageFormat.JPEG,
                        2);
    }

    private void setUpCamera(int width, int height){

        try {

            for (String cameraID : mCameraManager.getCameraIdList()) {

                CameraCharacteristics characteristics
                        = mCameraManager.getCameraCharacteristics(cameraID);

                // skip front camera
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }

                StreamConfigurationMap map = characteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null) {
                    continue;
                }

//
//                Size largest = Collections.max(
//                        Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
//                        new CompareSizesByArea());


            }

        } catch (CameraAccessException | NullPointerException e) {
            e.printStackTrace();
        }

    }


    public<T> void setCaptureSetting(CaptureRequest.Key<T> key, T value){
        if(captureRequestBuilder!=null && captureRequestBuilderImageReader!=null) {
            captureRequestBuilder.set(key, value);
            captureRequestBuilderImageReader.set(key, value);
        }
    }

    private String getId() throws CameraAccessException {

        for (String cameraID :
                mCameraManager.getCameraIdList()) {

            Integer facing =
                    mCameraManager
                            .getCameraCharacteristics(cameraID)
                            .get(CameraCharacteristics.LENS_FACING);

            if (facing != null
                    && facing == CameraCharacteristics.LENS_FACING_BACK) {
                return cameraID;
            }
        }
        return null;
    }

    public void closeCamera(){

        if (mCameraCaptureSession != null){
            mCameraCaptureSession.close();
            mCameraCaptureSession = null;
        }

        if (mCameraDevice != null){
            mCameraDevice.close();
            mCameraDevice = null;
        }

        if (mImageReader != null){
            mImageReader.close();
            mImageReader = null;
        }

    }



    private void notifyError(String message) {

        if (mCallback != null) {
            mCallback.onError(message);
        }

    }

    public void stopPreview() {
        try {
            mCameraCaptureSession.stopRepeating();
        } catch (CameraAccessException e) {
            notifyError(e.getMessage());
        }
    }


    public void close(){
        mCameraDevice.close();
        stopBackgroundThread();
    }



    public static File saveImage(Image image, File file) throws IOException {
        if(file.exists()) {
            image.close();
            return null;
        }
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        FileOutputStream output = new FileOutputStream(file);
        output.write(bytes);
        image.close();
        output.close();
        return file;
    }


    public interface CamCallback {

        void onCameraReady();
        void onPicture(Image image);
        void onError(String message);
        void onCameraDisconnected();


    }
}
