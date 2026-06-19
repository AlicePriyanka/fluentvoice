package com.example.fluentvoice.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fluentvoice.data.repository.FluentVoiceRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LoginScreen(
    initialRole: String?,
    onNavigateBack: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onAuthSuccess: (role: String) -> Unit
) {
    var mode by remember { mutableStateOf("signin") } // "signin" or "register"
    var role by remember { mutableStateOf(initialRole ?: "patient") } // "patient" or "therapist"
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE9EFF9)) // BgColor
            .verticalScroll(scrollState)
            .padding(24.dp)
    ) {
        // Back Button
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFF64748B)
            )
        }

        // Main Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFC8D3E8))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFC9A84C)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = Color(0xFF1B2B5E),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "FluentVoice",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B2B5E),
                            fontSize = 15.sp,
                            lineHeight = 16.sp
                        )
                        Text(
                            text = "SPEECH ANALYTICS",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = Color(0xFFC9A84C)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Signin / Register Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFDAE3F2))
                        .padding(3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (mode == "signin") Color.White else Color.Transparent)
                            .clickable { mode = "signin"; error = "" }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sign in",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (mode == "signin") Color(0xFF1B2B5E) else Color(0xFF9CA3AF)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (mode == "register") Color.White else Color.Transparent)
                            .clickable { mode = "register"; error = "" }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Create account",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (mode == "register") Color(0xFF1B2B5E) else Color(0xFF9CA3AF)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = if (mode == "signin") "Welcome back." else "Get started.",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1B2B5E)
                )
                Text(
                    text = if (mode == "signin") "Sign in to access your dashboard." else "Create your free account.",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                )

                // Form fields
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Register Role Toggle
                    if (mode == "register") {
                        Column {
                            Text(
                                text = "I AM A",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B),
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFDAE3F2))
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Button(
                                    onClick = { role = "patient" },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (role == "patient") Color.White else Color.Transparent
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = if (role == "patient") Color(0xFF1B2B5E) else Color(0xFF9CA3AF),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Patient",
                                        color = if (role == "patient") Color(0xFF1B2B5E) else Color(0xFF9CA3AF),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Button(
                                    onClick = { role = "therapist" },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (role == "therapist") Color.White else Color.Transparent
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MedicalServices,
                                        contentDescription = null,
                                        tint = if (role == "therapist") Color(0xFF1B2B5E) else Color(0xFF9CA3AF),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Therapist",
                                        color = if (role == "therapist") Color(0xFF1B2B5E) else Color(0xFF9CA3AF),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Full Name (register only)
                    if (mode == "register") {
                        Column {
                            Text(
                                text = "FULL NAME",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B),
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it; error = "" },
                                placeholder = { Text("e.g. Arjun Kumar") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF1B2B5E),
                                    unfocusedBorderColor = Color(0xFFC8D3E8)
                                ),
                                singleLine = true
                            )
                        }
                    }

                    // Email Address
                    Column {
                        Text(
                            text = "EMAIL ADDRESS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it; error = "" },
                            placeholder = { Text("you@example.com") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF1B2B5E),
                                unfocusedBorderColor = Color(0xFFC8D3E8)
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )
                    }

                    // Password
                    Column {
                        Text(
                            text = "PASSWORD",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it; error = "" },
                            placeholder = { Text(if (mode == "register") "At least 8 characters" else "••••••••") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF1B2B5E),
                                unfocusedBorderColor = Color(0xFFC8D3E8)
                            ),
                            singleLine = true,
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                        imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = if (showPassword) "Hide password" else "Show password"
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                        )
                    }
                }

                // Error message
                if (error.isNotEmpty()) {
                    Text(
                        text = error,
                        color = Color(0xFFEF4444),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }

                // Action Button
                Button(
                    onClick = {
                        error = ""
                        if (email.isBlank() || password.isBlank()) {
                            error = "Email and password are required."
                            return@Button
                        }
                        if (mode == "register" && name.isBlank()) {
                            error = "Name is required."
                            return@Button
                        }
                        if (mode == "register" && password.length < 8) {
                            error = "Password must be at least 8 characters."
                            return@Button
                        }

                        loading = true
                        coroutineScope.launch {
                            if (mode == "signin") {
                                val success = FluentVoiceRepository.login(email, password)
                                loading = false
                                if (success) {
                                    val currentRole = FluentVoiceRepository.currentUser.value?.role ?: "patient"
                                    onAuthSuccess(currentRole)
                                } else {
                                    error = "Invalid email or password."
                                }
                            } else {
                                val success = FluentVoiceRepository.register(name, email, role)
                                loading = false
                                if (success) {
                                    onAuthSuccess(role)
                                } else {
                                    error = "Registration failed."
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1B2B5E),
                        disabledContainerColor = Color(0xFF1B2B5E).copy(alpha = 0.5f)
                    )
                ) {
                    if (loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Text(
                            text = if (mode == "signin") "Sign in" else "Create account",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                    }
                }

                // Forgot Password link
                if (mode == "signin") {
                    TextButton(
                        onClick = onNavigateToForgotPassword,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = "Forgot password?",
                            color = Color(0xFF64748B),
                            fontSize = 12.sp,
                            textDecoration = TextDecoration.Underline
                        )
                    }
                }
            }
        }
    }
}
