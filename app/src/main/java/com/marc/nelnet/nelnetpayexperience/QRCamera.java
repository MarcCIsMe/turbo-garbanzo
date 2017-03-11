package com.marc.nelnet.nelnetpayexperience;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.Size;
import android.util.SparseArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marc on 3/10/2017.
 */

public class QRCamera {

    public static final int CAMERA_REQUEST_CODE = 10;
    private static CameraManager mCameraManager;
    private static CameraCharacteristics mCameraCharacteristics;
    private static String mCameraId;
    private static CameraDevice mCameraDevice;

    public interface QRCameraListener {
        void onBarcodeFound(String barcodeText);
    }

    public static void startQRCamera(final Activity activity, final SurfaceView surfaceView, final QRCameraListener listener) {

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
                alertBuilder.setMessage(R.string.qr_code_rationale_message)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
            }
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }
        detectQRCode(activity, surfaceView, listener);

//        CameraDevice.StateCallback cameraDeviceStateCallback = new CameraDevice.StateCallback() {
//            @Override
//            public void onOpened(@NonNull CameraDevice camera) {
//                mCameraDevice = camera;
//                try {
//
//                    StreamConfigurationMap configs = mCameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
//                    Size[] sizes = configs.getOutputSizes(SurfaceHolder.class);
//                    surfaceView.getHolder().setFixedSize(sizes[0].getWidth(), sizes[0].getHeight());
//                    List<Surface> surfaceList = new ArrayList<>();
//                    surfaceList.add(surfaceView.getHolder().getSurface());
//
//                    mCameraDevice.createCaptureSession(surfaceList, new CameraCaptureSession.StateCallback() {
//                        @Override
//                        public void onConfigured(@NonNull CameraCaptureSession session) {
//                            try {
//
//                                CaptureRequest.Builder captureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_ZERO_SHUTTER_LAG);
//                                captureRequestBuilder.addTarget(surfaceView.getHolder().getSurface());
//                                session.setRepeatingRequest(captureRequestBuilder.build(), null, null);
//                                detectQRCode(activity, surfaceView, listener);
//                            } catch (CameraAccessException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        @Override
//                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
//
//                        }
//                    }, null);
//                } catch (CameraAccessException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onDisconnected(@NonNull CameraDevice camera) {
//
//            }
//
//            @Override
//            public void onError(@NonNull CameraDevice camera, int error) {
//
//            }
//        };
//        mCameraManager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
//        try {
//            String[] cameraIdList = mCameraManager.getCameraIdList();
//            mCameraId = cameraIdList[0];
//            mCameraCharacteristics = mCameraManager.getCameraCharacteristics(mCameraId);
//            mCameraManager.openCamera(mCameraId, cameraDeviceStateCallback, null);
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
    }

    private static void detectQRCode(final Activity activity, final SurfaceView surfaceView, final QRCameraListener listener) {
        final BarcodeDetector detector = new BarcodeDetector.Builder(activity.getApplicationContext())
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        if (!detector.isOperational()) {
            Toast.makeText(activity, "Could not setup the detector!", Toast.LENGTH_SHORT).show();
            return;
        }

        final CameraSource cameraSource = new CameraSource.Builder(activity, detector)
                .setRequestedPreviewSize(surfaceView.getHolder().getSurfaceFrame().width(), surfaceView.getHolder().getSurfaceFrame().height())
                .setAutoFocusEnabled(true)
                .build();
        try {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            cameraSource.start(surfaceView.getHolder());
        } catch (IOException ie) {
            Log.e("CAMERA SOURCE", ie.getMessage());
        }
        detector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    if(listener != null) {
                        listener.onBarcodeFound(barcodes.valueAt(0).rawValue);

                    }
                    detector.release();
                    cameraSource.stop();
                    cameraSource.release();
                }
            }
        });
    }
}
