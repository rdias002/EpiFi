package com.example.epifi

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.epifi.ui.UiEvents
import com.example.epifi.ui.theme.EpiFiTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EpiFiTheme {
                SignUpScreen()
            }
        }

        val viewModel: OnBoardingViewModel by viewModels()
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvents.collectLatest { event ->
                    if (event is UiEvents.ShowToast) {
                        Toast.makeText(this@ComposeActivity, event.message, Toast.LENGTH_SHORT)
                            .show()
                    } else if (event is UiEvents.DismissScreen) {
                        finish()
                    }
                }

            }
        }
    }

    @Composable
    fun SignUpScreen() {
        val viewModel: OnBoardingViewModel by viewModels()
        val state = viewModel.onBoardingState.collectAsState().value
        val isEnabled = !state.mustShowProgressBar
        Box {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
            ) {
                Text(
                    text = "S.",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.secondary,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 36.sp
                )
                Spacer(modifier = Modifier.height(28.dp))
                Text(
                    text = stringResource(R.string.intro_msg),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 30.sp
                )
                Spacer(modifier = Modifier.weight(weight = 1f, fill = true))
                TextFieldWithLabel(
                    value = state.panNumber,
                    onValueChange = { viewModel.onEvent(OnBoardingEvents.OnPanChange(it)) },
                    label = stringResource(R.string.pan_number),
                    modifier = Modifier.fillMaxWidth(),
                    isTextCentered = false,
                    isEnabled = isEnabled
                )
                Spacer(modifier = Modifier.height(36.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(22.dp),
                ) {
                    TextFieldWithLabel(
                        value = state.date,
                        onValueChange = { viewModel.onEvent(OnBoardingEvents.OnDateChange(it)) },
                        label = stringResource(R.string.birthdate),
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        isEnabled = isEnabled
                    )
                    TextFieldWithLabel(
                        value = state.month,
                        onValueChange = { viewModel.onEvent(OnBoardingEvents.OnMonthChange(it)) },
                        label = "",
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        isEnabled = isEnabled
                    )
                    TextFieldWithLabel(
                        value = state.year,
                        onValueChange = { viewModel.onEvent(OnBoardingEvents.OnYearChange(it)) },
                        label = "",
                        modifier = Modifier.weight(2f),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        isEnabled = isEnabled
                    )
                }
                Spacer(modifier = Modifier.weight(weight = 2f, fill = true))
                Text(
                    text = getInfoText(),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = Color.Gray
                )
                Button(
                    onClick = { viewModel.onEvent(OnBoardingEvents.OnClickPositive) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    enabled = state.isAllOk && isEnabled
                ) {
                    Text(stringResource(R.string.next))
                }
                TextButton(
                    onClick = { viewModel.onEvent(OnBoardingEvents.OnClickNegative) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isEnabled
                ) {
                    Text(stringResource(R.string.i_dont_have_pan))
                }
            }

            if (state.mustShowProgressBar) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Gray.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .width(48.dp)
                            .height(48.dp),
                        strokeWidth = 4.dp
                    )
                }
            }
        }
    }

    @Composable
    fun getInfoText(): AnnotatedString {
        return with(AnnotatedString.Builder("")) {
            append(stringResource(R.string.info_text))
            pushStyle(SpanStyle(color = MaterialTheme.colors.primary))
            append(stringResource(R.string.learn_more))
            pop()
            toAnnotatedString()
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun SignUpScreenPreview() {
        SignUpScreen()
    }


    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun TextFieldWithLabel(
        value: String,
        onValueChange: (String) -> Unit,
        label: String,
        modifier: Modifier = Modifier,
        isTextCentered: Boolean = true,
        keyboardOptions: KeyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Characters,
            autoCorrect = false,
            KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        isEnabled: Boolean = true,
    ) {
        Column(modifier = modifier) {
            Text(
                text = label,
                color = Color.Gray,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 2.dp)
            )
            val keyboardController = LocalSoftwareKeyboardController.current
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = LocalTextStyle.current.copy(
                    textAlign = if (isTextCentered) TextAlign.Center else TextAlign.Start,
                    fontSize = 20.sp
                ),
                singleLine = true,
                keyboardOptions = keyboardOptions,
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colors.primary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                enabled = isEnabled
            )
        }
    }
}