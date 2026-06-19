package com.example.fluentvoice.ui.screens.patient

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fluentvoice.data.model.Appointment
import com.example.fluentvoice.data.repository.FluentVoiceRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PatientAppointmentsScreen() {
    val user by FluentVoiceRepository.currentUser.collectAsState()
    val appointments by FluentVoiceRepository.appointments.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val patientAppointments = appointments.filter { it.patientId == user?.id }

    var selectedTab by remember { mutableStateOf(0) } // 0 = List, 1 = Book New

    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("telehealth") } // "telehealth" or "in-clinic"
    var notes by remember { mutableStateOf("") }
    var successMsg by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE9EFF9)) // BgColor
            .padding(16.dp)
    ) {
        Text(
            text = "Appointments",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B2B5E),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Tab selection header
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = Color(0xFF1B2B5E),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFDAE3F2))
                .padding(2.dp)
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0; successMsg = "" },
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selectedTab == 0) Color.White else Color.Transparent)
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.List, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "My Bookings", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selectedTab == 1) Color.White else Color.Transparent)
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Book Session", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // TAB CONTENT
        if (selectedTab == 0) {
            if (successMsg.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF10B981).copy(alpha = 0.1f)),
                    border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.2f))
                ) {
                    Text(
                        text = successMsg,
                        color = Color(0xFF059669),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            if (patientAppointments.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(text = "No appointments scheduled.", color = Color(0xFF64748B), fontSize = 14.sp)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(patientAppointments) { app ->
                        AppointmentItemCard(appointment = app)
                    }
                }
            }
        } else {
            val scrollState = rememberScrollState()
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(scrollState),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFC8D3E8))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Schedule with Dr. Meera Iyer",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B2B5E)
                    )

                    // Date Input
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = "DATE (YYYY-MM-DD)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                        OutlinedTextField(
                            value = date,
                            onValueChange = { date = it },
                            placeholder = { Text("e.g. 2026-06-25") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF1B2B5E),
                                unfocusedBorderColor = Color(0xFFC8D3E8)
                            ),
                            singleLine = true
                        )
                    }

                    // Time Input
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = "TIME (HH:MM)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                        OutlinedTextField(
                            value = time,
                            onValueChange = { time = it },
                            placeholder = { Text("e.g. 11:30") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF1B2B5E),
                                unfocusedBorderColor = Color(0xFFC8D3E8)
                            ),
                            singleLine = true
                        )
                    }

                    // Session Type Selection
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = "SESSION TYPE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFDAE3F2))
                                .padding(3.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Button(
                                onClick = { type = "telehealth" },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (type == "telehealth") Color.White else Color.Transparent
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = Icons.Default.Videocam, contentDescription = null, tint = Color(0xFF1B2B5E), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Telehealth", color = Color(0xFF1B2B5E), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { type = "in-clinic" },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (type == "in-clinic") Color.White else Color.Transparent
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = Icons.Default.LocalHospital, contentDescription = null, tint = Color(0xFF1B2B5E), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "In-Clinic", color = Color(0xFF1B2B5E), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Notes
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = "NOTES / SYMPTOMS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            placeholder = { Text("What would you like to focus on?") },
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
                            if (date.isBlank() || time.isBlank()) return@Button
                            coroutineScope.launch {
                                FluentVoiceRepository.scheduleAppointment(
                                    patientId = user?.id ?: "p1",
                                    patientName = user?.name ?: "Arjun Kumar",
                                    date = date,
                                    time = time,
                                    type = type,
                                    notes = notes
                                )
                                successMsg = "Appointment request submitted successfully."
                                date = ""
                                time = ""
                                notes = ""
                                selectedTab = 0
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B2B5E))
                    ) {
                        Text(text = "Request Booking", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AppointmentItemCard(appointment: Appointment) {
    val statusColor = when (appointment.status) {
        "confirmed" -> Color(0xFF10B981)
        "cancelled" -> Color(0xFFEF4444)
        else -> Color(0xFFF59E0B)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFC8D3E8))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1B2B5E).copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = Color(0xFF1B2B5E),
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${appointment.date} @ ${appointment.time}",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B2B5E),
                        fontSize = 13.sp
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(statusColor.copy(alpha = 0.1f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = appointment.status.uppercase(),
                            color = statusColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.sp
                        )
                    }
                }
                Text(
                    text = "Type: " + appointment.type.replaceFirstChar { it.titlecase() } + " (45 min)",
                    color = Color(0xFF64748B),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
                if (appointment.notes.isNotEmpty()) {
                    Text(
                        text = "Notes: " + appointment.notes,
                        color = Color(0xFF374151),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
