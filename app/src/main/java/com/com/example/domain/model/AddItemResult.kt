package com.com.example.domain.model

sealed class AddItemResult {
    object Success : AddItemResult()
    data class ValidationError(val message: String) : AddItemResult()
    data class Failure(val throwable: Throwable) : AddItemResult()
}