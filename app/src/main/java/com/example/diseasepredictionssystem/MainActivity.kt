package com.example.diseasepredictionssystem

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import com.example.diseasepredictionssystem.ui.theme.DiseasePredictionsSystemTheme
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DiseasePredictionsSystemTheme {
                MainScreen(onLogout = {
                    val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    sharedPref.edit().clear().apply() // Clear token on logout
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onLogout: () -> Unit) {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var resultText by remember { mutableStateOf("") }
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Retrieve saved JWT token from SharedPreferences
    val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val token = sharedPref.getString("jwt_token", null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Disease Predictor") },
                navigationIcon = {
                    IconButton(onClick = {
//                        (context as? ComponentActivity)?.finish()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            properties = PopupProperties(focusable = true)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Profile") },
                                onClick = {
                                    Toast.makeText(context, "Profile Clicked", Toast.LENGTH_SHORT).show()
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("My History") },
                                onClick = {
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Logout") },
                                onClick = {
                                    expanded = false
                                    onLogout()
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFB3E5FC),
                            Color(0xFF81D4FA),
                            Color(0xFF0288D1)
                        )
                    )
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BasicTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color.White, shape = MaterialTheme.shapes.small)
                    .padding(12.dp),
                decorationBox = { innerTextField ->
                    if (searchText.text.isEmpty()) Text("Enter symptoms...")
                    innerTextField()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (token == null) {
                        Toast.makeText(context, "Token not found. Please login again.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val symptoms = searchText.text.split(",").map { it.trim() }
                    val request = PredictRequest(symptoms, token)

                    coroutineScope.launch {
                        try {
                            val response = ApiClient.apiService.predictDisease(request)
                            if (response.isSuccessful) {
                                val body = response.body() // Access the body
                                if (body != null) {
                                    resultText = buildString {
                                        appendLine("Disease: ${body.predicted_disease}")
                                        appendLine("Description: ${body.description}")
                                        appendLine("Precautions: ${body.precautions.joinToString(", ")}")
                                        appendLine("Medications: ${body.medications.joinToString(", ")}")
                                        appendLine("Diets: ${body.diets.joinToString(", ")}")
                                        appendLine("Workouts: ${body.workouts.joinToString(", ")}")
                                    }
                                } else {
                                    resultText = "Error: Empty response body"
                                }
                            } else {
                                resultText = "Error: ${response.message()}"
                            }
                        } catch (e: Exception) {
                            resultText = "Error: ${e.message}"
                        }
                    }

                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0288D1),
                    contentColor = Color.White
                )
            ) {
                Text("Submit", fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = resultText, color = Color.White)
        }
    }
}
