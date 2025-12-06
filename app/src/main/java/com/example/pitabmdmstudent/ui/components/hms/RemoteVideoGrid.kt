package com.example.pitabmdmstudent.ui.components.hms

import android.view.View
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun RemoteVideoGrid(remotePeers: List<View>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize()
    ) {
        items(remotePeers) { view ->
            AndroidView(
                factory = { view },
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxSize()
            )
        }
    }
}
