package com.example.appturismo.utils

import com.example.appturismo.model.MonumentoResponse

interface OnMonumentoClickListener {
    fun onMonumentoClick(monumento: MonumentoResponse)
}