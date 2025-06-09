package com.oliveratondo.practica2_modulo8.ui.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.oliveratondo.practica2_modulo8.VideoPlayer
import com.oliveratondo.practica2_modulo8.api.RetrofitClient
import com.oliveratondo.practica2_modulo8.data.models.Instrument
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.res.stringResource
import com.oliveratondo.practica2_modulo8.R

@Composable
fun InstrumentDetailsDialog(
    id: String,
    onDismiss: () -> Unit
) {
    val instrumentState = produceState<Instrument?>(initialValue = null, id) {
        value = try {
            RetrofitClient.apiService.getInstrumentDetails(id.toInt())
        } catch (e: Exception) {
            null
        }
    }

    val instrument = instrumentState.value
    var isImageLoading by remember { mutableStateOf(true) }
    var isVideoLoading by remember { mutableStateOf(true) }

    if (instrument != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                Button(onClick = onDismiss) {
                    Text("Cerrar")
                }
            },
            title = { Text(text = instrument.name) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 600.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(stringResource(R.string.tipo, instrument.type))
                    Text(stringResource(R.string.marca, instrument.brand))
                    Text(stringResource(R.string.modelo, instrument.model))
                    Text(stringResource(R.string.material, instrument.material))
                    Text(
                        stringResource(
                            R.string.ubicaci_n,
                            instrument.location.latitude,
                            instrument.location.longitude
                        ))
                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isImageLoading) {
                            CircularProgressIndicator()
                        }
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = instrument.image,
                                onSuccess = { isImageLoading = false },
                                onError = { isImageLoading = false }
                            ),
                            contentDescription = instrument.name,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isVideoLoading) {
                            CircularProgressIndicator()
                        }
                        VideoPlayer(
                            url = instrument.video,
                            audioUrl = instrument.audio,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            onLoadingChange = { isVideoLoading = it }
                        )
                    }
                }
            }
        )
    }
}
