package com.example.appturismo.utils

import com.example.appturismo.model.MonumentoResponse

interface ComentarioEnviadoListener {
    fun onComentarioEnviado(monumento: MonumentoResponse?)
}