package dev.iconpln.mims.data.remote.service

import android.content.Context
import android.util.JsonReader
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dev.iconpln.mims.BuildConfig
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object ApiConfig {
//    private val BASE_URL = "http://10.14.152.193:30880"
    private val BASE_ULR_PROD = "https://mims.pln.co.id"
//    private val BASE_URL = "http://10.14.69.34:3000"
    const val AGO_ENDPOINT = "/mobile/proxy/ago/"

    fun getApiService(context: Context): ApiService {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())
        val sslSocketFactory = sslContext.socketFactory

        val certificatePinner = CertificatePinner.Builder()
//            .add(BuildConfig.DOMAIN_URL, "sha256/nu4ZFVw4BXvDK4qxHyFUgdOF6MwHg0M7EdMVJV+W2kQ=")
//            .add(BuildConfig.DOMAIN_URL, "sha256/aZeR2aS2g2bguhPzWzTQqUq9OCf4tXk7VWrF6R/zJkM=")
//            .add(BuildConfig.DOMAIN_URL, "sha256/r/mIkG3eEpVdm+u/ko/cwxzOMo1bk4TyHIlByibiA5E=")
            .build()
        val loggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
        val client: OkHttpClient =
            if (BuildConfig.DEBUG)
                OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(AuthInterceptor(context))
                    .addInterceptor(AGOAuthInterceptor())
                    .certificatePinner(certificatePinner)
                    .connectTimeout(10, TimeUnit.MINUTES)
                    .readTimeout(10, TimeUnit.MINUTES)
                    .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                    .hostnameVerifier { _, _ -> true }
                    .build()
            else
                OkHttpClient.Builder()
                    .addInterceptor(AuthInterceptor(context))
                    .addInterceptor(AGOAuthInterceptor())
                    .certificatePinner(certificatePinner)
                    .connectTimeout(10, TimeUnit.MINUTES)
                    .readTimeout(10, TimeUnit.MINUTES)
                    .build()


        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(ApiService::class.java)
    }

    val hostUrl: String
        get() = BuildConfig.BASE_URL
    fun sendPenerimaanPerson(): String {
        return "$hostUrl/mobile/reports/v2/sendReportPenerimaanPerson"
    }

    fun updatePenerima(): String {
        return "$hostUrl/mobile/reports/updatePenerima"
    }

    fun sendPenerimaan(): String {
        return "$hostUrl/mobile/reports/v4/sendReportPenerimaan"
    }

    fun sendPemeriksaanPerson(): String {
        return "$hostUrl/mobile/reports/sendReportPemeriksaanPerson"
    }

    fun sendPemeriksaan(): String {
        return "$hostUrl/mobile/reports/v3/sendReportPemeriksaan"
    }

    fun sendTerimaMonitoringComplaint(): String {
        return "$hostUrl/mobile/reports/v3/sendReportTerimaMonitoringComplaint"
    }

    fun sendRejectMonitoringComplaint(): String {
        return "$hostUrl/mobile/reports/v3/sendReportRejectMonitoringComplaint"
    }

    fun sendComplaint(): String {
        return "$hostUrl/mobile/reports/v4/sendReportComplaint"
    }

    fun sendComplaintPemeriksaan(): String {
        return "$hostUrl/mobile/reports/v4/sendReportComplaintPemeriksaan"
    }

    fun sendRating(): String {
        return "$hostUrl/mobile/reports/v3/sendReportRating"
    }

    fun sendMonkitoringPermintaan(): String {
        return "$hostUrl/mobile/reports/sendReportMonitoringPermintaan2"
    }

    fun sendReportPenerimaanUlpDetail(): String {
        return "$hostUrl/mobile/reports/penerimaan/updateSelesaiDetail"
    }

    fun sendReportPenerimaanUlpSelesai(): String {
        return "$hostUrl/mobile/reports/penerimaan/updateSelesai"
    }

    fun sendReportPemakaianUlpSelesai(): String {
        return "$hostUrl/mobile/reports/pemakaian/updateSelesai"
    }

    fun sendReportPemakai(): String {
        return "$hostUrl/mobile/reports/pemakaian/insertPemakai"
    }

    fun sendReportPetugasPemeriksaanUlp(): String {
        return "$hostUrl/mobile/reports/penerimaanUlp/updatePetugasPemeriksaanUlp"
    }

    fun sendReportPetugasPenerimaanUlp(): String {
        return "$hostUrl/mobile/reports/penerimaanUlp/updatePetugasPenerimaanUlp"
    }
}