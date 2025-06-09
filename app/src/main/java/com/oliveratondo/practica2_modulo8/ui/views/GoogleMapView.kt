package com.oliveratondo.practica2_modulo8.ui.views

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import com.oliveratondo.practica2_modulo8.R
import com.oliveratondo.practica2_modulo8.data.models.InstrumentPreview
import com.oliveratondo.practica2_modulo8.data.models.Location
import com.oliveratondo.practica2_modulo8.ui.viewModels.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel

@Composable
fun GoogleMapView(
    instruments: List<InstrumentPreview>
) {
    val context = LocalContext.current
    val viewModel = koinViewModel<AuthViewModel>()

    val initialLocation = instruments.firstOrNull()?.location ?: Location(19.432608, -99.133209)
    val initialLatLng = LatLng(initialLocation.latitude, initialLocation.longitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLatLng, 12f)
    }

    var selectedInstrument by remember { mutableStateOf<InstrumentPreview?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            instruments.forEach { instrument ->
                val markerIcon by produceState<BitmapDescriptor?>(initialValue = null, instrument.previewImage) {
                    value = getBitmapDescriptorFromUrl(instrument.previewImage, context)
                }

                markerIcon?.let { icon ->
                    val position = LatLng(instrument.location.latitude, instrument.location.longitude)
                    Marker(
                        state = MarkerState(position = position),
                        title = instrument.name,
                        snippet = "$" + (instrument.id.toInt() / 21.5).toString().take(5),
                        icon = icon,
                        onClick = {
                            selectedInstrument = instrument
                            false
                        }
                    )
                }
            }
        }

        Button(
            onClick = { viewModel.logout() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
        ) {
            Text(stringResource(R.string.salir), color = Color.Black)
        }

        selectedInstrument?.let { instrument ->
            InstrumentDetailsDialog(
                id = instrument.id,
                onDismiss = { selectedInstrument = null }
            )
        }
    }
}

suspend fun getBitmapDescriptorFromUrl(url: String, context: Context): BitmapDescriptor? {
    return withContext(Dispatchers.IO) {
        try {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(url)
                .allowHardware(false)
                .build()

            val result = loader.execute(request)
            if (result is SuccessResult) {
                val drawable = result.drawable
                val resizedBitmap = drawable.toBitmap(128, 128)
                BitmapDescriptorFactory.fromBitmap(resizedBitmap)
            } else null
        } catch (e: Exception) {
            null
        }
    }
}