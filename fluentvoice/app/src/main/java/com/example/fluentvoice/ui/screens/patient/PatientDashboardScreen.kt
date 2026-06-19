package com.example.fluentvoice.ui.screens.patient

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.fluentvoice.ui.components.FluencyGauge

@Composable
fun PatientDashboardScreen(
    onNavigateToRecord: () -> Unit,
    onNavigateToAppointments: () -> Unit,
    onNavigateToSessions: () -> Unit,
    onNavigateToTreatment: () -> Unit
) {
    val user by FluentVoiceRepository.currentUser.collectAsState()
    val sessions by FluentVoiceRepository.sessions.collectAsState()
    val appointments by FluentVoiceRepository.appointments.collectAsState()

    val patientSessions = sessions.filter { it.patientId == user?.id }
    val latestSession = patientSessions.firstOrNull()

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
                    text = "Welcome back,",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp
                )
                Text(
                    text = user?.name ?: "Patient",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Lived experience backing clinical science.",
                    color = Color(0xFFC9A84C),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Quick Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onNavigateToRecord,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B2B5E))
            ) {
                Icon(imageVector = Icons.Default.Mic, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Record speech", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
            }

            Button(
                onClick = onNavigateToAppointments,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFC8D3E8))
            ) {
                Icon(imageVector = Icons.Default.Event, contentDescription = null, tint = Color(0xFF1B2B5E))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Appointments", fontWeight = FontWeight.Bold, color = Color(0xFF1B2B5E), fontSize = 13.sp)
            }
        }

        // Latest Session Overview
        if (latestSession != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFC8D3E8))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "LATEST SESSION REPORT",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF9CA3AF),
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = latestSession.date,
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            FluencyGauge(score = latestSession.fluencyScore, size = 110.dp)
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            DashboardMetricCard(
                                label = "Speech Rate",
                                value = "${latestSession.speechRate.toInt()}",
                                unit = "wpm",
                                color = Color(0xFF6366F1)
                            )
                            DashboardMetricCard(
                                label = "Disfluencies",
                                value = "${latestSession.disfluencies.size}",
                                unit = "events",
                                color = Color(0xFFF59E0B)
                            )
                        }
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFC8D3E8))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🎙️", fontSize = 32.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No recordings yet",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B2B5E),
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Record a 30s speech clip to analyze your fluency.",
                        color = Color(0xFF64748B),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
                    )
                    Button(
                        onClick = onNavigateToRecord,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B2B5E)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "Start Recording", color = Color.White)
                    }
                }
            }
        }

        // Active Treatment Cards
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToTreatment() },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFC8D3E8))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFC9A84C).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Assignment, contentDescription = null, tint = Color(0xFFC9A84C))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Treatment Plan & Goals", fontWeight = FontWeight.Bold, color = Color(0xFF1B2B5E), fontSize = 14.sp)
                    Text(text = "Check goals set by your therapist.", color = Color(0xFF64748B), fontSize = 11.sp)
                }
                Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF9CA3AF))
            }
        }

        // Recent Sessions List
        if (patientSessions.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Sessions",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B2B5E),
                        fontSize = 15.sp
                    )
                    TextButton(onClick = onNavigateToSessions) {
                        Text(text = "See all", color = Color(0xFF2D44A0), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                patientSessions.take(3).forEach { session ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToSessions() },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFC8D3E8))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0x141B2B5E)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${session.fluencyScore}",
                                    color = Color(0xFF1B2B5E),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Fluency Session",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1B2B5E),
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = session.date,
                                    color = Color(0xFF64748B),
                                    fontSize = 11.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50.dp))
                                    .background(
                                        when (session.severity) {
                                            "mild" -> Color(0xFF10B981).copy(alpha = 0.1f)
                                            "moderate" -> Color(0xFFF59E0B).copy(alpha = 0.1f)
                                            else -> Color(0xFFEF4444).copy(alpha = 0.1f)
                                        }
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = session.severity.replaceFirstChar { it.titlecase() },
                                    color = when (session.severity) {
                                        "mild" -> Color(0xFF10B981)
                                        "moderate" -> Color(0xFFF59E0B)
                                        else -> Color(0xFFEF4444)
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardMetricCard(
    label: String,
    value: String,
    unit: String,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF6F9FF)),
        border = BorderStroke(1.dp, Color(0xFFC8D3E8))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = color)
                Spacer(modifier = Modifier.width(2.dp))
                Text(text = unit, fontSize = 9.sp, color = Color(0xFF9CA3AF), fontWeight = FontWeight.Bold)
            }
            Text(
                text = label,
                fontSize = 9.sp,
                color = Color(0xFF9CA3AF),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
