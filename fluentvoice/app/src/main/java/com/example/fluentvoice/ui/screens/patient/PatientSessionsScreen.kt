package com.example.fluentvoice.ui.screens.patient

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fluentvoice.data.model.Session
import com.example.fluentvoice.data.repository.FluentVoiceRepository
import com.example.fluentvoice.ui.components.FluencyGauge

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PatientSessionsScreen() {
    val user by FluentVoiceRepository.currentUser.collectAsState()
    val sessions by FluentVoiceRepository.sessions.collectAsState()

    val patientSessions = sessions.filter { it.patientId == user?.id }

    var expandedSessionId by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE9EFF9)) // BgColor
            .padding(16.dp)
    ) {
        Text(
            text = "Session History",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B2B5E),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (patientSessions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No sessions recorded yet.",
                    color = Color(0xFF64748B),
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(patientSessions) { session ->
                    val isExpanded = expandedSessionId == session.id
                    SessionItemCard(
                        session = session,
                        isExpanded = isExpanded,
                        onToggleExpand = {
                            expandedSessionId = if (isExpanded) null else session.id
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SessionItemCard(
    session: Session,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    val severityColor = when (session.severity) {
        "mild" -> Color(0xFF10B981)
        "moderate" -> Color(0xFFF59E0B)
        else -> Color(0xFFEF4444)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleExpand() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFC8D3E8))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x141B2B5E)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${session.fluencyScore}",
                            color = Color(0xFF1B2B5E),
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }
                    Column {
                        Text(
                            text = "Fluency Analysis",
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
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(severityColor.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = session.severity.uppercase(),
                            color = severityColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        )
                    }
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = Color(0xFF9CA3AF)
                    )
                }
            }

            // Expanded detail section
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                ) {
                    Divider(color = Color(0xFFC8D3E8).copy(alpha = 0.5f), modifier = Modifier.padding(bottom = 16.dp))

                    // Gauge & Metrics Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            FluencyGauge(score = session.fluencyScore, size = 96.dp)
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            MetricBlock(label = "Speech Rate", value = "${session.speechRate.toInt()}", unit = "wpm", color = Color(0xFF6366F1))
                            MetricBlock(label = "Disfluencies", value = "${session.disfluencies.size}", unit = "events", color = Color(0xFFF59E0B))
                            MetricBlock(label = "Pauses", value = "${session.pauses}", unit = "total", color = Color(0xFFEC4899))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Transcript
                    Text(text = "TRANSCRIPT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF))
                    Text(
                        text = session.transcript,
                        fontSize = 12.sp,
                        color = Color(0xFF374151),
                        modifier = Modifier.padding(top = 4.dp),
                        lineHeight = 16.sp
                    )

                    // Disfluency pills timeline
                    if (session.disfluencies.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "DISFLUENCY TIMELINE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF))
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            session.disfluencies.forEach { ev ->
                                val pillColor = when (ev.event) {
                                    "block" -> Color(0xFFEF4444)
                                    "word_rep", "repetition" -> Color(0xFFF59E0B)
                                    "sound_rep" -> Color(0xFFF97316)
                                    "prolongation" -> Color(0xFF8B5CF6)
                                    "pause" -> Color(0xFF6366F1)
                                    else -> Color(0xFF9CA3AF)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(pillColor.copy(alpha = 0.1f))
                                        .border(1.dp, pillColor.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Text(text = ev.event.replace("_", " ").uppercase(), color = pillColor, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                        if (ev.word != null) {
                                            Text(text = "\"${ev.word}\"", color = pillColor, fontSize = 8.sp, fontWeight = FontWeight.SemiBold)
                                        }
                                        Text(text = "@${ev.time}", color = pillColor.copy(alpha = 0.5f), fontSize = 7.sp)
                                    }
                                }
                            }
                        }
                    }

                    // Notes
                    if (session.notes.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "THERAPIST REMARKS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF))
                        Text(
                            text = session.notes,
                            fontSize = 12.sp,
                            color = Color(0xFF1B2B5E),
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 4.dp),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}
