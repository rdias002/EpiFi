package com.example.epifi

data class OnBoardingState(
    val panNumber: String = "",
    val date: String = "",
    val month: String = "",
    val year: String = "",
    val isAllOk: Boolean = false,
    val mustShowProgressBar: Boolean = false
)
