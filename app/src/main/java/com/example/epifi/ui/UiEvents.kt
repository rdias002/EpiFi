package com.example.epifi.ui

sealed class UiEvents{
    object DismissScreen: UiEvents()
    data class ShowToast(val message: String): UiEvents()

}
