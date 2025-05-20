package com.datastax.themis

class ThemisException(message: String, cause: Throwable = null) extends RuntimeException(message, cause)