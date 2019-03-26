package com.example.justeating;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionBarContextView;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;


/**
 * A simple {@link Fragment} subclass.
 */
public class CameraFragment extends Fragment implements View.OnClickListener {

    Camera camera;
    CameraManager cameraManager;
    String cameraId;
    HandlerThread backgroundThread;
    Handler backgroundHandler;
    CameraDevice.StateCallback stateCallback;
    CameraDevice cameraDevice;
    TextureView textureView;
    Size previewSize;
    CaptureRequest captureRequest;
    CaptureRequest.Builder captureRequestBuilder;
    CameraCaptureSession cameraCaptureSession;
    TextureView.SurfaceTextureListener surfaceTextureListener;
    private final int CAMERA = 1;
    FloatingActionButton captureBtn;
    Bitmap capturedImage;
    ProgressBar loadingSpinner;



    public CameraFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        getActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA);

        loadingSpinner = getActivity().findViewById(R.id.loadingSpinner);
        loadingSpinner.setIndeterminate(true);

        captureBtn = getActivity().findViewById(R.id.captureFab);
        captureBtn.setOnClickListener(this);

        FloatingActionButton approveBtn = getActivity().findViewById(R.id.approveFab);
        approveBtn.setOnClickListener(this);

        ImageButton cancelPhotoBtn = getActivity().findViewById(R.id.cancelButton);
        cancelPhotoBtn.setOnClickListener(this);

        ((ConstraintLayout) getActivity().findViewById(R.id.previewLayout)).setVisibility(View.INVISIBLE);

        cameraManager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);

        textureView = getView().findViewById(R.id.textureView);

        surfaceTextureListener = new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                setUpCamera();
                openCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

            }
        };




        stateCallback = new CameraDevice.StateCallback() {
            @Override
            public void onOpened(CameraDevice cameraDevice) {
                CameraFragment.this.cameraDevice = cameraDevice;
                createPreviewSession();
            }

            @Override
            public void onDisconnected(CameraDevice cameraDevice) {
                cameraDevice.close();
                CameraFragment.this.cameraDevice = null;
            }

            @Override
            public void onError(CameraDevice cameraDevice, int error) {
                cameraDevice.close();
                CameraFragment.this.cameraDevice = null;
            }
        };


    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.captureFab:
                onCaptureClicked();
                break;
            case R.id.cancelButton:
                onCancelClicked();
                break;
            case R.id.approveFab:
                onApproveClicked();
                break;
        }
    }

    private void setUpCamera() {
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {



                CameraCharacteristics cameraCharacteristics =
                        cameraManager.getCameraCharacteristics(cameraId);
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) ==
                        cameraManager.getCameraCharacteristics(cameraId).LENS_FACING_BACK) {
                    StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(
                            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    previewSize = streamConfigurationMap.getOutputSizes(SurfaceTexture.class)[0];
                    this.cameraId = cameraId;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openCamera() {
        try {
            if (ActivityCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                cameraManager.openCamera(cameraId, stateCallback, backgroundHandler);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openBackgroundThread() {
        backgroundThread = new HandlerThread("camera_background_thread");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("ON RESUME");
        loadingSpinner.setVisibility(View.INVISIBLE);
        openBackgroundThread();
        if (textureView.isAvailable()) {
            setUpCamera();
            openCamera();
        } else {
            textureView.setSurfaceTextureListener(surfaceTextureListener);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        closeCamera();
        closeBackgroundThread();
    }

    private void closeCamera() {
        if (cameraCaptureSession != null) {
            cameraCaptureSession.close();
            cameraCaptureSession = null;
        }

        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
    }

    private void closeBackgroundThread() {
        if (backgroundHandler != null) {
            backgroundThread.quitSafely();
            backgroundThread = null;
            backgroundHandler = null;
        }
    }


    private void createPreviewSession() {
        try {
            SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
            surfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            Surface previewSurface = new Surface(surfaceTexture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(previewSurface);

            cameraDevice.createCaptureSession(Collections.singletonList(previewSurface),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                            if (cameraDevice == null) {
                                return;
                            }

                            try {
                                captureRequest = captureRequestBuilder.build();
                                CameraFragment.this.cameraCaptureSession = cameraCaptureSession;
                                CameraFragment.this.cameraCaptureSession.setRepeatingRequest(captureRequest,
                                        null, backgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {

                        }
                    }, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void onCaptureClicked() {
        FileOutputStream outputPhoto = null;
        capturedImage = textureView.getBitmap();
        ((ConstraintLayout) getActivity().findViewById(R.id.previewLayout)).setVisibility(View.VISIBLE);
        ImageView imagePreview = getActivity().findViewById(R.id.imagePreview);
        imagePreview.setVisibility(View.VISIBLE);
        imagePreview.setImageBitmap(capturedImage);
        captureBtn.hide();
        onStop();
//        try {
//            outputPhoto = new FileOutputStream(createImageFile(galleryFolder));
//            textureView.getBitmap()
//                    .compress(Bitmap.CompressFormat.PNG, 100, outputPhoto);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (outputPhoto != null) {
//                    outputPhoto.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

    public void onCancelClicked() {
        ((ConstraintLayout) getActivity().findViewById(R.id.previewLayout)).setVisibility(View.INVISIBLE);
        captureBtn.show();
        onResume();
    }

    public void onApproveClicked() {

        loadingSpinner.setVisibility(View.VISIBLE);
        final Intent photoIntent = new Intent(this.getContext(), PhotoActivity.class);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                File out = new File(getActivity().getFilesDir(), "capture.jpg");
                photoIntent.putExtra(PhotoActivity.EXTRA_IMAGE, out.getAbsolutePath());

                try (FileOutputStream fOut = new FileOutputStream(out)) {
                    capturedImage.compress(Bitmap.CompressFormat.PNG, 100, fOut); // bmp is your Bitmap instance
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }});

        t.start();

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        startActivity(photoIntent);
    }


}


