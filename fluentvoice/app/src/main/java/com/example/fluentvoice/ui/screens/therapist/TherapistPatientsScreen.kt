package com.example.fluentvoice.ui.screens.therapist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingUp
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
import com.example.fluentvoice.data.repository.FluentVoiceRepository

@Composable
fun TherapistPatientsScreen(
    onNavigateToPatientDetails: (patientId: String) -> Unit
) {
    val user by FluentVoiceRepository.currentUser.collectAsState()
    val patients by FluentVoiceRepository.patients.collectAsState()

    val myPatients = patients.filter { it.therapistId == user?.id }

    var searchQuery by remember { mutableStateOf("") }
    val filteredPatients = myPatients.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE9EFF9)) // BgColor
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "My Patients",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B2B5E)
        )

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search patients by name...") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color(0xFF9CA3AF))
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1B2B5E),
                unfocusedBorderColor = Color(0xFFC8D3E8),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            singleLine = true
        )

        if (filteredPatients.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No patients found.", color = Color(0xFF64748B), fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredPatients) { patient ->
                    PatientItemCard(
                        patient = patient,
                        onClick = { onNavigateToPatientDetails(patient.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun PatientItemCard(
    patient: Patient,
    onClick: () -> Unit
) {
    val trendColor = when (patient.trend) {
        "improving" -> Color(0xFF10B981)
        "declining" -> Color(0xFFEF4444)
        else -> Color(0xFFF59E0B)
    }
    val trendIcon = when (patient.trend) {
        "improving" -> Icons.Default.TrendingUp
        "declining" -> Icons.Default.TrendingDown
        else -> Icons.Default.TrendingFlat
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFC8D3E8))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = patient.name,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B2B5E),
                    fontSize = 14.sp
                )
                Text(
                    text = "Age: ${patient.age} · ${patient.condition}",
                    color = Color(0xFF64748B),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(
                        imageVector = trendIcon,
                        contentDescription = null,
                        tint = trendColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = patient.trend.uppercase(),
                        color = trendColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    )
                }
            }

            // Stats block
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${patient.avgFluency}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1B2B5E)
                    )
                    Text(
                        text = "%",
                        fontSize = 11.sp,
                        color = Color(0xFF9CA3AF),
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
                Text(
                    text = "AVG FLUENCY",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF9CA3AF)
                )
                Text(
                    text = "${patient.sessionsCount} sessions",
                    fontSize = 10.sp,
                    color = Color(0xFF64748B),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
