package com.oliveratondo.practica2_modulo8.ui.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.oliveratondo.practica2_modulo8.ui.viewModels.AuthState
import com.oliveratondo.practica2_modulo8.ui.viewModels.AuthViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.stringResource
import com.oliveratondo.practica2_modulo8.R


@Composable
fun LoginView(
    modifier: Modifier = Modifier
) {
    val viewModel = koinViewModel<AuthViewModel>()

    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val state by viewModel.authState.collectAsState()

    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        ResetPasswordDialog(
            onDismiss = { showDialog = false },
            onSendResetLink = { correo ->
                viewModel.resetPassword(correo)
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LightGray)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.musicexplorer),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = White
        )

        Spacer(modifier = Modifier.height(36.dp))

        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            placeholder = { Text(stringResource(R.string.correo_electr_nico), color = White) },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = White,
                focusedBorderColor = White,
                cursorColor = White,
                focusedTextColor = White,
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it },
            placeholder = { Text(stringResource(R.string.contrase_a), color = White) },
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = White,
                focusedBorderColor = White,
                cursorColor = White,
                focusedTextColor = White,
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.login(email.value, password.value)
            },
            colors = ButtonDefaults.buttonColors(containerColor = White),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.iniciar_sesi_n), color = Black)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                viewModel.register(email.value, password.value)
            },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = White),
            border = BorderStroke(1.dp, White),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.crear_cuenta))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.olvidaste_tu_contrase_a),
            color = Black,
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 8.dp)
                .clickable {
                    showDialog = true
                }
        )

        Spacer(modifier = Modifier.height(24.dp))

        when (state) {
            is AuthState.Loading -> Text(stringResource(R.string.cargando), color = Color.White)
            is AuthState.Success -> Text(stringResource(R.string.acceso_concedido), color = Color.Green)
            is AuthState.Error -> Text("Error: ${(state as AuthState.Error).message}", color = Color.Red)
            else -> {}
        }
    }
}

@Composable
fun ResetPasswordDialog(
    onDismiss: () -> Unit,
    onSendResetLink: (String) -> Unit
) {
    val email = remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(
                onClick = {
                    if (email.value.isNotBlank()) {
                        onSendResetLink(email.value)
                        onDismiss()
                    }
                }
            ) {
                Text(stringResource(R.string.enviar))
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(stringResource(R.string.cancelar))
            }
        },
        title = {
            Text(text = stringResource(R.string.restablecer_contrase_a))
        },
        text = {
            Column {
                Text(text = stringResource(R.string.ingresa_tu_correo_para_recibir_un_enlace_de_recuperaci_n))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    label = { Text(stringResource(R.string.correo_electr_nico)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}
