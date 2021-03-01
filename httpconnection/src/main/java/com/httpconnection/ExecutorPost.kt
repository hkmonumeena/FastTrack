package com.e.mylibrary

import org.json.JSONObject
import java.io.File
import java.net.Authenticator

interface ExecutorPost {
    fun url(url:String)
    fun setContentType(setTypeOfHttp:String="application/x-www-form-urlencoded")
    fun jsonBody(jsonObject: JSONObject)
    fun authenticatHeader(username:String, password:String)
    fun headerParameter(listHeader: MutableMap<String, String>)
    fun bodyParameter(listBody: MutableMap<String, String>)

}