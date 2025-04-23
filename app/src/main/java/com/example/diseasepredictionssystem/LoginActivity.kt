package com.example.diseasepredictionssystem

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyFirstLayout()
        }
    }
}
@Composable
fun MyFirstLayout() {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFB3E5FC), // light blue
                        Color(0xFF81D4FA), // sky blue
                        Color(0xFF0288D1)  // dark blue
                    )
                )
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(10.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "logo",
                modifier = Modifier
                    .size(250.dp)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(15.dp)
            ) {
                Text(
                    text = "AI Disease Predictor",
                    fontSize = 25.sp,
                    fontFamily = FontFamily.Cursive,
                    color = Color.Red,
                )
                Text(
                    text = "Thapathali Kathmandu",
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Cursive,
                )
            }

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Person") }
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    val loginRequest = LoginRequest(email = username, password = password)

                    ApiClient.apiService.loginUser(loginRequest).enqueue(object : retrofit2.Callback<LoginResponse> {
                        override fun onResponse(
                            call: retrofit2.Call<LoginResponse>,
                            response: retrofit2.Response<LoginResponse>
                        ) {
                            if (response.isSuccessful) {
                                val token = response.body()?.token

                                // Save token in SharedPreferences
                                val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                sharedPref.edit().putString("jwt_token", token).apply()

                                val intent = Intent(context, MainActivity::class.java)
                                context.startActivity(intent)
                                Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                            }
                            else {
                                Toast.makeText(context, "Invalid credentials", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: retrofit2.Call<LoginResponse>, t: Throwable) {
                            Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                },
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0288D1), // Custom background color (e.g., blue)
                    contentColor = Color.White           // Text color
                )
            ) {
                Text(
                    text = "Login",
                    fontSize = 40.sp,
                    fontFamily = FontFamily.Cursive,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = {
                    val intent=Intent(context,RegisterActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF81D4FA), // Custom background color (e.g., blue)
                    contentColor = Color.White           // Text color
                )
            ) {
                Text(
                    text = "Register",
                    fontSize = 40.sp,
                    fontFamily = FontFamily.Cursive,
                    fontWeight = FontWeight.Bold
                )
            }

        }
    }
}

@Preview(name="MyFirstlayout")

@Composable
fun MyFirstLayoutPreview() {
    MyFirstLayout()
}

