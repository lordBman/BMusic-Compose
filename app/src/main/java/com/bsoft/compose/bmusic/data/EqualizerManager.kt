package com.bsoft.compose.bmusic.data

import android.media.audiofx.Equalizer
import android.util.Log
import com.bsoft.compose.bmusic.data.models.EqualizerBand
import com.bsoft.compose.bmusic.data.models.EqualizerPresent
import com.bsoft.compose.bmusic.data.preferences.EqualizerPreferences

class EqualizerManager{
    private var _equalizer: Equalizer? = null

    val equalizer: Equalizer?
        get() = _equalizer

    var enabled: Boolean
        get() = _equalizer?.enabled ?: false
        set(value){
            _equalizer?.apply { enabled = value }
        }

    val bandCount: Int
        get() = (_equalizer?.numberOfBands ?: 0).toInt()

    val bands: List<EqualizerBand>
        get(){
            return _equalizer?.let {
                Log.d("Equalizer log", "Number Equalizer Bands: ${it.numberOfBands}")
                val range = it.bandLevelRange
                val min = range[0]
                val max = range[1]

                (0..< it.numberOfBands).map { band->
                    Log.d("Equalizer log", "Getting Equalizer Band Details for : $band")
                    val level = it.getBandLevel(band.toShort())
                    val normalized = (level - min).toFloat() / (max - min).toFloat()
                    EqualizerBand(
                        band = band.toShort(),
                        frequency = it.getCenterFreq(band.toShort()) / 1000,
                        level = it.getBandLevel(band.toShort()),
                        min = min, max = max)
                }
            } ?: emptyList()
        }

    val presets: List<EqualizerPresent>
        get() = _equalizer?.let {
            Log.d("Equalizer log", "Number Equalizer Presets: ${it.numberOfPresets}")
            (0..< it.numberOfPresets).map { preset->
                Log.d("Equalizer log", "Getting Equalizer Preset Details for : $preset name(${it.getPresetName(preset.toShort())})")
                EqualizerPresent(preset = preset.toShort(), name = it.getPresetName(preset.toShort()))
            }
        } ?: emptyList()

    private var _listener: (()-> Unit)? = null
    private var _updateListener: (()-> Unit)? = null

    fun setListeners(listener: ()-> Unit, updateListener: ()-> Unit){
        _listener = listener
        _updateListener = updateListener
    }

    fun attach(audioSessionID: Int){
        _equalizer?.release()
        _equalizer = Equalizer(0, audioSessionID)
        _listener?.let {
            it()
        }

        _updateListener?.let {
            it()
        }
        _equalizer?.setControlStatusListener { audioEffect, bool ->
            Log.d("Equalizer log", "control status listener was called")
        }
        _equalizer?.setParameterListener{ effect, status, params, params2, value ->
            Log.d("Equalizer log", "parameter listener was called")
        }
    }

    fun release(){
        _equalizer?.release()
        _equalizer = null
    }

    fun choosePreset(preset: Short){
        _equalizer?.usePreset(preset)
        _updateListener?.let {
            it()
        }
    }

    fun modifyBand(band: Short, level: Short){
        _equalizer?.setBandLevel(band, level)
        _updateListener?.let {
            it()
        }
    }
}