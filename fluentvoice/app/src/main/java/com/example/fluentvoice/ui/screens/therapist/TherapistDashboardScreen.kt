package com.example.fluentvoice.ui.screens.therapist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
fun TherapistDashboardScreen(
    onNavigateToPatients: () -> Unit,
    onNavigateToAppointments: () -> Unit
) {
    val user by FluentVoiceRepository.currentUser.collectAsState()
    val patients by FluentVoiceRepository.patients.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val appointments by FluentVoiceRepository.appointments.collectAsState()

    val myPatients = patients.filter { it.therapistId == user?.id }
    val myAppointments = appointments.filter { it.therapistId == user?.id }
    val pendingApp = myAppointments.filter { it.status == "pending" }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE9EFF9)) // BgColor
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2B5E))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Welcome,",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp
                )
                Text(
                    text = user?.name ?: "Therapist",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Clinical portal is online.",
                    color = Color(0xFFC9A84C),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Summary Statistics Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFC8D3E8))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Total Patients", fontSize = 11.sp, color = Color(0xFF9CA3AF), fontWeight = FontWeight.Bold)
                    Text(text = "${myPatients.size}", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color(0xFF1B2B5E))
                }
            }
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFC8D3E8))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Pending Bookings", fontSize = 11.sp, color = Color(0xFF9CA3AF), fontWeight = FontWeight.Bold)
                    Text(text = "${pendingApp.size}", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color(0xFFF59E0B))
                }
            }
        }

        // Pending Bookings Requests List
        if (pendingApp.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Pending Approvals", fontWeight = FontWeight.Bold, color = Color(0xFF1B2B5E), fontSize = 15.sp)
                pendingApp.forEach { app ->
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
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50.dp))
                                        .background(Color(0xFFF59E0B).copy(alpha = 0.1f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(text = "PENDING", color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold, fontSize = 9.sp)
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
                                    Text(text = "Cancel Slot", color = Color(0xFFEF4444), fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
                                    Text(text = "Approve Booking", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Today's/Upcoming confirmed sessions overview
        val confirmed = myAppointments.filter { it.status == "confirmed" }
        if (confirmed.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Confirmed Sessions", fontWeight = FontWeight.Bold, color = Color(0xFF1B2B5E), fontSize = 15.sp)
                    TextButton(onClick = onNavigateToAppointments) {
                        Text(text = "View all", color = Color(0xFF2D44A0), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                confirmed.take(3).forEach { app ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFC8D3E8))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, tint = Color(0xFF1B2B5E))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = app.patientName, fontWeight = FontWeight.Bold, color = Color(0xFF1B2B5E), fontSize = 13.sp)
                                Text(text = "${app.date} at ${app.time} - ${app.type}", color = Color(0xFF64748B), fontSize = 11.sp)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50.dp))
                                    .background(Color(0xFF10B981).copy(alpha = 0.1f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(text = "CONFIRMED", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 8.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
