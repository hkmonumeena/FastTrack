package com.e.mylibrary

import android.util.Base64
import android.util.Log
import androidx.lifecycle.Lifecycle
import com.google.gson.Gson
import com.monumeena.fastrack.exception.ExecutorException
import com.monumeena.fastrack.exception.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.net.URLEncoder

object Post : ExecutorPost {

    private var pathURL: String? = null
    private var getBodyParameter: MutableMap<String, String> = mutableMapOf()
    private var getMultipartParameter: MutableMap<String, File> = mutableMapOf()
    private var getAuthenticatHeader: String? = null
    private var getHeaderParameter: MutableMap<String, String> = mutableMapOf()
    private var jsonBody = JSONObject()
    private var bodyparams = JSONObject()
    private var getsetContentType: String? = "application/x-www-form-urlencoded"
    private var checkBodytype: String? = null


    override fun url(url: String) {
        pathURL = url
    }

    override fun setContentType(setTypeOfHttp: String) {
        getsetContentType = setTypeOfHttp
    }

    override fun jsonBody(jsonObject: JSONObject) {
        checkBodytype = "jsonBody"
        jsonBody = jsonObject

    }


    override fun authenticatHeader(username: String, password: String) {
        getAuthenticatHeader = username.plus(password)
        var encodeAuth: String? = null
        val auth: String = "$username:$password"
        val data = auth.toByteArray()
        encodeAuth = "Basic " + Base64.encodeToString(data, Base64.DEFAULT)
        getHeaderParameter["Authorization"] = encodeAuth
    }

    override fun headerParameter(listHeader: MutableMap<String, String>) {
        getHeaderParameter = listHeader

    }

    override fun bodyParameter(listBody: MutableMap<String, String>) {
        checkBodytype = "bodyParameter"
        for ((key, value) in listBody) {
            bodyparams.put(key, value)


        }
    }


    fun executor(proceed: (result: Result?, exception: ExecutorException?) -> Unit) {
        var httpURlConnection: HttpURLConnection? = null
        var writer: BufferedWriter? = null
        var os: OutputStream? = null

        GlobalScope.launch(Dispatchers.IO) {
            try {
                httpURlConnection = URL(pathURL).openConnection() as HttpURLConnection
                httpURlConnection?.requestMethod = "POST"
                httpURlConnection?.setRequestProperty("Connection", "Keep-Alive");
                httpURlConnection?.setRequestProperty("Cache-Control", "no-cache");
                httpURlConnection?.setRequestProperty("Content-Type", getsetContentType)
                httpURlConnection?.setRequestProperty("Accept", "*/*");
                for ((key, value) in getHeaderParameter.entries) {
                    httpURlConnection?.setRequestProperty(key, value)
                }
                httpURlConnection?.readTimeout = 60000
                httpURlConnection?.connectTimeout = 60000
                httpURlConnection?.doInput = true
                httpURlConnection?.doOutput = true
                os = httpURlConnection?.outputStream
                if (checkBodytype == "bodyParameter") {
                    writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
                    writer?.write(encodeParams(bodyparams))
                    writer?.flush()
                    writer?.close()
                    os?.close()
                } else if (checkBodytype == "jsonBody") {
                    writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
                    writer?.write(jsonBody.toString())
                    writer?.flush()
                    writer?.close()
                    os?.close()
                }

                httpURlConnection?.connect()
                val data = httpURlConnection?.inputStream?.bufferedReader()?.readText()
                httpURlConnection?.disconnect()
         val job =      GlobalScope.launch(Dispatchers.Main) {
                    if (httpURlConnection?.responseCode == 200) {
                        proceed.invoke(
                                Result(
                                        data,
                                        httpURlConnection?.responseCode!!,
                                        httpURlConnection?.responseMessage
                                ), null
                        )

                    }
                }

                job.cancel()

            } catch (se: SocketTimeoutException) {
                val `in` = InputStreamReader(httpURlConnection?.errorStream)
                var stringBuilder = StringBuilder()
                val bufferedReader = BufferedReader(`in`)
                if (bufferedReader != null) {
                    var cp: Int
                    while (bufferedReader.read().also { cp = it } != -1) {
                        stringBuilder.append(cp.toChar())
                    }
                    bufferedReader.close()
                }
                `in`.close()
                GlobalScope.launch(Dispatchers.Main) {
                    val executorException = ExecutorException(
                            httpURlConnection?.responseCode!!, httpURlConnection?.responseMessage,
                            "SocketTimeoutException $se", stringBuilder.toString()
                    )
                    proceed.invoke(null, executorException)
                    httpURlConnection?.disconnect()
                }

            } catch (e: IOException) {
                val `in` = InputStreamReader(httpURlConnection?.errorStream)
                var stringBuilder = StringBuilder()
                val bufferedReader = BufferedReader(`in`)
                if (bufferedReader != null) {
                    var cp: Int
                    while (bufferedReader.read().also { cp = it } != -1) {
                        stringBuilder.append(cp.toChar())
                    }
                    bufferedReader.close()
                }
                `in`.close()
val job = GlobalScope.launch(Dispatchers.Main) {
                    val executorException = ExecutorException(
                            httpURlConnection?.responseCode!!, httpURlConnection?.responseMessage,
                            "IOException $e", stringBuilder.toString()
                    )
                    proceed.invoke(
                            null,
                            executorException
                    )

                }



                job.cancel()

            } catch (e: java.lang.Exception) {
                val `in` = InputStreamReader(httpURlConnection?.errorStream)
                var stringBuilder = StringBuilder()
                val bufferedReader = BufferedReader(`in`)
                var cp: Int
                while (bufferedReader.read().also { cp = it } != -1) {
                    stringBuilder.append(cp.toChar())
                }
                bufferedReader.close()
                `in`.close()
         val  job =      GlobalScope.launch(Dispatchers.Main) {
                    val executorException = ExecutorException(
                            httpURlConnection?.responseCode!!, httpURlConnection?.responseMessage,
                            " java.lang.Exception $e", stringBuilder.toString()
                    )
                    proceed.invoke(
                            null,
                            executorException
                    )
                }

                job.cancel()

            } finally {
                if (httpURlConnection != null) {
                    httpURlConnection?.disconnect();
                }

                if (writer != null) {
                    try {
                        writer?.close()
                    } catch (ex: IOException) {
                        Log.e("Executor writer error", "Executor: " + ex)
                    }
                }

            }

        }

    }


    private fun encodeParams(params: JSONObject): String? {
        val result = StringBuilder()
        var first = true
        val itr = params.keys()
        while (itr.hasNext()) {
            val key = itr.next()
            val value = params[key]
            if (first) first = false else result.append("&")
            result.append(URLEncoder.encode(key, "UTF-8"))
            result.append("=")
            result.append(URLEncoder.encode(value.toString(), "UTF-8"))
        }
        return result.toString()
    }

    inline fun <reified T : Any> createModelFromClass(json: String): T {
        return Gson().fromJson(json, T::class.java)
    }

}