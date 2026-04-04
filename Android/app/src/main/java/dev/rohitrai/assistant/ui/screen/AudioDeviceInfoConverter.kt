package dev.rohitrai.assistant.ui.screen

import android.media.AudioDeviceInfo

class AudioDeviceInfoConverter {
    companion object {
        fun toString(adi: AudioDeviceInfo): String {
            val sb = StringBuilder()
            sb.append("Id: ")
            sb.append(adi.id)

            sb.append("\nProduct name: ")
            sb.append(adi.productName)

            sb.append("\nType: ")
            sb.append(typeToString(adi.type))

            sb.append("\nIs source: ")
            sb.append(if (adi.isSource) "Yes" else "No")

            sb.append("\nIs sink: ")
            sb.append(if (adi.isSink) "Yes" else "No")

            sb.append("\nChannel counts: ")
            val channelCounts = adi.channelCounts
            sb.append(intArrayToString(channelCounts))

            sb.append("\nChannel masks: ")
            val channelMasks = adi.channelMasks
            sb.append(intArrayToString(channelMasks))

            sb.append("\nChannel index masks: ")
            val channelIndexMasks = adi.channelIndexMasks
            sb.append(intArrayToString(channelIndexMasks))

            sb.append("\nEncodings: ")
            val encodings = adi.encodings
            sb.append(intArrayToString(encodings))

            sb.append("\nSample Rates: ")
            val sampleRates = adi.sampleRates
            sb.append(intArrayToString(sampleRates))

            return sb.toString()
        }

        fun intArrayToString(integerArray: IntArray): String {
            val sb = StringBuilder()

            for (i in 0..<integerArray.size) {
                sb.append(integerArray[i])

                if (i != integerArray.size - 1) sb.append(" ")
            }

            return sb.toString()
        }

        fun typeToString(type: Int): String {
            when (type) {
                AudioDeviceInfo.TYPE_AUX_LINE -> return "auxiliary line-level connectors"
                AudioDeviceInfo.TYPE_BLUETOOTH_A2DP -> return "Bluetooth device supporting the A2DP profile"
                AudioDeviceInfo.TYPE_BLUETOOTH_SCO -> return "Bluetooth device typically used for telephony"
                AudioDeviceInfo.TYPE_BUILTIN_EARPIECE -> return "built-in earphone speaker"
                AudioDeviceInfo.TYPE_BUILTIN_MIC -> return "built-in microphone"
                AudioDeviceInfo.TYPE_BUILTIN_SPEAKER -> return "built-in speaker"
                AudioDeviceInfo.TYPE_BUS -> return "BUS"
                AudioDeviceInfo.TYPE_DOCK -> return "DOCK"
                AudioDeviceInfo.TYPE_FM -> return "FM"
                AudioDeviceInfo.TYPE_FM_TUNER -> return "FM tuner"
                AudioDeviceInfo.TYPE_HDMI -> return "HDMI"
                AudioDeviceInfo.TYPE_HDMI_ARC -> return "HDMI audio return channel"
                AudioDeviceInfo.TYPE_IP -> return "IP"
                AudioDeviceInfo.TYPE_LINE_ANALOG -> return "line analog"
                AudioDeviceInfo.TYPE_LINE_DIGITAL -> return "line digital"
                AudioDeviceInfo.TYPE_TELEPHONY -> return "telephony"
                AudioDeviceInfo.TYPE_TV_TUNER -> return "TV tuner"
                AudioDeviceInfo.TYPE_USB_ACCESSORY -> return "USB accessory"
                AudioDeviceInfo.TYPE_USB_DEVICE -> return "USB device"
                AudioDeviceInfo.TYPE_WIRED_HEADPHONES -> return "wired headphones"
                AudioDeviceInfo.TYPE_WIRED_HEADSET -> return "wired headset"
                else -> return "unknown"
            }
        }
    }
}
