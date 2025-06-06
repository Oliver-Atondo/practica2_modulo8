package com.oliveratondo.practica2_modulo8

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.oliveratondo.practica2_modulo8.ui.theme.Practica2_modulo8Theme
import com.oliveratondo.practica2_modulo8.ui.viewModels.AuthViewModel
import com.oliveratondo.practica2_modulo8.ui.viewModels.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.compose.koinViewModel
import org.koin.core.context.GlobalContext.startKoin
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.oliveratondo.practica2_modulo8.ui.viewModels.AuthState
import com.oliveratondo.practica2_modulo8.ui.views.LoginView
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.oliveratondo.practica2_modulo8.api.RetrofitClient
import com.oliveratondo.practica2_modulo8.data.models.Hotel
import com.oliveratondo.practica2_modulo8.data.models.Location
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startKoin {
            androidContext(this@MainActivity)
            modules(appModule)
        }

        enableEdgeToEdge()
        setContent {
            Practica2_modulo8Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RootView(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun GoogleMapView(hotels: List<Hotel>) {
    val context = LocalContext.current
    val viewModel = koinViewModel<AuthViewModel>()

    val initialLocation = hotels.firstOrNull()?.location ?: Location(19.432608, -99.133209)
    val initialLatLng = LatLng(initialLocation.latitude, initialLocation.longitude)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLatLng, 12f)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            val icon = BitmapDescriptorFactory.fromBitmap(
                BitmapFactory.decodeResource(context.resources, R.drawable.hotel_marker)
            )

            hotels.forEach { hotel ->
                val position = LatLng(hotel.location.latitude, hotel.location.longitude)
                Marker(
                    state = MarkerState(position = position),
                    title = hotel.name,
                    snippet = hotel.description,
                    icon = icon
                )
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
    }
}


@Composable
fun RootView(
    viewModel: AuthViewModel = koinViewModel(),
    modifier: Modifier
) {
    val state by viewModel.authState.collectAsState()

    val hotelsState = produceState<List<Hotel>>(initialValue = emptyList()) {
        value = try {
            RetrofitClient.apiService.getHotels()
        } catch (e: Exception) {
            emptyList()
        }
    }

    when {
        state is AuthState.Success || viewModel.isUserLoggedIn() -> {
            GoogleMapView(hotelsState.value)
        }
        else -> {
            LoginView()
        }
    }
}
