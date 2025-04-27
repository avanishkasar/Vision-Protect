package com.example.visionprotect04

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.visionprotect04.service.VisionProtectService
import com.example.visionprotect04.ui.components.CameraPreview
import com.example.visionprotect04.ui.theme.VisionProtect04Theme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.mlkit.vision.face.Face

class MainActivity : ComponentActivity() {
    private var serviceRunning = false

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            VisionProtect04Theme {
                val context = LocalContext.current
                val permissionsState = rememberMultiplePermissionsState(
                    listOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.FOREGROUND_SERVICE,
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                )
                
                LaunchedEffect(permissionsState.allPermissionsGranted) {
                    if (permissionsState.allPermissionsGranted && 
                        !Settings.canDrawOverlays(context)) {
                        val intent = Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:${context.packageName}")
                        )
                        context.startActivity(intent)
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (!permissionsState.allPermissionsGranted) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "Camera and notification permissions are required",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { permissionsState.launchMultiplePermissionRequest() }) {
                                Text("Grant Permissions")
                            }
                        }
                    } else {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CameraPreview(
                                onFaceDetected = { face: Face, coverage: Float ->
                                    // Handle face detection if needed
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                            
                            Button(
                                onClick = { 
                                    if (serviceRunning) {
                                        stopVisionProtectService()
                                    } else {
                                        startVisionProtectService()
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 32.dp)
                            ) {
                                Text(if (serviceRunning) "Stop Protection" else "Start Protection")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun startVisionProtectService() {
        if (!serviceRunning) {
            val serviceIntent = Intent(this, VisionProtectService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }
            serviceRunning = true
        }
    }

    private fun stopVisionProtectService() {
        if (serviceRunning) {
            stopService(Intent(this, VisionProtectService::class.java))
            serviceRunning = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopVisionProtectService()
    }
}