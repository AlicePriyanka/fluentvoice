package com.example.fluentvoice.ui.screens.therapist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fluentvoice.data.model.Profile
import com.example.fluentvoice.data.repository.FluentVoiceRepository
import kotlinx.coroutines.launch

@Composable
fun TherapistProfileScreen() {
    val user by FluentVoiceRepository.currentUser.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var phone by remember { mutableStateOf("") }
    var specialty by remember { mutableStateOf("") }
    var licenseNumber by remember { mutableStateOf("") }
    var clinicName by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var successMsg by remember { mutableStateOf("") }

    LaunchedEffect(user?.id) {
        user?.id?.let { uid ->
            val p = FluentVoiceRepository.getProfile(uid)
            if (p != null) {
                phone = p.phone ?: ""
                specialty = p.specialty ?: ""
                licenseNumber = p.licenseNumber ?: ""
                clinicName = p.clinicName ?: ""
                bio = p.bio ?: ""
            }
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE9EFF9)) // BgColor
            .verticalScroll(scrollState)
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
                        Icon(imageVector = Icons.Default.MedicalServices, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
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
                        singleLine = true
                    )
                }

                // Specialty Input
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "SPECIALTY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                    OutlinedTextField(
                        value = specialty,
                        onValueChange = { specialty = it; successMsg = "" },
                        placeholder = { Text("e.g. Stuttering specialist") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1B2B5E),
                            unfocusedBorderColor = Color(0xFFC8D3E8)
                        ),
                        singleLine = true
                    )
                }

                // License Input
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "LICENSE NUMBER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                    OutlinedTextField(
                        value = licenseNumber,
                        onValueChange = { licenseNumber = it; successMsg = "" },
                        placeholder = { Text("e.g. A-123456") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1B2B5E),
                            unfocusedBorderColor = Color(0xFFC8D3E8)
                        ),
                        singleLine = true
                    )
                }

                // Clinic Input
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "CLINIC NAME", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                    OutlinedTextField(
                        value = clinicName,
                        onValueChange = { clinicName = it; successMsg = "" },
                        placeholder = { Text("e.g. VocalFreedom clinic") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1B2B5E),
                            unfocusedBorderColor = Color(0xFFC8D3E8)
                        ),
                        singleLine = true
                    )
                }

                // Bio Input
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "PROFESSIONAL BIO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it; successMsg = "" },
                        placeholder = { Text("Enter a brief clinical bio...") },
                        modifier = Modifier.fillMaxWidth().height(80.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1B2B5E),
                            unfocusedBorderColor = Color(0xFFC8D3E8)
                        )
                    )
                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            val updated = Profile(
                                userId = user?.id ?: "",
                                role = "therapist",
                                phone = phone,
                                specialty = specialty,
                                licenseNumber = licenseNumber,
                                clinicName = clinicName,
                                bio = bio
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
