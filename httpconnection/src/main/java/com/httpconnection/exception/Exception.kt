package com.monumeena.fastrack.exception

data class ExecutorException(val responseCode :Int,val responseMessage:String?,val Exception:String?,val ApiMessageIfAny:String?)
