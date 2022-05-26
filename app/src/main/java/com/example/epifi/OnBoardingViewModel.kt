package com.example.epifi

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.epifi.ui.UiEvents
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class OnBoardingViewModel: ViewModel() {

    val panNumber = MutableStateFlow("")
    val date = MutableStateFlow("")
    val month = MutableStateFlow("")
    val year = MutableStateFlow("")

    private val _onBoardingState = MutableStateFlow(OnBoardingState())
    val onBoardingState : StateFlow<OnBoardingState> = _onBoardingState

    private val _uiEvents = MutableSharedFlow<UiEvents>()
    val uiEvents: SharedFlow<UiEvents> = _uiEvents


    init {
        viewModelScope.launch {
            launch {
                panNumber.collectLatest { onEvent(OnBoardingEvents.OnPanChange(it)) }
            }
            launch {
                date.collectLatest { onEvent(OnBoardingEvents.OnDateChange(it)) }
            }
            launch {
                month.collectLatest { onEvent(OnBoardingEvents.OnMonthChange(it)) }
            }
            launch {
                year.collectLatest { onEvent(OnBoardingEvents.OnYearChange(it)) }
            }
        }
    }

    fun onEvent(event: OnBoardingEvents) {
        var tempState : OnBoardingState? = null
        when (event) {
            OnBoardingEvents.OnClickNegative -> viewModelScope.launch{ _uiEvents.emit(UiEvents.DismissScreen) }
            OnBoardingEvents.OnClickPositive -> {
                submitData()
            }
            is OnBoardingEvents.OnPanChange -> {
                if (event.panNumber.isNotBlank() &&
                    (event.panNumber.length > 10 ||
                            !event.panNumber.matches("[a-zA-Z0-9]+".toRegex()))) return
                tempState = onBoardingState.value.copy(
                    panNumber = event.panNumber
                )
            }
            is OnBoardingEvents.OnDateChange -> {
                if (event.date.length > 2) return
                tempState = onBoardingState.value.copy(
                    date = event.date
                )
            }
            is OnBoardingEvents.OnMonthChange -> {
                if (event.month.length > 2) return
                tempState = onBoardingState.value.copy(
                    month = event.month
                )
            }
            is OnBoardingEvents.OnYearChange -> {
                if (event.year.length > 4) return
                tempState = onBoardingState.value.copy(
                    year = event.year
                )
            }
        }
        if (tempState != null && tempState != onBoardingState.value) {
            _onBoardingState.value = tempState.copy(isAllOk = validateDate(tempState))
        }
    }

    private fun submitData() = viewModelScope.launch {
        _onBoardingState.value = onBoardingState.value.copy(mustShowProgressBar = true)
        delay(3000)
        _onBoardingState.value = onBoardingState.value.copy(mustShowProgressBar = false)
        _uiEvents.emit(UiEvents.ShowToast("Details submitted successfully"))
        _uiEvents.emit(UiEvents.DismissScreen)
    }

    private fun validateDate(state: OnBoardingState): Boolean {
        if (!state.panNumber.matches("[A-Z]{5}[0-9]{4}[A-Z]".toRegex())) {
            return false
        }

        try {
            with(state){
                SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH).apply {
                    isLenient = false
                    parse("$date.$month.$year")
                }
            }
        } catch (e: ParseException) {
            return false
        }

        return true
    }

    fun onPositiveButtonClick(){
        onEvent(OnBoardingEvents.OnClickPositive)
    }
    fun onNegativeButtonClick(){
        onEvent(OnBoardingEvents.OnClickNegative)
    }
}