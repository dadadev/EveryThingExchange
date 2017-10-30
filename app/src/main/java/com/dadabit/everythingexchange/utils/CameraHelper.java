package com.dadabit.everythingexchange.utils;


import android.annotation.SuppressLint;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import java.util.Arrays;

public class CameraHelper implements TextureView.SurfaceTextureListener{

    private CameraManager mCameraManager;
    private String mCameraID;

    private CameraDevice mCameraDevice;

    private TextureView mTextureView;

    private CameraCaptureSession mSession;

    private ImageReader mImageReader;

    private final CameraDevice.StateCallback mCameraCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {

            mCameraDevice = camera;

            createCameraPreviewSession();

            Log.i("@@@", "Open camera with id: "+ mCameraDevice.getId());

        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {

            mCameraDevice.close();

            Log.i("@@@", "Disconnect camera with id: "+ mCameraDevice.getId());

            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {

            Log.i("@@@", "Error! camera id: "+ mCameraDevice.getId() + " error: "+error);

        }
    };

    public CameraHelper(@NonNull CameraManager mCameraManager, @NonNull String mCameraID) {
        this.mCameraManager = mCameraManager;
        this.mCameraID = mCameraID;
    }

    public void setTextureView(TextureView textureView){
        mTextureView = textureView;
    }

    public void viewCameraInfo() {

        try {

            CameraCharacteristics cameraCharacteristics =
                    mCameraManager.getCameraCharacteristics(mCameraID);


            StreamConfigurationMap configurationMap =
                    cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);


            Size[] sizesJPEG =
                    configurationMap != null
                            ? configurationMap.getOutputSizes(ImageFormat.JPEG)
                            : null;

            if (sizesJPEG != null) {

                for (Size item :
                        sizesJPEG) {

                    Log.i("@@@", "w: " + item.getWidth() + "\nh: " + item.getHeight());

                }
            } else {

                Log.e("@@@", "camera " + mCameraID + " don`t support JPEG");

            }


        } catch (CameraAccessException | NullPointerException e) {

            Log.e("@@@", e.getMessage());
            e.printStackTrace();

        }


    }


    private void createCameraPreviewSession() throws NullPointerException{

        try {

            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;

//            texture.setDefaultBufferSize(1920, 1080);
            texture.setDefaultBufferSize(1920, 1080);

            Surface surface = new Surface(texture);



            final CaptureRequest.Builder builder =
                    mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);


            

            builder.addTarget(surface);

            mCameraDevice.createCaptureSession(
                    Arrays.asList(surface),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {

                            mSession = session;

                            try {
                                mSession.setRepeatingRequest(builder.build(), null, null);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                            Log.i("@@@", "Camera Capture Session Error!");

                        }
                    },
                    null );

        } catch (CameraAccessException e) {

            e.printStackTrace();
        }

    }

    @SuppressLint("MissingPermission")
    public void openCamera() {

        try {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }

            mCameraManager.openCamera(mCameraID, mCameraCallback, null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    public void closeCamera() {

        if (mCameraDevice != null){
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }

    public boolean isOpen() {
        return mCameraDevice != null;
    }



    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

        openCam(width, height);


    }

    private void openCam(int width, int height) {



    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
