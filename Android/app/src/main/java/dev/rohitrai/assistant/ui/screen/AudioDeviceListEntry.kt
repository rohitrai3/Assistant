package dev.rohitrai.assistant.ui.screen

import android.media.AudioDeviceInfo
import android.media.AudioManager

class AudioDeviceListEntry(deviceId: Int, deviceName: String?) {
    private var mId = deviceId
    private var mName = deviceName

    fun getId(): Int {
        return mId
    }

    fun getName(): String? {
        return mName
    }

    override fun equals(o: Any?): Boolean {
        if (this == o) return true;
        if (o == null || javaClass != o.javaClass) return false

        val that = o as AudioDeviceListEntry

        if (mId != that.mId) return false;

        return mName?.equals(that.mName) ?: (that.mName == null);
    }

    override fun hashCode(): Int {
        var result = mId
        result = 31 * result + (mName?.hashCode() ?: 0)

        return result
    }

    companion object {
        fun createListFrom(devices: List<AudioDeviceInfo>, directionType: Int) {
            val listEntries = mutableListOf<AudioDeviceListEntry>()

            for (info in devices) {
                if (directionType == AudioManager.GET_DEVICES_ALL ||
                    (directionType == AudioManager.GET_DEVICES_OUTPUTS && info.isSink) ||
                        (directionType == AudioManager.GET_DEVICES_INPUTS && info.isSource)) {
                    listEntries.add(AudioDeviceListEntry(info.id, "${info.productName} ${
                        AudioDeviceInfoConverter.typeToString(
                            info.type
                        )
                    }"));
                }
            }
        }
    }
}