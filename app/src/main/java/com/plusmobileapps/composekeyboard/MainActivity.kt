package com.plusmobileapps.composekeyboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.insets.ui.TopAppBar
import com.google.accompanist.insets.*
import com.plusmobileapps.composekeyboard.ui.theme.ComposeKeyboardTheme
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

@Composable
fun ComposeKeyboardScreen() {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
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
                        scope.launch {
                            scaffoldState.snackbarHostState.showSnackbar("Button Clicked")
                        }
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .navigationBarsWithImePadding()
                ) {
                    Text(text = "Submit")
                }
            }
        }
    ) { contentPadding ->
        ComposeKeyboardBody(contentPadding)
    }
}

@OptIn(ExperimentalAnimatedInsets::class)
@Composable
fun ComposeKeyboardBody(contentPadding: PaddingValues) {
    var text by remember {
        mutableStateOf("")
    }
    Box(modifier = Modifier.padding(contentPadding)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(connection = rememberImeNestedScrollConnection())
                .verticalScroll(state = rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            (0..15).forEach {
                Text("hello world")
            }
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text(text = "Enter email") })
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