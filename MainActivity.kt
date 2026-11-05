package com.example.nutnoi

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val DRAW_OVER_OTHER_APPS_REQUEST_CODE = 1234

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Giả định bạn có 1 nút bấm trong activity_main.xml tên là btnStartService
        val btnStart = findViewById<Button>(R.id.btnStartService)
        btnStart.setOnClickListener {
            checkPermissionAndStartService()
        }
    }

    private fun checkPermissionAndStartService() {
        // Nếu Android dưới M (6.0), quyền được cấp tự động lúc cài đặt
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                // Chưa có quyền -> Mở màn hình cài đặt hệ thống để user bật
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivityForResult(intent, DRAW_OVER_OTHER_APPS_REQUEST_CODE)
            } else {
                // Đã có quyền -> Chạy service hiển thị nút nổi
                startFloatingWidgetService()
            }
        } else {
            startFloatingWidgetService()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DRAW_OVER_OTHER_APPS_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    startFloatingWidgetService()
                } else {
                    Toast.makeText(this, "Bạn cần cấp quyền để tính năng hoạt động!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun startFloatingWidgetService() {
        startService(Intent(this, FloatingWidgetService::class.java))
        finish() // Đóng app sau khi bật nút nổi (tùy chọn)
    }
}
