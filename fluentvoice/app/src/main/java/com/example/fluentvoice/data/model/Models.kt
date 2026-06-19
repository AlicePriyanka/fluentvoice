package com.example.fluentvoice.data.model

data class DisfluencyEvent(
    val event: String, // "block", "word_rep", "sound_rep", "prolongation", "interjection", "pause", "unknown"
    val time: String,
    val word: String? = null,
    val duration: Double? = null
)

data class Session(
    val id: String,
    val patientId: String,
    val patientName: String,
    val date: String,
    val fluencyScore: Int,
    val severity: String, // "mild", "moderate", "severe"
    val speechRate: Double,
    val transcript: String,
    val disfluencies: List<DisfluencyEvent>,
    val pauses: Int,
    val notes: String,
    val audioUrl: String? = null
)

data class Patient(
    val id: String,
    val name: String,
    val age: Int,
    val joinedDate: String,
    val therapistId: String,
    val condition: String,
    val treatmentGoals: List<String> = emptyList(),
    val practiceExercises: List<String> = emptyList(),
    val treatmentRemarks: String = "",
    val nextAppointment: String = "",
    val sessionsCount: Int = 0,
    val avgFluency: Int = 0,
    val trend: String = "stable" // "improving", "stable", "declining"
)

data class Appointment(
    val id: String,
    val patientId: String,
    val therapistId: String,
    val patientName: String,
    val date: String,           // "YYYY-MM-DD"
    val time: String,           // "HH:MM"
    val durationMinutes: Int,
    val type: String,           // "in-clinic", "telehealth"
    var status: String,         // "pending", "confirmed", "cancelled"
    val notes: String
)

data class User(
    val id: String,
    val email: String,
    val name: String,
    val role: String,           // "patient", "therapist"
    val therapistId: String? = null,
    val joinedDate: String
)

data class Profile(
    val userId: String,
    val role: String,
    val phone: String? = null,
    val age: Int? = null,
    val condition: String? = null,
    val bio: String? = null,
    val specialty: String? = null,
    val licenseNumber: String? = null,
    val clinicName: String? = null
)

data class TreatmentPlan(
    val patientId: String,
    val therapistId: String? = null,
    val goals: List<String> = emptyList(),
    val exercises: List<String> = emptyList(),
    val remarks: String = ""
)
