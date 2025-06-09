package com.oliveratondo.practica2_modulo8

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
import com.oliveratondo.practica2_modulo8.ui.viewModels.AuthState
import com.oliveratondo.practica2_modulo8.ui.views.LoginView
import com.oliveratondo.practica2_modulo8.api.RetrofitClient
import com.oliveratondo.practica2_modulo8.data.models.InstrumentPreview
import com.oliveratondo.practica2_modulo8.ui.views.GoogleMapView

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
fun RootView(
    viewModel: AuthViewModel = koinViewModel(),
    modifier: Modifier
) {
    val state by viewModel.authState.collectAsState()

    val instrumentsListState = produceState<List<InstrumentPreview>>(initialValue = emptyList()) {
        value = try {
            RetrofitClient.apiService.getInstruments()
        } catch (e: Exception) {
            emptyList()
        }
    }

    when {
        state is AuthState.Success || viewModel.isUserLoggedIn() -> {
            GoogleMapView(
                instrumentsListState.value
            )
        }
        else -> {
            LoginView()
        }
    }
}
