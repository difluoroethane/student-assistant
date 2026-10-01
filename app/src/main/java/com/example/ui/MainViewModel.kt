package com.example.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppRepository
import com.example.data.FirestoreService
import com.example.data.SyncResult
import com.example.data.UserProfile
import com.example.network.ConnectivityObserver
import com.example.update.AppUpdateChecker
import com.example.update.AppUpdateInfo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainViewModel(
    private val repository: AppRepository,
    private val connectivityObserver: ConnectivityObserver,
    private val firestoreService: FirestoreService
) : ViewModel() {
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Network Connectivity State Monitoring
    val networkStatus: StateFlow<ConnectivityObserver.NetworkStatus> =
        connectivityObserver.observe()
            .stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                connectivityObserver.getCurrentStatus()
            )

    // Cloud Sync State
    private val _syncResult = MutableStateFlow<SyncResult?>(null)
    val syncResult: StateFlow<SyncResult?> = _syncResult.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    // In-App Continuous Updates (OTA)
    private val _updateInfo = MutableStateFlow<AppUpdateInfo?>(null)
    val updateInfo: StateFlow<AppUpdateInfo?> = _updateInfo.asStateFlow()

    private val _isDownloadingUpdate = MutableStateFlow(false)
    val isDownloadingUpdate: StateFlow<Boolean> = _isDownloadingUpdate.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getUserProfile().collect { profile ->
                _userProfile.value = profile
                _isLoading.value = false
                if (profile != null) {
                    try {
                        firestoreService.syncUserProfileSilently(profile)
                    } catch (e: Exception) {
                        android.util.Log.e("MainViewModel", "Error syncing profile: ${e.message}", e)
                    }
                }
            }
        }
        // Background silent cloud synchronization on launch
        viewModelScope.launch {
            repository.syncWithCloud()
        }
        // Auto-update checker: check when online, and recheck if network becomes available
        if (connectivityObserver.getCurrentStatus().isOnline) {
            checkForUpdates()
        }
        viewModelScope.launch {
            networkStatus.collect { status ->
                if (status.isOnline) {
                    repository.syncWithCloud()
                    if (_updateInfo.value == null) {
                        checkForUpdates()
                    }
                }
            }
        }
    }

    fun checkForUpdates() {
        if (!connectivityObserver.getCurrentStatus().isOnline) {
            return
        }
        viewModelScope.launch {
            try {
                val update = AppUpdateChecker.checkForUpdates(firestoreService.getFirestoreInstance())
                _updateInfo.value = update
            } catch (e: Exception) {
                android.util.Log.w("MainViewModel", "Update check notice: ${e.message}")
            }
        }
    }

    fun dismissUpdate() {
        _updateInfo.value = null
    }

    fun startUpdateDownload(context: Context) {
        val info = _updateInfo.value ?: return
        _isDownloadingUpdate.value = true
        AppUpdateChecker.downloadAndInstall(
            context = context,
            updateInfo = info,
            onDownloadStarted = {
                _isDownloadingUpdate.value = true
            },
            onError = {
                _isDownloadingUpdate.value = false
            }
        )
    }

    fun triggerSync() {
        if (_isSyncing.value) return
        viewModelScope.launch {
            _isSyncing.value = true
            val result = repository.syncWithCloud()
            _syncResult.value = result
            _isSyncing.value = false
        }
    }

    fun saveOnboardingData(name: String, username: String, department: String, semester: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val formattedUsername = if (username.isNotBlank()) {
                    if (username.startsWith("@")) username else "@$username"
                } else {
                    "@AnonCowboy"
                }
                val profile = UserProfile(
                    name = name.ifBlank { null },
                    username = formattedUsername,
                    department = department,
                    semester = semester,
                    notificationsEnabled = true,
                    themeMode = "Light"
                )
                repository.insertUserProfile(profile)
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "saveOnboardingData failed: ${e.message}", e)
            }
        }
    }

    fun updateUsername(newUsername: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val current = _userProfile.value ?: return@launch
                val formatted = if (newUsername.startsWith("@")) newUsername else "@$newUsername"
                val updated = current.copy(username = formatted)
                repository.insertUserProfile(updated)
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "updateUsername failed: ${e.message}", e)
            }
        }
    }
}
