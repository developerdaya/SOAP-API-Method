package com.developerdaya.soap_api_methods.ui
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.developerdaya.soap_api_methods.R
import com.developerdaya.soap_api_methods.api.RetrofitClient
import com.developerdaya.soap_api_methods.api.SoapApi
import com.developerdaya.soap_api_methods.util.SoapRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader

class MainActivity : AppCompatActivity() {
    private lateinit var resultTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        resultTextView = findViewById(R.id.text_result)
        val api = RetrofitClient.retrofit.create(SoapApi::class.java)
        val requestBody = RequestBody.create(
            "text/xml".toMediaTypeOrNull(),
            SoapRequest.getEnvelope())
        val call = api.getCountries(requestBody)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful)
                {
                    val soapResponse   = response.body()?.string() ?: ""
                    resultTextView.text = soapResponse
                }
                else
                {
                    resultTextView.text = "Response error: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                resultTextView.text = "Error: ${t.message}"
            }
        })
    }















}
