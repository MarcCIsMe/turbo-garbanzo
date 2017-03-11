package com.marc.nelnet.nelnetpayexperience.utils;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.marc.nelnet.nelnetpayexperience.R;

import java.io.IOException;

/**
 * Created by Marc on 3/10/2017.
 */

public class QRCamera {

    public static final int CAMERA_REQUEST_CODE = 10;

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
            return;
        }
        detectQRCode(activity, surfaceView, listener);
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
            // Here to make lint happy.
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

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
