package com.plusmobileapps.composekeyboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.google.accompanist.insets.*
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.insets.ui.TopAppBar
import com.plusmobileapps.composekeyboard.ui.theme.ComposeKeyboardTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            ComposeKeyboardTheme {
                ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
                    // A surface container using the 'background' color from the theme
                    Surface(color = MaterialTheme.colors.background) {
                        ComposeKeyboardScreen()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ComposeKeyboardScreen() {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    var text by remember {
        mutableStateOf("")
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(text = "Compose Keyboard") },
                backgroundColor = MaterialTheme.colors.surface,
                contentPadding = rememberInsetsPaddingValues(
                    insets = LocalWindowInsets.current.statusBars,
                    applyBottom = false
                )
            )
        },
        bottomBar = {
            Surface(elevation = 1.dp) {
                Button(
                    onClick = {
                        val email = text
                        scope.launch {
                            scaffoldState.snackbarHostState.showSnackbar("$email was submitted!")
                        }
                        text = ""
                        keyboardController?.hide()
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsWithImePadding()
                ) {
                    Text(text = "Submit")
                }
            }
        }
    ) { contentPadding ->
        ComposeKeyboardBody(
            contentPadding = contentPadding,
            text = text,
            onTextFieldChanged = { text = it },
            onKeyboardSubmitClicked = {
                val email = text
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar("$email was submitted!")
                }
                text = ""
                keyboardController?.hide()
            }
        )
    }
}

@OptIn(ExperimentalAnimatedInsets::class, ExperimentalFoundationApi::class)
@Composable
fun ComposeKeyboardBody(
    contentPadding: PaddingValues,
    text: String,
    onTextFieldChanged: (String) -> Unit,
    onKeyboardSubmitClicked: () -> Unit
) {
    val requesterScope = rememberCoroutineScope()
    val bringIntoViewRequester = remember {
        BringIntoViewRequester()
    }
    val ime = LocalWindowInsets.current.ime
    val focusManager = LocalFocusManager.current
    if (!ime.isVisible) {
        focusManager.clearFocus()
    }

    Box(modifier = Modifier.padding(contentPadding)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            (0..15).forEach {
                Text("hello world")
            }
            OutlinedTextField(
                value = text,
                onValueChange = onTextFieldChanged,
                label = { Text(text = "Enter email") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    autoCorrect = false,
                    imeAction = ImeAction.Go
                ),
                keyboardActions = KeyboardActions(onGo = {
                    onKeyboardSubmitClicked()
                }),
                modifier = Modifier
                    .bringIntoViewRequester(bringIntoViewRequester)
                    .onFocusChanged {
                        if (it.isFocused) {
                            requesterScope.launch {
                                delay(500)
                                bringIntoViewRequester.bringIntoView()
                            }
                        }
                    }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeKeyboardTheme {
        ComposeKeyboardScreen()
    }
}