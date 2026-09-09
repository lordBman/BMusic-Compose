package com.bsoft.compose.bmusic.data.datastores

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.bsoft.compose.bmusic.data.serializables.EqualizerPreferencesData
import com.bsoft.compose.bmusic.data.serializers.EqualizerPreferencesDataSerializer

val Context.equalizerPreferencesDataStore: DataStore<EqualizerPreferencesData> by dataStore(
    fileName = "equalizer_preferences.json",
    serializer = EqualizerPreferencesDataSerializer,
)