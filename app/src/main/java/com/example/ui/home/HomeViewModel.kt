package com.example.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.Announcement
import com.example.data.AppRepository
import com.example.data.SyncResult
import com.example.network.AcademicCircular
import com.example.network.ConnectivityObserver
import com.example.network.ExternalNewsService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: AppRepository,
    private val connectivityObserver: ConnectivityObserver,
    private val externalNewsService: ExternalNewsService
) : ViewModel() {
    private val localAnnouncements = repository.getAllAnnouncements()
    private val cloudAnnouncements = repository.getCloudAnnouncements()

    private val _selectedCategory = MutableStateFlow<String?>("All")
    val selectedCategory: StateFlow<String?> = _selectedCategory

    // Experiment 5: Network Connectivity
    val networkStatus: StateFlow<ConnectivityObserver.NetworkStatus> =
        connectivityObserver.observe()
            .stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                connectivityObserver.getCurrentStatus()
            )

    // Experiment 4: Cloud Sync
    private val _syncResult = MutableStateFlow<SyncResult?>(null)
    val syncResult: StateFlow<SyncResult?> = _syncResult.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    // Experiment 5: REST External Data Exchange
    private val _externalNews = MutableStateFlow<List<AcademicCircular>>(emptyList())
    val externalNews: StateFlow<List<AcademicCircular>> = _externalNews.asStateFlow()

    private val _isLoadingNews = MutableStateFlow(false)
    val isLoadingNews: StateFlow<Boolean> = _isLoadingNews.asStateFlow()

    init {
        // Initial cloud sync
        syncWithCloud()
        // Initial external REST fetch
        refreshExternalNews()
    }

    val announcements = combine(localAnnouncements, cloudAnnouncements, _selectedCategory) { local, cloud, category ->
        val merged = mutableListOf<Announcement>()
        merged.addAll(local)
        cloud.forEach { ca ->
            if (merged.none { it.title.equals(ca.title, ignoreCase = true) }) {
                merged.add(
                    Announcement(
                        id = ca.id.hashCode(),
                        title = ca.title,
                        description = ca.description,
                        category = ca.category,
                        timestamp = ca.timestamp,
                        pinned = ca.pinned
                    )
                )
            }
        }
        val sorted = merged.sortedWith(compareByDescending<Announcement> { it.pinned }.thenByDescending { it.timestamp })
        if (category == "All" || category == null) sorted
        else sorted.filter { it.category.equals(category, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val nearestDeadline = repository.getAllAssignments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun syncWithCloud() {
        if (_isSyncing.value) return
        viewModelScope.launch {
            _isSyncing.value = true
            val result = repository.syncWithCloud()
            _syncResult.value = result
            _isSyncing.value = false
        }
    }

    fun refreshExternalNews() {
        viewModelScope.launch {
            _isLoadingNews.value = true
            val data = externalNewsService.fetchExternalAcademicData()
            _externalNews.value = data
            _isLoadingNews.value = false
        }
    }

    fun postAnnouncement(title: String, description: String, category: String, pinned: Boolean) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.postAnnouncement(
                title = title.trim(),
                description = description.trim(),
                category = category,
                pinned = pinned
            )
        }
    }
}
