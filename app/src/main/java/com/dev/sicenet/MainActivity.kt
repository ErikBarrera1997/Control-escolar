package com.dev.sicenet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dev.sicenet.data.NetworSNRepository
import com.dev.sicenet.factory.LoginViewModelFactory
import com.dev.sicenet.interfaces.LoginScreen
import com.dev.sicenet.interfaces.LoginViewModel
import com.dev.sicenet.network.SICENETWService
import com.dev.sicenet.ui.theme.SicenetTheme
import okhttp3.OkHttpClient
import retrofit2.Retrofit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://sicenet.surguanajuato.tecnm.mx/")
            .client(OkHttpClient())
            .build()

        val service = retrofit.create(SICENETWService::class.java)
        val repository = NetworSNRepository(service)
        val factory = LoginViewModelFactory(repository)

        setContent {
            SicenetTheme {
                val viewModel: LoginViewModel = viewModel(factory = factory)

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


