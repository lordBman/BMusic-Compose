package com.bsoft.compose.bmusic.data.preferences

import android.content.Context
import com.bsoft.compose.bmusic.data.datastores.equalizerPreferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class EqualizerPreferences(private val context: Context) {
    val isEqualizerEnabled: Flow<Boolean>
        get() = context.equalizerPreferencesDataStore.data.map{ data -> data.isEqualizerEnabled }

    val values: Flow<List<Short>>
        get() = context.equalizerPreferencesDataStore.data.map{ data -> data.values }

    val selected: Flow<Short>
        get() = context.equalizerPreferencesDataStore.data.map{ data -> data.selected }

    fun enableEqualizer(enable: Boolean = true) {
        CoroutineScope(Dispatchers.IO).launch{
            context.equalizerPreferencesDataStore.updateData { data -> data.copy(isEqualizerEnabled = enable) }
        }
    }

    fun setValues(values: List<Short>){
        CoroutineScope(Dispatchers.IO).launch{
            context.equalizerPreferencesDataStore.updateData { data -> data.copy(values = values) }
        }
    }

    fun setSelected(selected: Short = -1){
        CoroutineScope(Dispatchers.IO).launch{
            context.equalizerPreferencesDataStore.updateData { data -> data.copy(selected = selected) }
        }
    }
}