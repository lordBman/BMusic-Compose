package com.bsoft.compose.bmusic.data.serializers

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.bsoft.compose.bmusic.data.serializables.AppSettingsData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object AppSettingsDataSerializer : Serializer<AppSettingsData> {

    override val defaultValue: AppSettingsData = AppSettingsData()

    override suspend fun readFrom(input: InputStream): AppSettingsData =
        try {
            Json.decodeFromString<AppSettingsData>(
                input.readBytes().decodeToString()
            )
        } catch (serialization: SerializationException) {
            throw CorruptionException("Unable to read app settings", serialization)
        }

    override suspend fun writeTo(t: AppSettingsData, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(Json.encodeToString(t).encodeToByteArray())
        }
    }
}
