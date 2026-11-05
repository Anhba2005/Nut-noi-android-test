package com.example.nutnoi

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast

class FloatingWidgetService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: View

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        // 1. Inflate layout của nút nổi
        floatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null)

        // 2. Thiết lập thông số WindowManager để vẽ đè lên màn hình
        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, // Cho phép bấm các app phía dưới
            PixelFormat.TRANSLUCENT
        )

        // Vị trí xuất hiện ban đầu (Góc trên bên trái)
        params.gravity = Gravity.TOP or Gravity.START
        params.x = 100
        params.y = 100

        // 3. Thêm view vào màn hình nền hệ thống
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        windowManager.addView(floatingView, params)

        // 4. Xử lý tương tác: Kéo thả di chuyển và Click vào nút
        val collapsedView = floatingView.findViewById<View>(R.id.collapse_view)
        val closeButton = floatingView.findViewById<ImageView>(R.id.close_btn)

        // Nút tắt icon nổi
        closeButton.setOnClickListener {
            stopSelf() // Tắt service, nút nổi sẽ biến mất
        }

        // Xử lý vuốt/chạm để di chuyển widget
        collapsedView.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f
            private var isMoved = false

            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        isMoved = false
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val dx = (event.rawX - initialTouchX).toInt()
                        val dy = (event.rawY - initialTouchY).toInt()
                        
                        // Nếu dịch chuyển nhiều hơn 10px thì coi như đang kéo thả
                        if (Math.abs(dx) > 10 || Math.abs(dy) > 10) {
                            isMoved = true
                        }

                        params.x = initialX + dx
                        params.y = initialY + dy
                        
                        // Cập nhật vị trí mới của view trên màn hình
                        windowManager.updateViewLayout(floatingView, params)
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        // Nếu không di chuyển xa -> Tính là một cú Click
                        if (!isMoved) {
                            Toast.makeText(this@FloatingWidgetService, "Bạn vừa bấm vào nút nổi!", Toast.LENGTH_SHORT).show()
                        }
                        return true
                    }
                }
                return false
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        // Xóa view khỏi WindowManager khi Service bị hủy
        if (::floatingView.isInitialized) {
            windowManager.removeView(floatingView)
        }
    }
}
