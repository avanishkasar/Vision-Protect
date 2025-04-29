package com.example.visionprotect04.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.visionprotect04.R

@Composable
fun CameraScreen(
    onStartProtection: () -> Unit,
    onStopProtection: () -> Unit,
    isServiceRunning: Boolean
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                if (isServiceRunning) {
                    onStopProtection()
                } else {
                    onStartProtection()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isServiceRunning) MaterialTheme.colorScheme.error 
                else MaterialTheme.colorScheme.primary
            ),
            shape = MaterialTheme.shapes.large
        ) {
            Text(
                text = stringResource(
                    if (isServiceRunning) R.string.stop_protection 
                    else R.string.start_protection
                ),
                fontSize = 20.sp
            )
        }
    }
} 