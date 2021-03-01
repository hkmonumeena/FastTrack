package com.e.mylibrary

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.DataPart
import com.github.kittinunf.fuel.core.FileDataPart
import com.github.kittinunf.fuel.core.extensions.authentication
import com.google.gson.Gson
import com.monumeena.fastrack.exception.DataClassTest
import com.monumeena.fastrack.exception.ExecutorException
import com.monumeena.fastrack.exception.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

object Upload : ExecutorUpload {

    private val LINE_FEED = "\r\n"
    private val maxBufferSize = 1024 * 1024
    private val charset = "UTF-8"

    private var pathURL: String? = null
    private var getBodyParameter: MutableMap<String, String> = mutableMapOf()
    private var setMultipartParameter: ArrayList<File> = arrayListOf()
    private var getAuthenticatHeader: String? = null
    private var getHeaderParameter: MutableMap<String, String> = mutableMapOf()
    private var setFileNameandType: MutableMap<String, String> = mutableMapOf()
    private var jsonBody = JSONObject()
    private var bodyparams = JSONObject()
    private var getsetContentType: String? = "application/x-www-form-urlencoded"
    private var checkBodytype: String? = null

    private var myfieldName: String? = null
    private var myfileName: String? = null
    private var myfileType: String? = null
    private var myuploadFile: File? = null
    private var checkSingleOrDoubleFile: Int = 0
    private var checkSingleFile: Int = 0
    private var objData: String? = null
    private val singleFiles: MutableMap<String, File> = mutableMapOf()
    private val singleFilesType = arrayListOf<String>()
    private var checkAuth = 0  // if 0 that means none  if 1 that means bearer if 2 means basic
    private var username: String? = null
    private var password: String? = null
    //for fuel

    private val dataParts: MutableCollection<DataPart> = mutableListOf()

    override fun url(url: String) {
        pathURL = url
    }


    override fun authenticatHeader(username: String, password: String) {
        checkAuth = 2
        this.username = username
        this.password = password
    }

    override fun headerParameter(listHeader: MutableMap<String, String>) {
        getHeaderParameter = listHeader

    }

    override fun bodyParameter(listBody: MutableMap<String, String>) {
        checkBodytype = "bodyParameter"
        getBodyParameter = listBody

    }


    override fun addFileList(
        getMultipartParameter: ArrayList<File>,
        paramName: String
    ) {
        checkSingleOrDoubleFile = 1
        setMultipartParameter = getMultipartParameter
        myfieldName = paramName
        getMultipartParameter.forEach {
            dataParts.add(FileDataPart(it, name = paramName))
        }


    }


    override fun addFilePart(
        fieldName: String,
        uploadFile: File
    ) {
        checkSingleFile = 2
        singleFiles[fieldName] = uploadFile
        // singleFilesType.add(fileType)
        dataParts.add(FileDataPart(uploadFile, name = fieldName))
    }

    /*  fun Executor(proceed: (response: Result?, executorException: ExecutorException?) -> Unit) {
          val boundary: String = "===" + System.currentTimeMillis() + "==="
          val httpURlConnection = URL(pathURL).openConnection() as HttpURLConnection
          var outputStream: OutputStream
          var writer: PrintWriter
          var index = 0

          GlobalScope.launch(Dispatchers.IO) {
              try {

                  httpURlConnection.setRequestProperty("Accept-Charset", "UTF-8")
                  httpURlConnection.setRequestProperty("Connection", "Keep-Alive")
                  httpURlConnection.setRequestProperty("Cache-Control", "no-cache")
                  httpURlConnection.setRequestProperty("Content-Length", 226.toString())
                  httpURlConnection.setRequestProperty(
                          "Content-Type",
                          "multipart/form-data; boundary=" + boundary
                  )
                  for ((key, value) in getHeaderParameter.entries) {
                      httpURlConnection?.setRequestProperty(key, value)
                  }
                  httpURlConnection.setChunkedStreamingMode(maxBufferSize)
                  httpURlConnection.doInput = true
                  httpURlConnection.doOutput = true    // indicates POST method
                  httpURlConnection.useCaches = false
                  outputStream = httpURlConnection.outputStream
                  writer = PrintWriter(OutputStreamWriter(outputStream, charset), true)


                  if (checkBodytype == "bodyParameter") {
                      for ((key, value) in getBodyParameter) {

                          writer?.append("--")?.append(boundary)?.append(LINE_FEED)
                          writer?.append("Content-Disposition: form-data; name=\"")?.append(key)!!
                                  .append("\"")
                                  .append(LINE_FEED)
                          writer?.append(LINE_FEED)
                          writer!!.append(value).append(LINE_FEED)
                          writer?.flush()


                      }

                  } else if (checkBodytype == "jsonBody") {
                      *//*  writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
                      writer?.write(jsonBody.toString())
                      writer?.flush()
                      writer?.close()
                      os?.close()*//*
                }

                if (checkSingleOrDoubleFile == 1) {

                    for (i in 0 until setMultipartParameter.size) {
                        val fNAme = setMultipartParameter[i].absolutePath.split("/".toRegex())
                                .toTypedArray()
                        writer!!.append("--").append(boundary).append(LINE_FEED)
                        writer!!.append("Content-Disposition: file; name=\"").append(myfieldName)
                                .append("\"; filename=\"").append(fNAme.toString()).append("\"")
                                .append(LINE_FEED)
                        writer!!.append("Content-Type: ").append(myfileType).append(LINE_FEED)
                        writer!!.append(LINE_FEED)
                        writer!!.flush()

                        val inputStream = FileInputStream(setMultipartParameter[i])
                        inputStream.copyTo(outputStream, maxBufferSize)

                        outputStream.flush()
                        inputStream.close()
                        writer.append(LINE_FEED)
                        writer.flush()

                    }
                }

                if (checkSingleFile == 2) {

                    for ((key, value) in singleFiles) {
                        val fNAme = value.absolutePath.split("/".toRegex()).toTypedArray()
                        val type = singleFilesType[index]
                        writer!!.append("--").append(boundary).append(LINE_FEED)
                        writer!!.append("Content-Disposition: file; name=\"").append(key)
                                .append("\"; filename=\"").append(fNAme.toString()).append("\"")
                                .append(LINE_FEED)
                        writer!!.append("Content-Type: ").append(type).append(LINE_FEED)
                        writer!!.append(LINE_FEED)
                        writer!!.flush()

                        val inputStream = FileInputStream(value)
                        inputStream.copyTo(outputStream, maxBufferSize)
                        outputStream.flush()
                        inputStream.close()
                        writer.append(LINE_FEED)
                        writer.flush()
                        index + 1

                    }

                }


                writer.append(LINE_FEED).flush()
                writer.append("--").append(boundary).append("--")
                        .append(LINE_FEED)
                writer.close()


                httpURlConnection?.connect()
                val status = httpURlConnection.responseCode
                val data = httpURlConnection?.inputStream?.bufferedReader()?.readText()
                GlobalScope.launch(Dispatchers.Main) {

                    if (httpURlConnection?.responseCode == 200) {
                        try {


                            proceed.invoke(
                                    Result(
                                            data,
                                            httpURlConnection?.responseCode,
                                            httpURlConnection?.responseMessage
                                    ), null
                            )

                            objData = data

                            httpURlConnection?.disconnect()
                        } catch (js: JSONException) {
                            val executorException = ExecutorException(
                                    httpURlConnection?.responseCode,
                                    httpURlConnection?.responseMessage,
                                    "JSONException $js",
                                    data
                            )
                            proceed.invoke(null, executorException)
                            httpURlConnection?.disconnect()
                        }
                    }
                }


            } catch (se: SocketTimeoutException) {
                val `in` = InputStreamReader(httpURlConnection?.errorStream)
                var stringBuilder = StringBuilder()
                val bufferedReader = BufferedReader(`in`)
                var cp: Int
                while (bufferedReader.read().also { cp = it } != -1) {
                    stringBuilder.append(cp.toChar())
                }
                bufferedReader.close()
                `in`.close()
                GlobalScope.launch(Dispatchers.Main) {
                    val executorException = ExecutorException(
                            httpURlConnection?.responseCode, httpURlConnection?.responseMessage,
                            "SocketTimeoutException $se", stringBuilder.toString()
                    )
                    proceed.invoke(
                            null,
                            executorException
                    )
                }

            } catch (e: IOException) {
                var stringBuilder = StringBuilder()
                if (httpURlConnection.errorStream != null) {
                    val `in` = InputStreamReader(httpURlConnection?.errorStream)
                    val bufferedReader = BufferedReader(`in`)
                    if (bufferedReader != null) {
                        var cp: Int
                        while (bufferedReader.read().also { cp = it } != -1) {
                            stringBuilder.append(cp.toChar())
                        }
                        bufferedReader.close()
                    }
                    `in`.close()
                }
                GlobalScope.launch(Dispatchers.Main) {
                    val executorException = ExecutorException(
                            httpURlConnection?.responseCode, httpURlConnection?.responseMessage,
                            "IOException $e", stringBuilder.toString()
                    )
                    proceed.invoke(
                            null,
                            executorException
                    )
                }

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
                GlobalScope.launch(Dispatchers.Main) {
                    val executorException = ExecutorException(
                            httpURlConnection?.responseCode, httpURlConnection?.responseMessage,
                            " java.lang.Exception $e", stringBuilder.toString()
                    )
                    proceed.invoke(
                            null,
                            executorException
                    )
                }

            } finally {
                httpURlConnection?.disconnect()

                try {
                    //  writer?.close()
                } catch (ex: IOException) {
                    Log.e("fdfdfd", "Executor: " + ex)
                }

            }

        }


    }*/

    fun executor(proceed: (result: Result?, exception: ExecutorException?) -> Unit) {
        if (checkAuth == 2) {
            Fuel.upload("")
                .plus(dataParts)
                .authentication()
                .basic(username!!, password!!)
                .responseString { request, response, result ->
                    if (response.statusCode == 200) {
                        proceed.invoke(
                            Result(
                                result.get(),
                                response.statusCode,
                                response.responseMessage
                            ), null
                        )
                    } else {
                        proceed.invoke(
                            null,
                            ExecutorException(
                                response.statusCode,
                                response.responseMessage,
                                result.component2()?.exception?.message,
                                result.component2()?.localizedMessage
                            )
                        )
                    }
                }
        } else {
            Fuel.upload(pathURL!!, parameters = getBodyParameter.toList())
                .plus(dataParts)
                .responseString { request, response, result ->
                    if (response.statusCode == 200) {
                        proceed.invoke(
                            Result(
                                result.get(),
                                response.statusCode,
                                response.responseMessage
                            ), null
                        )
                    } else {
                        proceed.invoke(
                            null,
                            ExecutorException(
                                response.statusCode,
                                response.responseMessage,
                                result.component2()?.exception?.message,
                                result.component2()?.localizedMessage
                            )
                        )
                    }
                }
        }

    }

    inline fun <reified T : Any> createModelFromClass(json: String): T {
        return Gson().fromJson(json, T::class.java)
    }


}