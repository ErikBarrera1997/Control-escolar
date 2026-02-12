package com.dev.sicenet

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.dev.sicenet.data.NetworSNRepository
import com.dev.sicenet.factory.LoginViewModelFactory
import com.dev.sicenet.interfaces.AppNavHost
import com.dev.sicenet.network.SICENETWService
import com.dev.sicenet.ui.theme.SicenetTheme
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.net.CookieManager
import java.net.CookiePolicy

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val cookieManager = CookieManager().apply {
            setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        }

        val client = OkHttpClient.Builder()
            .cookieJar(JavaNetCookieJar(cookieManager))
            .addInterceptor(logging)
            .build()

        //aqui esta el retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://sicenet.surguanajuato.tecnm.mx/")
            .client(client)
            .build()

        val service = retrofit.create(SICENETWService::class.java)
        val repository = NetworSNRepository(service)

        val loginFactory = LoginViewModelFactory(repository)

        setContent {
            SicenetTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavHost(
                        navController = navController,
                        loginFactory = loginFactory,
                        repository = repository,
                        innerPadding = innerPadding
                    )
                }
            }
        }
    }
}





