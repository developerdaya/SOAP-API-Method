package com.developerdaya.soap_api_methods.api

import okhttp3.RequestBody
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
    fun getCountries(@Body requestBody: RequestBody): Call<ResponseBody>
}
