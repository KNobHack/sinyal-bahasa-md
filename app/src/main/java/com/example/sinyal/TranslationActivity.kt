package com.example.sinyal

import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.example.sinyal.dataclass.Response
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor

import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
class TranslationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_translation)

        val videoView: VideoView = findViewById(R.id.videoView)
        val tvTranslation: TextView = findViewById(R.id.tvTranslation)

        // Mendapatkan URI video dari intent
        val videoUriString = intent.getStringExtra("videoUri")
        val videoUri = Uri.parse(videoUriString)

        // Set URI video ke VideoView
        videoView.setVideoURI(videoUri)

        // Mulai memainkan video
        videoView.start()

        // Tambahkan kode untuk melakukan terjemahan dengan menggunakan API
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val translationResult = uploadVideo(videoUriString)

                // Set hasil terjemahan ke TextView (tvTranslation)
                runOnUiThread {
                    tvTranslation.text = translationResult.prediction
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    tvTranslation.text = "Terjadi kesalahan saat menerjemahkan video."
                }
            }
        }
    }

    private suspend fun uploadVideo(videoUri: String?): Response {
        return withContext(Dispatchers.IO) {
            val videoFile = File(getRealPathFromURI(videoUri ?: ""))

            // Set your desired timeout values in seconds (e.g., 120 seconds for a 2-minute timeout)
            val connectTimeoutSeconds = 120L
            val readTimeoutSeconds = 120L

            val httpClient = OkHttpClient.Builder()
                .connectTimeout(connectTimeoutSeconds, TimeUnit.SECONDS)
                .readTimeout(readTimeoutSeconds, TimeUnit.SECONDS)
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "video",
                    videoFile.name,
                    RequestBody.create("video/mp4".toMediaTypeOrNull(), videoFile)
                )
                .build()

            val request = Request.Builder()
                .url("https://sinyal-bahasa-fastapi-ivhwomwomq-et.a.run.app/translate_video")
                .post(requestBody)
                .build()

            try {
                val response = httpClient.newCall(request).execute()

                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    Gson().fromJson(responseBody, Response::class.java)
                } else {
                    throw IOException("Gagal mendapatkan respons dari server, kode: ${response.code}")
                }
            } catch (e: Exception) {
                throw IOException("Gagal mendapatkan respons dari server", e)
            }
        }
    }


    private fun getRealPathFromURI(uri: String): String {
        val uri = Uri.parse(uri)
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        val path = cursor?.getString(columnIndex ?: 0) ?: ""
        cursor?.close()
        return path
    }

    private fun translateVideo(videoUri: String?): Response {
        val apiUrl =
            "https://sinyal-bahasa-fastapi-ivhwomwomq-et.a.run.app/translate?videoUri=$videoUri"

        val url = URL(apiUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val inputStream = connection.inputStream
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))

            val response = StringBuilder()
            var line: String?

            while (bufferedReader.readLine().also { line = it } != null) {
                response.append(line)
            }

            bufferedReader.close()
            inputStream.close()

            try {
                // Gunakan Gson untuk mengonversi JSON ke objek Response
                return Gson().fromJson(response.toString(), Response::class.java)
            } catch (e: JsonSyntaxException) {
                throw IOException("Gagal mengonversi respons JSON", e)
            }
        } else {
            throw IOException("Gagal mendapatkan respons dari server, kode: $responseCode")
        }
    }
}
