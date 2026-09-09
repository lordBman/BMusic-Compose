package com.bsoft.compose.bmusic.data.datastores

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.bsoft.compose.bmusic.data.serializables.AppSettingsData
import com.bsoft.compose.bmusic.data.serializers.AppSettingsDataSerializer

val Context.appSettingsDataStore: DataStore<AppSettingsData> by dataStore(
    fileName = "app_settings.json",
    serializer = AppSettingsDataSerializer,
)
