package com.bsoft.compose.bmusic.data.serializers

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.bsoft.compose.bmusic.data.serializables.EqualizerPreferencesData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object EqualizerPreferencesDataSerializer : Serializer<EqualizerPreferencesData> {

    override val defaultValue: EqualizerPreferencesData = EqualizerPreferencesData()

    override suspend fun readFrom(input: InputStream): EqualizerPreferencesData =
        try {
            Json.decodeFromString<EqualizerPreferencesData>(
                input.readBytes().decodeToString()
            )
        } catch (serialization: SerializationException) {
            throw CorruptionException("Unable to read equalizer preferences", serialization)
        }

    override suspend fun writeTo(t: EqualizerPreferencesData, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(Json.encodeToString(t).encodeToByteArray())
        }
    }
}