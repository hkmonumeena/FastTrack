package com.e.mylibrary

import org.json.JSONObject
import java.io.File

interface ExecutorUpload {
    fun url(url:String)
    fun authenticatHeader(username:String, password:String)
    fun headerParameter(listHeader: MutableMap<String, String>)
    fun bodyParameter(listBody: MutableMap<String, String>)
    fun addFileList(getMultipartParameter: ArrayList<File>, paramName:String)
    fun addFilePart(fieldName: String, uploadFile: File)

}