package com.example.qrcode.functions.scanFunction

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Camera
import android.net.Uri
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.qrcode.R
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.util.Util
import com.example.qrcode.activity.BaseFragment
import com.example.qrcode.activity.ScanItemDisplayActivity
import com.example.qrcode.databinding.ActivityScanItemDisplayBinding
import com.example.qrcode.databinding.FragmentScanBinding
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

//扫描Fragment->负责UI交互，权限申请，生命周期绑定等
class ScanFragment: BaseFragment<FragmentScanBinding>(FragmentScanBinding::inflate) {
    //定义控件变量
    private val viewModel: ScanViewModel by lazy { ViewModelProvider(this)[ScanViewModel::class.java]}
    private lateinit var cameraExecutor: ExecutorService //定义相机专属单线程执行器，用于处理异步图像分析
    private var camera: androidx.camera.core.Camera? = null                  //Camera对象引用，用于控制相机闪光灯和缩放
    //定义相册选择启动器 ->用户选择一个相机后会触发这个回调
    private var pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { processImageFromUri(it) }
        }
    //定义权限请求启动器
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGrant ->
            if (isGrant){
                startCamera()
            }else{
            Toast.makeText(requireContext(),getString(R.string.toast_require_camera_permission),
                Toast.LENGTH_SHORT).show()
        }
        }
    override fun initData() {}

    override fun initView() {
        cameraExecutor = Executors.newSingleThreadExecutor()  //单线程线程池 ->专门用来处理ImageAnalysis
        checkAndStartCamera() //检查权限然后开启相机实时扫描
        initClickListener()  //初始化监听器
        observeViewModel()  //观察数据变化
    }
    //检查权限然后决定是否扫描
    private fun checkAndStartCamera(){
        val cameraPermission = Manifest.permission.CAMERA
        //情况一:用户有相机权限，直接开启相机与二维码解析
        if (ContextCompat.checkSelfPermission(requireContext(),cameraPermission) == PackageManager.PERMISSION_GRANTED){
            startCamera()
        }else{ //情况二：用户没有权限，向系统请求相机权限
            requestPermissionLauncher.launch(cameraPermission)
        }
    }
    //初始化监听事件的点击事项
    private fun initClickListener() {
        binding.btnFlashlight.setOnClickListener { viewModel.toggleFlashState()} //切换闪光灯状态
        binding.btnGallery.setOnClickListener { pickImageLauncher.launch("image/*") }//相册图标绑定到选择图片
        binding.btnBatch.setOnClickListener { Toast.makeText(requireContext(),getString(R.string.scan_page_batch_hint), Toast.LENGTH_SHORT).show() }
        binding.btnAdd.setOnClickListener { //点击增加/减少缩放比例
            val currentRatio = viewModel.zoomRatio.value ?: 0f
            viewModel.updateZoomState(currentRatio + 0.1f)
        }
        binding.btnReduce.setOnClickListener {
            val currentRation = viewModel.zoomRatio.value ?: 0f
            viewModel.updateZoomState(currentRation - 0.1f)
        }
        binding.layoutProgressBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, isFromUser: Boolean) {
                if (isFromUser){
                    viewModel.updateZoomState(progress/100f) //将滑动控件缩放100倍来确定缩放比例
                }
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}

        })
    }
    //观察ViewModel数据变化，刷新UI状态
    private fun observeViewModel() {
        //观察扫描状态，如果扫描成功并保存就跳转到结果页
        viewModel.navigateToResult.observe(viewLifecycleOwner){ pair ->
            val (historyRecord,historyRecordId) = pair
            val intent = Intent(requireContext(), ScanItemDisplayActivity::class.java).apply {
                putExtra("EXTRA_SCAN_TYPE",historyRecord.category.name)
                putExtra("EXTRA_SCAN_CONTENT",historyRecord.content)
                putExtra("EXTRA_SCAN_ID",historyRecordId)
            }
            startActivity(intent)
        }
        //观察闪光灯状态,并且切换选中状态
        viewModel.flashState.observe(viewLifecycleOwner){ison ->
            camera?.cameraControl?.enableTorch(ison)
            binding.btnFlashlight.isSelected = ison
        }
        //观察缩放比例状态，更新缩放比例，并且同步更新滑动条位置
        viewModel.zoomRatio.observe(viewLifecycleOwner){ratio ->
            camera?.cameraControl?.setLinearZoom(ratio)
            binding.layoutProgressBar.progress = (ratio * 100).toInt()
        }
    }
    //初始化CameraX摄像头 ->CameraX是用例驱动的，需要显示用例或者隐式用例
    private fun startCamera() {
        //1，获取相机提供者的异步实例
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            //2,拿到真正的相机对象cameraProvider ->用于将相机生命周期绑定到生命周期所有者上
            val cameraProvider = cameraProviderFuture.get()
               //2.1创建预览用例 ->将预览视图绑定到Preview视图上
            val preview = Preview.Builder().build().also{
                it.setSurfaceProvider(binding.previewScan.surfaceProvider)
            }
                //2.2创建分析用例 ->ImageAnalysis将获取的每一帧图像处理解析
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)//设置获取最后一帧图像内容避免重复跳转
                .build().also {
                    it.setAnalyzer(cameraExecutor){imageProxy ->  //设置分析器，告诉CameraX我们在cameraExecutor(线程池中来处理帧图像内容)
                        processImageProxy(imageProxy)
                    }
                }
            //3,默认选择后置摄像头
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            //4,先解绑所有用例，然后再绑定Preview(预览用例)和分析用例(imageAnalysis)到相机的生命周期上
            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(this,cameraSelector,preview,imageAnalysis)
            }catch (e: Exception){
                Toast.makeText(requireContext(),getString(R.string.toast_camera_boot_failed), Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    //处理动态相机抛出的每一帧图像,imageProxy代表原始数据包裹
    private fun processImageProxy(imageProxy: ImageProxy) {
        val scanner = BarcodeScanning.getClient()
        if (viewModel.isProcessing == true){//如已经有待处理图帧，直接抛弃后续所有帧
            imageProxy.close()
            return
        }
        //1,从imageProxy包裹中取出需要的mediaImage资源
        val mediaImage = imageProxy.image ?:run {
            imageProxy.close()
            return
        }
        //2,将获取的ImageProxy包装成mlkit能识别的InputImage格式
        val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        //3,扫描引擎解析图片
        scanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                if (barcodes.isNotEmpty()){
                    val result = barcodes[0].rawValue ?:""
                    requireActivity().runOnUiThread {
                        viewModel.processScanResult(result)
                    }
                }
            }.addOnCompleteListener {
                imageProxy.close() //4，扫描结束后，要释放imageProxy的所有帧资源，释放内存
            }
    }

    //处理从相册获取的静态二维码图片
    private fun processImageFromUri(uri: Uri) {
        try {
            val scanner = BarcodeScanning.getClient()
            val image = InputImage.fromFilePath(requireContext(), uri) //将图片文件转化为mlkit能识别的类型
            //开始扫描图片
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                  if (barcodes.isNotEmpty()){
                      //识别成功，取第一项识别的结果
                      barcodes[0].rawValue?.let { viewModel.processScanResult(it) }
                  }else{ //识别失败，弹出图片无二维码提示
                      Toast.makeText(requireContext(),getString(R.string.error_qr_image_not_found), Toast.LENGTH_SHORT).show()
                  }
                }
                .addOnFailureListener {e->
                    Toast.makeText(requireContext(),"Scan failed:${e.message}", Toast.LENGTH_SHORT).show()
                }
        }catch (e: Exception){
            Toast.makeText(requireContext(),getString(R.string.error_loading_img), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        viewModel.resetProcessing()//每次重启页面刷新重置相机的状态锁
        super.onResume()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden){
            viewModel.resetProcessing()//每次进入扫描界面重置处理锁
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.close() //退出页面时关闭线程池防止内存泄漏
    }
}