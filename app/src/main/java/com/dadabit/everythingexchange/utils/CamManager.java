package com.dadabit.everythingexchange.utils;


import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.util.Size;

import java.util.Arrays;
import java.util.Collections;

public class CamManager {


    private CameraManager mCameraManager;

    private ImageReader mImageReader;

    private CameraDevice mCameraDevice;

    private CameraCaptureSession mCameraCaptureSession;

    private Size imageSize;

    private String mCameraID;

    public CamManager(CameraManager mCameraManager) {
        this.mCameraManager = mCameraManager;
    }

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
}
