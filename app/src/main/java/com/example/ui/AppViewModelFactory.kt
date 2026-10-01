package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.AppDao
import com.example.data.AppRepository
import com.example.data.FirestoreService
import com.example.network.ConnectivityObserver
import com.example.network.ExternalNewsService

class AppViewModelFactory(
    private val repository: AppRepository,
    private val connectivityObserver: ConnectivityObserver,
    private val externalNewsService: ExternalNewsService,
    private val firestoreService: FirestoreService,
    private val dao: AppDao? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val appDao = dao ?: repository.dao
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository, connectivityObserver, firestoreService) as T
        }
        if (modelClass.isAssignableFrom(com.example.ui.home.HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return com.example.ui.home.HomeViewModel(repository, connectivityObserver, externalNewsService) as T
        }
        if (modelClass.isAssignableFrom(com.example.ui.notes.NotesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return com.example.ui.notes.NotesViewModel(repository, appDao) as T
        }
        if (modelClass.isAssignableFrom(com.example.ui.forum.ForumViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return com.example.ui.forum.ForumViewModel(repository, appDao) as T
        }
        if (modelClass.isAssignableFrom(com.example.ui.planner.PlannerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return com.example.ui.planner.PlannerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
