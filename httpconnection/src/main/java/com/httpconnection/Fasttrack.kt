package com.e.mylibrary

object Fasttrack :InterfaceMethodType{

    override fun post(post: (Post) -> Unit) {
        post.invoke(Post)
    }

    override fun get(post: (Get) -> Unit) {
        post.invoke(Get)
    }

     override fun upload(post: (Upload) -> Unit) {
         post.invoke(Upload)
     }

 }