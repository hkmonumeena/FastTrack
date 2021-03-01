package com.e.mylibrary

interface InterfaceMethodType {
    fun post (post: (Post) -> Unit)
    fun get (post: (Get) -> Unit)
    fun upload (post: (Upload) -> Unit)
}