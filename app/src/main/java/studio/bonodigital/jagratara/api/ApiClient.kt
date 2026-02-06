package studio.bonodigital.jagratara.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private const val BASE_URL = "https://siagaai-api-hxwuxrccbn.ap-southeast-1.fcapp.run/"

    private val okHttpClient =
        OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).build()

    val api: JagrataraApi by lazy {
        Retrofit.Builder().baseUrl(BASE_URL).client(okHttpClient).addConverterFactory(GsonConverterFactory.create()).build()
            .create(JagrataraApi::class.java)
    }
}