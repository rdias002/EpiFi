package com.example.epifi

sealed class OnBoardingEvents{
    object OnClickPositive: OnBoardingEvents()
    object OnClickNegative: OnBoardingEvents()
    data class OnPanChange(val panNumber: String): OnBoardingEvents()
    data class OnDateChange(val date: String): OnBoardingEvents()
    data class OnMonthChange(val month: String): OnBoardingEvents()
    data class OnYearChange(val year: String): OnBoardingEvents()
}
