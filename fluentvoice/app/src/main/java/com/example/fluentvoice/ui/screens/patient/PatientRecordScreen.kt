package com.example.fluentvoice.ui.screens.patient

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.fluentvoice.data.model.DisfluencyEvent
import com.example.fluentvoice.data.model.Session
import com.example.fluentvoice.data.repository.FluentVoiceRepository
import com.example.fluentvoice.ui.components.AudioVisualizer
import com.example.fluentvoice.ui.components.FluencyGauge
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "PatientRecordScreen"

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PatientRecordScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val user by FluentVoiceRepository.currentUser.collectAsState()

    var stage by remember { mutableStateOf("idle") } // "idle", "recording", "too_short", "done", "analyzing", "results", "error"
    var elapsed by remember { mutableStateOf(0) }
    var tempFile by remember { mutableStateOf<File?>(null) }
    var result by remember { mutableStateOf<Session?>(null) }
    var errorMsg by remember { mutableStateOf("") }
    
    var mediaRecorder by remember { mutableStateOf<MediaRecorder?>(null) }

    // Request Mic permission launcher
    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasMicPermission = isGranted
        if (!isGranted) {
            errorMsg = "mic_denied"
            stage = "error"
        }
    }

    // Recording duration timer
    LaunchedEffect(stage) {
        if (stage == "recording") {
            elapsed = 0
            while (stage == "recording") {
                delay(1000)
                elapsed++
            }
        }
    }

    // Helper formatting mm:ss
    fun formatTime(sec: Int): String {
        val m = sec / 60
        val s = sec % 60
        return String.format("%02d:%02d", m, s)
    }

    fun startRecording() {
        if (!hasMicPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            return
        }

        try {
            stage = "recording"
            errorMsg = ""
            elapsed = 0

            val cacheDir = context.cacheDir
            tempFile = File.createTempFile("fluentvoice_rec", ".wav", cacheDir)

            val mr = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            mr.setAudioSource(MediaRecorder.AudioSource.MIC)
            mr.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mr.setOutputFile(tempFile?.absolutePath)
            mr.prepare()
            mr.start()
            mediaRecorder = mr
            Log.d(TAG, "Recording started at: ${tempFile?.absolutePath}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start MediaRecorder", e)
            errorMsg = "We could not access your microphone. Please check settings."
            stage = "error"
        }
    }

    fun stopRecording() {
        try {
            mediaRecorder?.let {
                it.stop()
                it.release()
            }
            mediaRecorder = null
            Log.d(TAG, "Recording stopped. Size: ${tempFile?.length()} bytes")

            if (elapsed < 5) {
                stage = "too_short"
            } else {
                stage = "done"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop MediaRecorder", e)
            stage = "too_short" // Safe recovery
        }
    }

    fun reset() {
        mediaRecorder?.let {
            try { it.stop() } catch (ex: Exception) {}
            it.release()
        }
        mediaRecorder = null
        tempFile?.delete()
        tempFile = null
        stage = "idle"
        elapsed = 0
        result = null
        errorMsg = ""
    }

    fun triggerAnalysis() {
        val file = tempFile ?: return
        val currentPatientId = user?.id ?: "p1"
        val currentPatientName = user?.name ?: "Arjun Kumar"

        stage = "analyzing"
        coroutineScope.launch {
            try {
                // Perform network upload with automatic simulation fallback
                val sessionRes = FluentVoiceRepository.analyzeAudio(file, currentPatientId, currentPatientName)
                result = sessionRes
                stage = "results"
            } catch (e: Exception) {
                errorMsg = "Speech analysis failed. Please check internet connection."
                stage = "error"
            }
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE9EFF9)) // BgColor
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // IDLE STATE
        if (stage == "idle") {
            Text(
                text = "Ready to record?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1B2B5E)
            )
            Text(
                text = "Speak naturally for at least 30 seconds. We'll analyze your disfluency once you are done.",
                fontSize = 13.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 48.dp)
            )

            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(Color(0xFF1B2B5E))
                    .clickable { startRecording() }
                    .border(4.dp, Color.White, RoundedCornerShape(100.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Start",
                    tint = Color.White,
                    modifier = Modifier.size(56.dp)
                )
            }

            Text(
                text = "Tap mic to start",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF9CA3AF),
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        // RECORDING STATE
        if (stage == "recording") {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color(0xFFEF4444).copy(alpha = 0.1f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color(0xFFEF4444))
                )
                Text(
                    text = "RECORDING",
                    color = Color(0xFFEF4444),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = formatTime(elapsed),
                fontSize = 54.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1B2B5E),
                modifier = Modifier.padding(vertical = 24.dp)
            )

            // Animated Audio wave visualizer
            AudioVisualizer(isRecording = true, modifier = Modifier.clip(RoundedCornerShape(16.dp)))

            Spacer(modifier = Modifier.height(24.dp))

            // Limit warning progress
            if (elapsed < 5) {
                Text(
                    text = "Speak for at least 5s (${elapsed}/5s)",
                    color = Color(0xFFF59E0B),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            } else {
                Text(
                    text = "Minimum duration reached",
                    color = Color(0xFF10B981),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(Color(0xFFEF4444))
                    .clickable { stopRecording() }
                    .border(4.dp, Color.White, RoundedCornerShape(100.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Square,
                    contentDescription = "Stop",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // TOO SHORT STATE
        if (stage == "too_short") {
            Text(text = "⏱️", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Recording too short",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1B2B5E)
            )
            Text(
                text = "We need at least 5 seconds of speech to run an accurate analysis.",
                fontSize = 13.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp, bottom = 24.dp)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { reset() },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFC8D3E8))
                ) {
                    Text(text = "Cancel", color = Color(0xFF64748B))
                }
                Button(
                    onClick = { startRecording() },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B2B5E))
                ) {
                    Text(text = "Record again", color = Color.White)
                }
            }
        }

        // RECORDING DONE STATE
        if (stage == "done") {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(Color(0xFF10B981)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Speech captured",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1B2B5E)
            )
            Text(
                text = "Ready to send audio to the AI analysis model. This will take about 15-30 seconds.",
                fontSize = 12.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { reset() },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFC8D3E8)),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = Color(0xFF1B2B5E))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Re-record", color = Color(0xFF1B2B5E))
                }

                Button(
                    onClick = { triggerAnalysis() },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B2B5E)),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Analyze", color = Color.White)
                }
            }
        }

        // ANALYZING STATE
        if (stage == "analyzing") {
            CircularProgressIndicator(
                color = Color(0xFF1B2B5E),
                strokeWidth = 4.dp,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Analyzing speech...",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1B2B5E)
            )
            Text(
                text = "Whisper AI model is transcribing and mapping disfluencies.",
                fontSize = 12.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // ERROR STATE
        if (stage == "error") {
            Text(text = "⚠️", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Something went wrong",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1B2B5E)
            )
            Text(
                text = if (errorMsg == "mic_denied") "Microphone access is blocked. Please enable recording permissions in Settings." else errorMsg,
                fontSize = 13.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp, bottom = 24.dp)
            )

            Button(
                onClick = { reset() },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B2B5E))
            ) {
                Text(text = "Try again", color = Color.White)
            }
        }

        // RESULTS STATE
        if (stage == "results" && result != null) {
            val report = result!!
            Text(
                text = "Your Report",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1B2B5E),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Score Banner Gauge
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFC8D3E8))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        FluencyGauge(score = report.fluencyScore, size = 110.dp)
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        MetricBlock(label = "Speech Rate", value = "${report.speechRate.toInt()}", unit = "wpm", color = Color(0xFF6366F1))
                        MetricBlock(label = "Disfluencies", value = "${report.disfluencies.size}", unit = "events", color = Color(0xFFF59E0B))
                        MetricBlock(label = "Pauses", value = "${report.pauses}", unit = "total", color = Color(0xFFEC4899))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Semantic message box
            val (sevInfo, sevColors) = when (report.severity) {
                "mild" -> Triple("🌟", "Your speech is flowing well", "Excellent fluency. Most of your speech is smooth and natural. Keep up practice.") to Triple(Color(0xFFE6F7F0), Color(0xFF10B981).copy(alpha = 0.2f), Color(0xFF059669))
                "moderate" -> Triple("📈", "You are making progress", "Moderate disfluency is common and responds well to consistent pacing practice.") to Triple(Color(0xFFFFF9E6), Color(0xFFF59E0B).copy(alpha = 0.2f), Color(0xFFB45309))
                else -> Triple("💪", "Every session moves you forward", "Captured disfluency events. Practice easy-onset breathing to reduce blocking frequency.") to Triple(Color(0xFFFEE2E2), Color(0xFFEF4444).copy(alpha = 0.2f), Color(0xFFB91C1C))
            }
            val (sevEmoji, sevHeading, sevBody) = sevInfo
            val (sevBg, sevBorder, sevTxtColor) = sevColors

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(sevBg)
                    .border(1.dp, sevBorder, RoundedCornerShape(16.dp))
                    .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(text = sevEmoji, fontSize = 20.sp)
                Column {
                    Text(text = sevHeading, fontWeight = FontWeight.Bold, color = sevTxtColor, fontSize = 13.sp)
                    Text(text = sevBody, color = sevTxtColor.copy(alpha = 0.8f), fontSize = 11.sp, lineHeight = 15.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Transcript Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFC8D3E8))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "TRANSCRIPT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF))
                    Text(
                        text = report.transcript,
                        fontSize = 13.sp,
                        color = Color(0xFF374151),
                        modifier = Modifier.padding(top = 8.dp),
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Disfluency pills list
            if (report.disfluencies.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFC8D3E8))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "DISFLUENCY EVENTS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF))
                        Spacer(modifier = Modifier.height(12.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            report.disfluencies.forEach { ev ->
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
                                        .border(1.dp, pillColor.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(text = ev.event.replace("_", " ").uppercase(), color = pillColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        if (ev.word != null) {
                                            Text(text = "\"${ev.word}\"", color = pillColor, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                                        }
                                        Text(text = "@${ev.time}", color = pillColor.copy(alpha = 0.6f), fontSize = 8.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { reset() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B2B5E))
            ) {
                Text(text = "Done", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MetricBlock(label: String, value: String, unit: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color(0xFF9CA3AF), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.Bottom) {
            Text(text = value, color = color, fontSize = 13.sp, fontWeight = FontWeight.Black)
            Text(text = " $unit", color = Color(0xFF9CA3AF), fontSize = 9.sp)
        }
    }
}
