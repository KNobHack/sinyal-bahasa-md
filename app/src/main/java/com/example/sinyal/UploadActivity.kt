package com.example.sinyal

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
class UploadActivity : AppCompatActivity() {
    private val REQUEST_VIDEO_CODE = 1001 // Kode permintaan untuk hasil aktivitas pemilihan video

    val httpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)


        val btnUpload: Button = findViewById(R.id.btnsUpload)
        btnUpload.setOnClickListener {
            // Memilih video dari penyimpanan perangkat

            val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_VIDEO_CODE)
        }
    }



    private fun uploadVideo(videoUri: Uri) {
        val videoFile = File(getRealPathFromURI(videoUri))

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("video", videoFile.name, RequestBody.create("video/mp4".toMediaTypeOrNull(), videoFile))
            .build()

        val request = Request.Builder()
            .url("https://sinyal-bahasa-fastapi-ivhwomwomq-et.a.run.app/translate_video")
            .post(requestBody)
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = httpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    // Handle respons dari server, bisa menampilkan toast atau melakukan aksi lainnya
                } else {
                    // Handle kesalahan upload, misalnya menampilkan toast error
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle kesalahan upload, misalnya menampilkan toast error
            }
        }
    }


    private fun getRealPathFromURI(uri: Uri): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        val path = cursor?.getString(columnIndex ?: 0) ?: ""
        cursor?.close()
        return path
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_VIDEO_CODE && resultCode == RESULT_OK) {
            // Mendapatkan URI video yang dipilih
            val videoUri: Uri? = data?.data
            videoUri?.let { uploadVideo(it) }

            // Intent ke ActivityKedua dengan mengirim URI video

            val intent = Intent(this, TranslationActivity::class.java)
            intent.putExtra("videoUri", videoUri.toString())
            startActivity(intent)
        }
    }
}