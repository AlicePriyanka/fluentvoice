package com.example.fluentvoice.ui.screens.patient

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fluentvoice.data.model.Profile
import com.example.fluentvoice.data.repository.FluentVoiceRepository
import kotlinx.coroutines.launch

@Composable
fun PatientProfileScreen() {
    val user by FluentVoiceRepository.currentUser.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var phone by remember { mutableStateOf("") }
    var ageStr by remember { mutableStateOf("") }
    var condition by remember { mutableStateOf("") }
    var successMsg by remember { mutableStateOf("") }

    LaunchedEffect(user?.id) {
        user?.id?.let { uid ->
            val initialProfile = FluentVoiceRepository.getProfile(uid)
            if (initialProfile != null) {
                phone = initialProfile.phone ?: ""
                ageStr = initialProfile.age?.toString() ?: ""
                condition = initialProfile.condition ?: ""
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE9EFF9)) // BgColor
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Profile Settings",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B2B5E),
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFC8D3E8))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Avatar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1B2B5E)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                    Column {
                        Text(text = user?.name ?: "", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1B2B5E))
                        Text(text = user?.email ?: "", fontSize = 12.sp, color = Color(0xFF64748B))
                    }
                }

                Divider(color = Color(0xFFC8D3E8).copy(alpha = 0.5f))

                // Success Message Banner
                if (successMsg.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF10B981).copy(alpha = 0.1f)),
                        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.2f))
                    ) {
                        Text(
                            text = successMsg,
                            color = Color(0xFF059669),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                // Phone Input
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "PHONE NUMBER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it; successMsg = "" },
                        placeholder = { Text("Enter mobile number") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1B2B5E),
                            unfocusedBorderColor = Color(0xFFC8D3E8)
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                }

                // Age Input
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "AGE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                    OutlinedTextField(
                        value = ageStr,
                        onValueChange = { ageStr = it; successMsg = "" },
                        placeholder = { Text("Enter age") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1B2B5E),
                            unfocusedBorderColor = Color(0xFFC8D3E8)
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                // Condition Input
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "SPEECH CONDITION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                    OutlinedTextField(
                        value = condition,
                        onValueChange = { condition = it; successMsg = "" },
                        placeholder = { Text("e.g. Developmental Stuttering") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1B2B5E),
                            unfocusedBorderColor = Color(0xFFC8D3E8)
                        ),
                        singleLine = true
                    )
                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            val ageInt = ageStr.toIntOrNull()
                            val updated = Profile(
                                userId = user?.id ?: "",
                                role = "patient",
                                phone = phone,
                                age = ageInt,
                                condition = condition
                            )
                            FluentVoiceRepository.saveProfile(updated)
                            successMsg = "✓ Settings saved successfully."
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B2B5E))
                ) {
                    Text(text = "Save Settings", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
