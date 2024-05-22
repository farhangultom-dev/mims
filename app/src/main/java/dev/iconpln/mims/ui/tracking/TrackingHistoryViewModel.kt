package dev.iconpln.mims.ui.tracking

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.iconpln.mims.R
import dev.iconpln.mims.data.remote.response.DetailTrackingHistoryResponse
import dev.iconpln.mims.data.remote.response.TrackingHistoryResponse
import dev.iconpln.mims.data.remote.service.ApiConfig
import dev.iconpln.mims.utils.Helper
import dev.iconpln.mims.utils.SharedPrefsUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TrackingHistoryViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _trackingResponse = MutableLiveData<TrackingHistoryResponse>()
    val trackingResponse: LiveData<TrackingHistoryResponse> = _trackingResponse

    private val _detailTrackingHistoryResponse = MutableLiveData<DetailTrackingHistoryResponse>()
    val detailTrackingHistoryResponse: LiveData<DetailTrackingHistoryResponse> =
        _detailTrackingHistoryResponse

    fun getTrackingHistory(sn: String, ctx: Context) {
        _isLoading.value = true
        val apiService = ApiConfig.getApiService(ctx)
        CoroutineScope(Dispatchers.IO).launch {
            val response = apiService.getTrackingHistory(sn)
            withContext(Dispatchers.Main) {
                try {
                    if (response.isSuccessful) {
                        _isLoading.value = false
                        val isLoginSso = SharedPrefsUtils.getIntegerPreference(
                            ctx,
                            dev.iconpln.mims.utils.Config.IS_LOGIN_SSO, 0
                        )

                        if (response.body()?.datas.isNullOrEmpty()) {
                            Toast.makeText(ctx, response.body()?.message, Toast.LENGTH_SHORT).show()
                        } else {
                            when (response.body()?.status) {
                                dev.iconpln.mims.utils.Config.KEY_SUCCESS -> {
                                    val result = response.body()
                                    _trackingResponse.postValue(result!!)
                                    _isLoading.value = false
                                }

                                dev.iconpln.mims.utils.Config.KEY_FAILURE -> {
                                    if (response.body()?.message == dev.iconpln.mims.utils.Config.DO_LOGOUT) {
                                        Helper.logout(ctx, isLoginSso)
                                        Toast.makeText(
                                            ctx,
                                            ctx.getString(R.string.session_kamu_telah_habis),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else Toast.makeText(
                                        ctx,
                                        response.body()?.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    } else {
                        _isLoading.value = false
                        Toast.makeText(ctx, "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}