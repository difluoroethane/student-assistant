package com.example.ui.planner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppRepository
import com.example.data.Assignment
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlannerViewModel(private val repository: AppRepository) : ViewModel() {
    val timetable = repository.getAllTimetableEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val assignments = repository.getAllAssignments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val exams = repository.getAllExams()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleAssignmentStatus(assignment: Assignment) {
        viewModelScope.launch {
            val newStatus = if (assignment.status == "Pending") "Submitted" else "Pending"
            repository.updateAssignment(assignment.copy(status = newStatus))
        }
    }
}
