package com.example.fluentvoice.ui.screens.therapist

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fluentvoice.data.model.Patient
import com.example.fluentvoice.data.model.Session
import com.example.fluentvoice.data.model.TreatmentPlan
import com.example.fluentvoice.data.repository.FluentVoiceRepository
import com.example.fluentvoice.ui.screens.patient.SessionItemCard
import kotlinx.coroutines.launch

@Composable
fun TherapistPatientDetailsScreen(
    patientId: String,
    onNavigateBack: () -> Unit
) {
    val patients by FluentVoiceRepository.patients.collectAsState()
    val sessions by FluentVoiceRepository.sessions.collectAsState()

    val patient = patients.find { it.id == patientId }
    val patientSessions = sessions.filter { it.patientId == patientId }

    if (patient == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Patient not found.", color = Color(0xFF64748B))
        }
        return
    }

    var selectedSectionTab by remember { mutableStateOf(0) } // 0 = Treatment Plan, 1 = Recorded Sessions

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE9EFF9)) // BgColor
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Back Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF1B2B5E))
            }
            Text(
                text = "Patient Profile",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B2B5E)
            )
        }

        // Patient summary card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFC8D3E8))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = patient.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B2B5E))
                Text(
                    text = "Age: ${patient.age} · Condition: ${patient.condition}",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    modifier = Modifier.padding(top = 2.dp)
                )

                Divider(color = Color(0xFFC8D3E8).copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "AVERAGE FLUENCY", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF))
                        Text(text = "${patient.avgFluency}%", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B2B5E))
                    }
                    Column {
                        Text(text = "TOTAL RECORDINGS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF))
                        Text(text = "${patient.sessionsCount} sessions", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B2B5E))
                    }
                    Column {
                        Text(text = "ACTIVE TREND", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF))
                        Text(text = patient.trend.uppercase(), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B2B5E))
                    }
                }
            }
        }

        // Tab selection for edit form vs session logs
        TabRow(
            selectedTabIndex = selectedSectionTab,
            containerColor = Color.Transparent,
            contentColor = Color(0xFF1B2B5E),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFDAE3F2))
                .padding(2.dp)
        ) {
            Tab(
                selected = selectedSectionTab == 0,
                onClick = { selectedSectionTab = 0 },
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selectedSectionTab == 0) Color.White else Color.Transparent)
            ) {
                Text(text = "Treatment Plan", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 10.dp))
            }
            Tab(
                selected = selectedSectionTab == 1,
                onClick = { selectedSectionTab = 1 },
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selectedSectionTab == 1) Color.White else Color.Transparent)
            ) {
                Text(text = "Session History", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 10.dp))
            }
        }

        if (selectedSectionTab == 0) {
            // Edit Treatment plan layout
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                TreatmentPlanEditPanel(patient = patient)
            }
        } else {
            // Recorded Session listing
            if (patientSessions.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(text = "No sessions recorded by this patient.", color = Color(0xFF64748B), fontSize = 13.sp)
                }
            } else {
                var expandedId by remember { mutableStateOf<String?>(null) }
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f).fillMaxWidth()
                ) {
                    items(patientSessions) { session ->
                        val isExp = expandedId == session.id
                        SessionItemCard(
                            session = session,
                            isExpanded = isExp,
                            onToggleExpand = { expandedId = if (isExp) null else session.id }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TreatmentPlanEditPanel(patient: Patient) {
    val coroutineScope = rememberCoroutineScope()
    val goals = remember { mutableStateListOf<String>() }
    val exercises = remember { mutableStateListOf<String>() }
    var remarks by remember { mutableStateOf("") }

    LaunchedEffect(patient.id) {
        val plan = FluentVoiceRepository.getTreatmentPlan(patient.id)
        goals.clear()
        plan?.goals?.let { goals.addAll(it) }
        exercises.clear()
        plan?.exercises?.let { exercises.addAll(it) }
        remarks = plan?.remarks ?: ""
    }

    var newGoalText by remember { mutableStateOf("") }
    var newExerciseText by remember { mutableStateOf("") }
    var successMsg by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFC8D3E8))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Success feedback
                if (successMsg.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF10B981).copy(alpha = 0.1f)),
                        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.2f))
                    ) {
                        Text(text = successMsg, color = Color(0xFF059669), fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))
                    }
                }

                // Edit Goals Section
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "TREATMENT GOALS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))

                    goals.forEach { goal ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "• $goal", fontSize = 12.sp, color = Color(0xFF374151), modifier = Modifier.weight(1f))
                            IconButton(onClick = { goals.remove(goal); successMsg = "" }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Remove", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newGoalText,
                            onValueChange = { newGoalText = it },
                            placeholder = { Text("Add new goal...") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF1B2B5E),
                                unfocusedBorderColor = Color(0xFFC8D3E8)
                            ),
                            singleLine = true
                        )
                        Button(
                            onClick = {
                                if (newGoalText.isBlank()) return@Button
                                goals.add(newGoalText.trim())
                                newGoalText = ""
                                successMsg = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B2B5E)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = "Add", color = Color.White)
                        }
                    }
                }

                Divider(color = Color(0xFFC8D3E8).copy(alpha = 0.5f))

                // Edit Exercises Section
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "PRACTICE EXERCISES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))

                    exercises.forEach { ex ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "• $ex", fontSize = 12.sp, color = Color(0xFF374151), modifier = Modifier.weight(1f))
                            IconButton(onClick = { exercises.remove(ex); successMsg = "" }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Remove", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newExerciseText,
                            onValueChange = { newExerciseText = it },
                            placeholder = { Text("Add practice drill...") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF1B2B5E),
                                unfocusedBorderColor = Color(0xFFC8D3E8)
                            ),
                            singleLine = true
                        )
                        Button(
                            onClick = {
                                if (newExerciseText.isBlank()) return@Button
                                exercises.add(newExerciseText.trim())
                                newExerciseText = ""
                                successMsg = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B2B5E)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = "Add", color = Color.White)
                        }
                    }
                }

                Divider(color = Color(0xFFC8D3E8).copy(alpha = 0.5f))

                // Therapist Notes
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "CLINICAL REMARKS / NOTES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                    OutlinedTextField(
                        value = remarks,
                        onValueChange = { remarks = it; successMsg = "" },
                        placeholder = { Text("Type therapeutic observations...") },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1B2B5E),
                            unfocusedBorderColor = Color(0xFFC8D3E8)
                        )
                    )
                }

                // Action Save Button
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val planObj = TreatmentPlan(
                                patientId = patient.id,
                                therapistId = "t1",
                                goals = goals.toList(),
                                exercises = exercises.toList(),
                                remarks = remarks.trim()
                            )
                            FluentVoiceRepository.saveTreatmentPlan(planObj)
                            successMsg = "✓ Treatment plan saved successfully."
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B2B5E))
                ) {
                    Text(text = "Save Updates", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
