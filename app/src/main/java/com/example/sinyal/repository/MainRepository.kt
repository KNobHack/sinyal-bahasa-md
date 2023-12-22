package com.example.sinyal.repository


import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sinyal.LoginActivity
import com.example.sinyal.MainActivity
import com.example.sinyal.api.APIConfig
import com.example.sinyal.api.APIService
import com.example.sinyal.dataclass.Errors
import com.example.sinyal.dataclass.LoginAccount
import com.example.sinyal.dataclass.RegisterDataAccount
import com.example.sinyal.dataclass.ResponseDetail
import com.example.sinyal.dataclass.ResponseLogin
import com.example.sinyal.db.EventDatabase
import com.example.sinyal.db.EventDetail
import com.example.sinyal.wrapEspressoIdlingResource
import com.google.gson.Gson
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainRepository(
    private val storyDatabase: EventDatabase,
    private val apiService: APIService
) {

    private val _loading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _loading

    private val _userLogin = MutableLiveData<ResponseLogin>()
    var userlogin: LiveData<ResponseLogin> = _userLogin

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    public val _responStatus = MutableLiveData<Int>()
    val rStatus: LiveData<Int> = _responStatus

    private var _stories = MutableLiveData<List<EventDetail>>()
    var stories: LiveData<List<EventDetail>> = _stories


    fun getResponseLogin(loginDataAccount: LoginAccount,
                         onResponse: (Response<ResponseLogin>) -> Unit,
                         onFailure: (Throwable) -> Unit
                         ) {
        wrapEspressoIdlingResource {
            _loading.value = true
            val api = APIConfig.getApiService().loginUser(loginDataAccount)
            api.enqueue(object : Callback<ResponseLogin> {
                override fun onResponse(
                    call: Call<ResponseLogin>,
                    response: Response<ResponseLogin>

                ) {
                    _loading.value = false
                    val responseBody = response.body()
                    onResponse(response)

                    if (response.isSuccessful) {
                        _userLogin.value = responseBody!!

                    } else {
                        when (response.code()) {
                            401 -> _message.value =
                                "Email atau password yang anda masukan salah, silahkan coba lagi"
                            408 -> _message.value =
                                "Koneksi internet anda lambat, silahkan coba lagi"
                            else -> {
                                _message.value =
                                    "Gagal login. Kode HTTP: ${response.code()}" // Perubahan: Pesan kesalahan HTTP
                                val errorBody = response.errorBody()?.string()
                                try {
                                    val gson = Gson()
                                    val errors = gson.fromJson(errorBody, Errors::class.java)
                                    // Lakukan sesuatu dengan objek Errors, contohnya mencetak ke Logcat
                                    Log.e("Gagal Login", "Response Keasalahan: ${gson.toJson(errors)}")
                                } catch (e: Exception) {
                                    // Jika tidak dapat mengonversi ke objek Errors, tetap mencetak body aslinya
                                    Log.e("Gagal Login", "Response Keasalahan: $errorBody")
                                }

                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {

                    onFailure(t)
                    _loading.value = false
                    val errorMessage = "Gagal login. ${t.message}"
                    _message.value = errorMessage

                    Log.e("Gagal Bjir", errorMessage)

                }

            })
        }
    }

    fun getResponseRegister(registDataUser: RegisterDataAccount) {
        wrapEspressoIdlingResource {
            _loading.value = true
            val api = APIConfig.getApiService().registUser(registDataUser)
            api.enqueue(object : Callback<ResponseDetail> {
                override fun onResponse(
                    call: Call<ResponseDetail>,
                    response: Response<ResponseDetail>
                ) {
                    _loading.value = false
                    if (response.isSuccessful) {
                        _message.value = "Yeay akun berhasil dibuat"
                    } else {
                        when (response.code()) {
                            400 -> _message.value =
                                "Email yang anda masukan sudah terdaftar, silahkan coba lagi"
                            408 -> _message.value =
                                "Koneksi internet anda lambat, silahkan coba lagi"
                            else -> _message.value = "Gagal login. Kode HTTP: ${response.code()}" // Perubahan: Pesan kesalahan HTTP
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseDetail>, t: Throwable) {
                    _loading.value = false
                    val errorMessage = "Gagal login. ${t.message}"
                    _message.value = errorMessage

                    // Mencetak pesan kesalahan ke Logcat
                    Log.e("Hello", errorMessage)
                }

            })
        }
    }





}