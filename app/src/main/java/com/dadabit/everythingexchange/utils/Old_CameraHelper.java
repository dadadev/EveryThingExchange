package com.dadabit.everythingexchange.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Size;
import android.util.SparseArray;
import android.view.Surface;
import android.view.TextureView;

import java.util.Arrays;
import java.util.Collections;

public class Old_CameraHelper implements TextureView.SurfaceTextureListener{

    private CameraManager mCameraManager;
    private String mCameraID;

    private SparseArray<String> camerasList;


    private CameraDevice mCameraDevice;

    private TextureView mTextureView;

    private CameraCaptureSession mSession;

    private ImageReader mImageReader;

    private CameraCallback mCallback;

    private Context mContext;


    private HandlerThread backgroundThread;
    private Handler backgroundHandler;


    private CaptureRequest.Builder captureRequestBuilder;
    private CaptureRequest.Builder captureRequestBuilderImageReader;

    private final CameraDevice.StateCallback mCameraCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {

            mCameraDevice = camera;

            createCameraPreviewSession();

            if (mCallback != null){

                mCallback.onCameraReady();
            }

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



    private ImageReader.OnImageAvailableListener onImageAvailableListener =
            new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {


                    Log.i("@@@", "CameraHelper.ImageReader.OnImageAvailableListener");

                    if(mCallback != null){
                        mCallback.onPicture(imageReader.acquireLatestImage());
                    }
                }
            };





    private CameraCharacteristics mCameraCharacteristics;
    private Size previewSize;

    private ImageReader imageReader;


    public Old_CameraHelper(@NonNull CameraManager mCameraManager, @NonNull String mCameraID) {
        this.mCameraManager = mCameraManager;
        this.mCameraID = mCameraID;
    }

    public Old_CameraHelper(Context context) {
        this.mContext = context;
        this.mCameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
    }


    public void setCallback(CameraCallback mCallback) {
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
//            notifyError(e.getMessage());
            return null;
        }
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
                    backgroundHandler );

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


            startBackgroundThread();

            selectCamera(getCamerasList().get(CameraCharacteristics.LENS_FACING_FRONT));

            mCameraManager.openCamera(
                    getCamerasList().get(CameraCharacteristics.LENS_FACING_BACK),
                    mCameraCallback,
                    null);

        } catch (CameraAccessException e) {
            if(mCallback != null){
                mCallback.onError("Camera device is no longer available for use.");
                mCallback.onCameraDisconnected();
            }
        }

    }


    public void selectCamera(String id) {
        if(camerasList == null){
            getCamerasList();
        }

        mCameraID = camerasList.indexOfValue(id)<0?null:id;
        if(mCameraID == null) {
            notifyError("Camera id not found.");
            return;
        }

        try {

            mCameraCharacteristics = mCameraManager.getCameraCharacteristics(mCameraID);

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



    private void notifyError(String message) {

        if (mCallback != null) {
            mCallback.onError(message);
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



    public void takePicture(){


        Log.d("@@@", "CameraHelper.takePicture");

//        captureRequestBuilderImageReader.set(
//                CaptureRequest.JPEG_ORIENTATION,
//                mCameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION));

        captureRequestBuilderImageReader.set(CaptureRequest.CONTROL_AF_TRIGGER,
                CameraMetadata.CONTROL_AF_TRIGGER_START);

        try {
            mSession.capture(
                    captureRequestBuilderImageReader.build(), null, null);
        } catch (CameraAccessException e) {
            notifyError(e.getMessage());
        }
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



    private void startBackgroundThread() {

        backgroundThread = new HandlerThread("ete.Camera");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());

    }


    public interface CameraCallback {

        void onCameraReady();
        void onPicture(Image image);
        void onError(String message);
        void onCameraDisconnected();


    }

}
