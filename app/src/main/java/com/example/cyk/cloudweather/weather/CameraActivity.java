package com.example.cyk.cloudweather.weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cyk.cloudweather.R;
import com.example.cyk.cloudweather.db.DBManager;
import com.example.cyk.cloudweather.gson.Weather;
import com.example.cyk.cloudweather.json.WeatherJson;
import com.example.cyk.cloudweather.util.IconUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener{
    //相册
    private static final int CHOOSE_PHOTO = 2;
    private CameraManager mCameraManager;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceViewHolder;
    private Handler mHandler;
    private String mCameraId;
    private ImageReader mImageReader;
    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder mPreviewBuilder;
    private CameraCaptureSession mSession;
    private Bitmap bitmap;
    private Button cancelBtn, cameraBtn, sureBtn,backBtn;
    private ImageView pictureIv,weatherIv,img_show;
    private LinearLayout pictureLinear;
    private TextView countyTv,weatherTv,temperatureTv;
    private Handler mainHandler;
    //城市
    private String city;
    private Weather weather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        //提取数据
        Intent intent=getIntent();
        city = intent.getStringExtra("city");
        String s = DBManager.queryInfoByCity(city);
        weather = WeatherJson.getWeatherResponse(s);
        //隐藏ActionBar
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }
        //控件初始化
        img_show = (ImageView) findViewById(R.id.img_show);
        backBtn = (Button) findViewById(R.id.backBtn);
        cancelBtn = (Button) findViewById(R.id.cancelBtn);
        cameraBtn = (Button) findViewById(R.id.cameraBtn);
        sureBtn = (Button) findViewById(R.id.sureBtn);
        pictureIv = (ImageView) findViewById(R.id.pictureIv);
//        weatherIv = (ImageView) findViewById(R.id.weatherIv);
        pictureLinear = (LinearLayout) findViewById(R.id.pictureLinear);
        countyTv= (TextView) findViewById(R.id.countyTv);
        weatherTv= (TextView) findViewById(R.id.weatherTv);
        temperatureTv= (TextView) findViewById(R.id.temperatureTv);
        mSurfaceView = (SurfaceView) findViewById(R.id.mFirstSurfaceView);
        countyTv.setText(weather.basic.countyName);
        weatherTv.setText(weather.now.txt);
        temperatureTv.setText(weather.now.temperature+"℃");
        //初始化SurfaceView
        initSurfaceView();
        //点击事件
        img_show.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        cameraBtn.setOnClickListener(this);
        sureBtn.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_show:
                openAlbum();
                break;
            case R.id.backBtn:
                finish();
                break;
            case R.id.cancelBtn:
                pictureLinear.setVisibility(View.INVISIBLE);
                cancelBtn.setVisibility(View.INVISIBLE);
                backBtn.setVisibility(View.VISIBLE);
                sureBtn.setVisibility(View.INVISIBLE);
                cameraBtn.setVisibility(View.VISIBLE);
                img_show.setVisibility(View.VISIBLE);
                countyTv.setVisibility(View.VISIBLE);
//                weatherIv.setVisibility(View.VISIBLE);
                temperatureTv.setVisibility(View.VISIBLE);
                weatherTv.setVisibility(View.VISIBLE);
                mSurfaceView.setVisibility(View.VISIBLE);
                break;
            case R.id.cameraBtn:
                takePicture();
                break;
            case R.id.sureBtn:
                pictureLinear.setVisibility(View.INVISIBLE);
                cancelBtn.setVisibility(View.INVISIBLE);
                backBtn.setVisibility(View.VISIBLE);
                sureBtn.setVisibility(View.INVISIBLE);
                cameraBtn.setVisibility(View.VISIBLE);
                img_show.setVisibility(View.VISIBLE);
                countyTv.setVisibility(View.VISIBLE);
//                weatherIv.setVisibility(View.VISIBLE);
                temperatureTv.setVisibility(View.VISIBLE);
                weatherTv.setVisibility(View.VISIBLE);
                mSurfaceView.setVisibility(View.VISIBLE);
                savePicture(bitmap);
                break;
            default:
                break;
        }
    }

    /**
     * 初始化surfaceView，即照相机预览界面
     */
    public void initSurfaceView(){
        mSurfaceView.setFocusable(true);
        mSurfaceViewHolder = mSurfaceView.getHolder();//通过SurfaceViewHolder可以对SurfaceView进行管理
        mSurfaceViewHolder.addCallback(new SurfaceHolder.Callback() {
            //SurfaceView被成功创建
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                initCameraAndPreview();
            }
            //SurfaceView被销毁
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                //释放camera
                if (mCameraDevice != null) {
                    mCameraDevice.close();
                    mCameraDevice = null;
                }
            }
            //SurfaceView内容发生改变
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }
        });
    }

    /**
     * 初始化相机和实现相机预览功能
     */
    @TargetApi(19)
    public void initCameraAndPreview() {
        HandlerThread handlerThread = new HandlerThread("My First Camera2");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());
        mainHandler = new Handler(getMainLooper());//用来处理ui线程的handler，即ui线程
        try {
            mCameraId = "" + CameraCharacteristics.LENS_FACING_FRONT;
            mImageReader = ImageReader.newInstance(mSurfaceView.getWidth(), mSurfaceView.getHeight(), ImageFormat.JPEG,/*maxImages*/7);
            mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mainHandler);//这里必须传入mainHandler，因为涉及到了Ui操作
            mCameraManager = (CameraManager) this.getSystemService(Context.CAMERA_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;//按理说这里应该有一个申请权限的过程，但为了使程序尽可能最简化，所以先不添加
            }
            //打开相机，第一个参数指示打开哪个摄像头，第二个参数stateCallback为相机的状态回调接口，第三个参数用来确定Callback在哪个线程执行，为null的话就在当前线程执行
            mCameraManager.openCamera(mCameraId, deviceStateCallback, mHandler);
        } catch (CameraAccessException e) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 设置ImageReader，用来读取拍摄图像的类
     */
    private ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            //关闭相机
            mCameraDevice.close();
            //图像获取
            Image image = reader.acquireNextImage();
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);//将image对象转化为byte，再转化为bitmap
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            if (bitmap != null) {
                bitmap = compressImage(bitmap);
                bitmap = addWater(bitmap);
                img_show.setImageBitmap(bitmap);
                pictureIv.setImageBitmap(bitmap);
            }
            cameraBtn.setVisibility(View.INVISIBLE);
            cancelBtn.setVisibility(View.VISIBLE);
            backBtn.setVisibility(View.INVISIBLE);
            sureBtn.setVisibility(View.VISIBLE);
            img_show.setVisibility(View.INVISIBLE);
            countyTv.setVisibility(View.INVISIBLE);
//            weatherIv.setVisibility(View.INVISIBLE);
            temperatureTv.setVisibility(View.INVISIBLE);
            weatherTv.setVisibility(View.INVISIBLE);
            pictureLinear.setVisibility(View.VISIBLE);
            mSurfaceView.setVisibility(View.INVISIBLE);
            reader.close();
        }
    };

    /**
     * 添加水印
     */
    private Bitmap addWater(Bitmap mBitmap) {
        android.graphics.Bitmap.Config bitmapConfig =
                mBitmap.getConfig();
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        //获取原始图片与水印图片的宽与高
        int mBitmapWidth = mBitmap.getWidth();
        int mBitmapHeight = mBitmap.getHeight();

        DisplayMetrics dm = getResources().getDisplayMetrics();
        float screenWidth = dm.widthPixels;//1080
        float mBitmapWidthF = mBitmapWidth;
        float times = mBitmapWidthF / screenWidth;

        Bitmap mNewBitmap = Bitmap.createBitmap(mBitmapWidth, mBitmapHeight, bitmapConfig);
        Canvas canvas = new Canvas(mNewBitmap);
        //向位图中开始画入MBitmap原始图片
        canvas.drawBitmap(mBitmap, 0, 0, null);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setDither(true); //获取跟清晰的图像采样
        paint.setFilterBitmap(true);//过滤一些
        paint.setTextSize(sp2px(this, 20) * times);
        String text = city;
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        float textW = paint.measureText(text);
        float x = (mBitmapWidth * 3 / 4);
        float textH = -paint.ascent() + paint.descent();
        float y = (mBitmapHeight * 1 / 5)- textH/2;
        canvas.drawText(text, x, y, paint);//mBitmapWidth=3024


//        img_show.setImageResource(backgroundImageId);
//        Bitmap weatherBitmap = ((BitmapDrawable)img_show.getDrawable()).getBitmap();
//        canvas.drawBitmap(weatherBitmap,x+textW, y,paint);
        text = weather.now.temperature+"℃"+weather.now.txt;
        paint.setTextSize(sp2px(this, 20) * times);
        paint.getTextBounds(text, 0, text.length(), bounds);
        textW = paint.measureText(text);
        x = (mBitmapWidth * 3 / 4);
        canvas.drawText(text, x, y+ textH, paint);
//        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.save();
        return mNewBitmap;

    }

    /**
     * 保存图片
     */
    private void savePicture(Bitmap bitmap) {
        try {
            FileOutputStream outStream = null;
            String filePath = Environment.getExternalStorageDirectory().getPath() + File.separator + "testPhoto";
            String fileName = filePath + File.separator + String.valueOf(System.currentTimeMillis()) + ".jpg";
            File file = new File(fileName);
            if (!file.exists()) file.getParentFile().mkdirs();
            outStream = new FileOutputStream(fileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            if (outStream != null) outStream.close();
            // 最后通知图库更新
            CameraActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileName)));
            Toast.makeText(this, "图片已保存至:" + fileName, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 反馈相机工作状态
     */
    private CameraDevice.StateCallback deviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            mCameraDevice = camera;
            try {
                takePreview();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            if (mCameraDevice != null) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            Toast.makeText(CameraActivity.this, "打开摄像头失败", Toast.LENGTH_SHORT).show();
        }
    };


    /**
     * 显示预览界面，进行正式预览
     * @throws CameraAccessException
     */
    public void takePreview() throws CameraAccessException {
        mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        mPreviewBuilder.addTarget(mSurfaceViewHolder.getSurface());
        mCameraDevice.createCaptureSession(Arrays.asList(mSurfaceViewHolder.getSurface(), mImageReader.getSurface()), mSessionPreviewStateCallback, mHandler);
    }

    private CameraCaptureSession.StateCallback mSessionPreviewStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            mSession = session;
            //配置完毕开始预览
            try {
                /**
                 * 设置你需要配置的参数
                 */
                //自动对焦
                mPreviewBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                //打开闪光灯
//                mPreviewBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                //无限次的重复获取图像
                mSession.setRepeatingRequest(mPreviewBuilder.build(), null, mHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            Toast.makeText(CameraActivity.this, "配置失败", Toast.LENGTH_SHORT).show();
        }
    };

    //预览帧数据
    private CameraCaptureSession.CaptureCallback mSessionCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        //拍摄完全完成并且成功，拍摄图像数据可用时回调
        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            mSession = session;
        }
        //拍摄进行中，但拍摄图像数据部分可用时回调
        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
            mSession = session;
        }
        //拍摄失败时回调
        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
        }
    };

    /**
     * 拍照
     */
    public void takePicture() {
        try {
            //创建请求拍照的CaptureRequest
            CaptureRequest.Builder captureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureRequestBuilder.addTarget(mImageReader.getSurface());
            // 自动对焦
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            // 自动曝光
            captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            //获取屏幕的方向
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            CameraCharacteristics cameraCharacteristics = mCameraManager.getCameraCharacteristics(mCameraId);
            //使图片做顺时针旋转
            captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, getJpegOrientation(cameraCharacteristics, rotation));
            CaptureRequest mCaptureRequest = captureRequestBuilder.build();
            mSession.capture(mCaptureRequest, null, mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取图片应该旋转的角度，使图片竖直
     */
    private int getJpegOrientation(CameraCharacteristics c, int deviceOrientation) {
        if (deviceOrientation == android.view.OrientationEventListener.ORIENTATION_UNKNOWN)
            return 0;
        int sensorOrientation = c.get(CameraCharacteristics.SENSOR_ORIENTATION);

        // Round device orientation to a multiple of 90
        deviceOrientation = (deviceOrientation + 45) / 90 * 90;

        // LENS_FACING相对于设备屏幕的方向,LENS_FACING_FRONT相机设备面向与设备屏幕相同的方向
        boolean facingFront = c.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT;
        if (facingFront) deviceOrientation = -deviceOrientation;

        // Calculate desired JPEG orientation relative to camera orientation to make
        // the image upright relative to the device orientation
        int jpegOrientation = (sensorOrientation + deviceOrientation + 360) % 360;

        return jpegOrientation;
    }

    /**
     * 质量压缩方法
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 98;
        while (baos.toByteArray().length / 1024 > 3072) { // 循环判断如果压缩后图片是否大于 3Mb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 2;// 每次都减少2
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * dip转pix
     */
    public static int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 打开相册
     */
    public void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");//选择照片后毁掉onActivityResult方法
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    /**
     * 相册回调
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //获取图片URI
                    img_show.setImageURI(data.getData());
                }
                break;
            default:
        }
    }

    @Override
    public  void onDestroy(){
        super.onDestroy();
    }
}
