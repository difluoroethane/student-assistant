package com.example

import android.app.Application
import com.example.data.AppDatabase
import com.example.data.FirestoreService
import com.example.data.LocalAppRepository
import com.example.data.MockDataGenerator
import com.example.network.ExternalNewsService
import com.example.network.NetworkConnectivityObserver

class StudentAssistantApp : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val firestoreService by lazy { FirestoreService(this, database.appDao()) }
    val repository by lazy { LocalAppRepository(database.appDao(), firestoreService) }
    val connectivityObserver by lazy { NetworkConnectivityObserver(this) }
    val externalNewsService by lazy { ExternalNewsService() }

    override fun onCreate() {
        super.onCreate()
    }
}
