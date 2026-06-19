package com.example.fluentvoice.ui.screens.therapist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fluentvoice.data.repository.FluentVoiceRepository
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun TherapistAppointmentsScreen() {
    val user by FluentVoiceRepository.currentUser.collectAsState()
    val appointments by FluentVoiceRepository.appointments.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val myAppointments = appointments.filter { it.therapistId == user?.id }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE9EFF9)) // BgColor
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Clinic Bookings",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B2B5E),
            modifier = Modifier.padding(bottom = 4.dp)
        )

        if (myAppointments.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Text(text = "No appointments scheduled.", color = Color(0xFF64748B), fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(myAppointments) { app ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFC8D3E8))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = app.patientName, fontWeight = FontWeight.Bold, color = Color(0xFF1B2B5E), fontSize = 14.sp)
                                val statusColor = when (app.status) {
                                    "confirmed" -> Color(0xFF10B981)
                                    "cancelled" -> Color(0xFFEF4444)
                                    else -> Color(0xFFF59E0B)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50.dp))
                                        .background(statusColor.copy(alpha = 0.1f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(text = app.status.uppercase(), color = statusColor, fontWeight = FontWeight.Bold, fontSize = 8.sp)
                                }
                            }
                            Text(
                                text = "${app.date} @ ${app.time} (${app.type})",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                            if (app.notes.isNotEmpty()) {
                                Text(
                                    text = "Notes: " + app.notes,
                                    fontSize = 11.sp,
                                    color = Color(0xFF374151),
                                    modifier = Modifier.padding(top = 6.dp)
                                )
                            }

                            if (app.status == "pending") {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            coroutineScope.launch {
                                                FluentVoiceRepository.updateAppointmentStatus(app.id, "cancelled")
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444).copy(alpha = 0.1f)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f),
                                        contentPadding = PaddingValues(vertical = 4.dp)
                                    ) {
                                        Text(text = "Decline", color = Color(0xFFEF4444), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Button(
                                        onClick = {
                                            coroutineScope.launch {
                                                FluentVoiceRepository.updateAppointmentStatus(app.id, "confirmed")
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f),
                                        contentPadding = PaddingValues(vertical = 4.dp)
                                    ) {
                                        Text(text = "Approve", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
