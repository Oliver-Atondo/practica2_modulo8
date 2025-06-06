package com.oliveratondo.practica2_modulo8.ui.viewModels

import com.google.firebase.auth.FirebaseAuth
import com.oliveratondo.practica2_modulo8.auth.*
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { FirebaseAuth.getInstance() }
    single { SecureStorage(androidContext()) }
    single<AuthRepository> { FirebaseAuthRepositoryImpl(get(), get()) }
    viewModel { AuthViewModel(get()) }
}
