## SOAP API
```
app/
 └── src/
     └── main/
         └── java/
             └── com/
                 └── example/
                     └── soapretrofit/
                         ├── api/
                         │    └── SoapApi.kt
                         ├── model/
                         │    ├── SoapRequest.kt
                         │    └── SoapResponse.kt
                         ├── network/
                         │    └── RetrofitClient.kt
                         └── ui/
                              └── MainActivity.kt
         └── res/
             └── layout/
                 └── activity_main.xml
```

### Step-by-Step Code

#### 1. Add Dependencies
In your `build.gradle` file:
```gradle
dependencies {
    // Retrofit
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'

    // OkHttp for logging
    implementation 'com.squareup.okhttp3:logging-interceptor:4.9.1'
}
```

#### 2. Define SOAP API Interface

##### `api/SoapApi.kt`
```kotlin
package com.example.soapretrofit.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface SoapApi {
    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: http://www.oorsprong.org/websamples.countryinfo/ListOfCountryNamesByName"
    )
    @POST("CountryInfoService.wso")
    fun getCountries(@Body request: String): Call<ResponseBody>
}
```

- **SOAPAction**: The SOAP action header should match the operation we are calling (`ListOfCountryNamesByName`).
- **Body**: We will send the SOAP envelope as the body of the request.

#### 3. Create Retrofit Client

##### `network/RetrofitClient.kt`
```kotlin
package com.example.soapretrofit.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object RetrofitClient {
    private const val BASE_URL = "http://webservices.oorsprong.org/websamples.countryinfo/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .build()
}
```

#### 4. Create SOAP Request Envelope

SOAP requests are sent as XML, so you'll need to build the request as a `String`. Here is a basic SOAP request:

##### `model/SoapRequest.kt`
```kotlin
package com.example.soapretrofit.model

object SoapRequest {
    fun getEnvelope(): String {
        return """
            <?xml version="1.0" encoding="utf-8"?>
            <soap12:Envelope xmlns:soap12="http://www.w3.org/2003/05/soap-envelope">
              <soap12:Body>
                <ListOfCountryNamesByName xmlns="http://www.oorsprong.org/websamples.countryinfo">
                </ListOfCountryNamesByName>
              </soap12:Body>
            </soap12:Envelope>
        """.trimIndent()
    }
}
```

#### 5. Create Main Activity

##### `ui/MainActivity.kt`
```kotlin
package com.example.soapretrofit.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.soapretrofit.R
import com.example.soapretrofit.api.SoapApi
import com.example.soapretrofit.model.SoapRequest
import com.example.soapretrofit.network.RetrofitClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var resultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resultTextView = findViewById(R.id.text_result)

        val api = RetrofitClient.retrofit.create(SoapApi::class.java)
        val call = api.getCountries(SoapRequest.getEnvelope())

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val result = response.body()?.string()
                    resultTextView.text = result
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                resultTextView.text = "Error: ${t.message}"
            }
        })
    }
}
```

#### 6. Design the Layout

##### `res/layout/activity_main.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <TextView
        android:id="@+id/text_result"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="SOAP API Response"
        android:textSize="18sp"
        android:layout_marginBottom="8dp"/>
</LinearLayout>
```

### Key Points

- **API Interface (`SoapApi.kt`)**: Defines the SOAP request with appropriate headers (`Content-Type` and `SOAPAction`).
- **Retrofit Client (`RetrofitClient.kt`)**: Configures Retrofit with logging to see the raw SOAP request and response.
- **Request Envelope (`SoapRequest.kt`)**: Builds the SOAP request envelope as a string.
- **Main Activity (`MainActivity.kt`)**: Sends the SOAP request and displays the response in a `TextView`.
- **XML Layout (`activity_main.xml`)**: Simple layout for showing the response.

### Explanation

- **SOAP Request**: The body is an XML envelope that specifies the SOAP action (`ListOfCountryNamesByName`) without parameters.
- **Response**: The response is received as an XML string, which you can further process or parse using XML parsers (e.g., `XmlPullParser`).
