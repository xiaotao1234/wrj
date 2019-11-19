package com.example.uav_client.Data.Common

class RequestBuildUtil {
    companion object {
        var ALARMSQUARE: Int = 1
        var UAV_ON_TOME_DATA: Int = 2
        var USER_LOGIN: Int = 3
        var USER_LOGIN_RESULT: Int = 4
        var ADD_USER: Int = 5
        var ADD_USER_RESULT: Int = 6
        var SEARCH_USER_LIST: Int = 7
        var SEARCH_USER_LIST_RESULT: Int = 8
        var SEARCH_DAILY: Int = 9
        var SEARCH_DAILY_RESULT: Int = 10
        var SEARCH_ALARM: Int = 11
        var SEARCH_ALARM_RESULT: Int = 12
        var ERROR:ByteArray = addFrameHeader("",-1)

        fun transformRequestToByte(): ByteArray {
            var byteArray: ByteArray
            byteArray = ByteArray(10)
            loop@ for (i in 0..9) {
                byteArray[i] = i.toByte()
            }
            return byteArray
        }

        fun addFrameHeader(s: String, order: Int): ByteArray {
            var byteArray = s.toByteArray()
            var byr = sumHex(0xEEEEEEEE, 4)
            var bym = sumHex((byteArray.size + 5).toLong(), 4)
            var byc = sumHex(order.toLong(), 1)
            var bys = ByteArray(4 + 4 + 1 + byteArray.size)
            System.arraycopy(byr, 0, bys, 0, byr.size)
            System.arraycopy(bym, 0, bys, 4, bym.size)
            System.arraycopy(byc, 0, bys, 8, byc.size)
            System.arraycopy(byteArray, 0, bys, 9, byteArray.size)
            return bys
        }

        fun unPack(byteArray: ByteArray): ByteArray? {
//            if(nigetPartByteArray(byteArray,0,3) == sumHex(0xEEEEEEEE,4)){
            var byteArray1 = byteArray.copyOfRange(4,8)
            var length = bytesToIntLittle(byteArray.copyOfRange(4, 8))
            return byteArray.copyOfRange(9, (9 + length - 5))
        }

        @JvmStatic
        fun unPackrequestCode(byteArray: ByteArray,int: Int):Int{
            return byteArray[int].toInt() and 0xFF
        }

        fun int2ByteArray(i: Long): ByteArray {
            val result = ByteArray(4)
            result[0] = (i shr 24 and 0xFF).toByte()
            result[1] = (i shr 16 and 0xFF).toByte()
            result[2] = (i shr 8 and 0xFF).toByte()
            result[3] = (i and 0xFF).toByte()
            return result
        }

        fun sumHex(tu5: Long, length: Int): ByteArray {
            var length = length
            val bytes5 = ByteArray(length)
            while (length > 0) {
                length--
                bytes5[length] = (tu5 shr 8 * (bytes5.size - length - 1) and 0xFF).toByte()
            }
            return bytes5
        }

        fun nigetPartByteArray(b: ByteArray, start: Int, stop: Int): ByteArray {
            val c = ByteArray(stop - start + 1)
            for (i in stop downTo start) {
                c[stop - i] = b[i]
            }
            return c
        }

        fun bytesToIntLittle(src: ByteArray): Int {
            val value: Int
            value = (src[0].toInt() and 0xFF
                    or (src[1].toInt() and 0xFF shl 8)
                    or (src[2].toInt() and 0xFF shl 16)
                    or (src[3].toInt() and 0xFF shl 24))
            return value
        }

        fun fourBytesToLong(b: ByteArray): Long {
            var intValue = 0
            var f: Long = 0
            val c = b[0].toLong() and 0xff shl 24
            if (c < 0) {
                f = (c + Math.pow(2.0, 32.0)).toLong()
            } else {
                f = c.toLong()
            }
            for (i in 1 until b.size) {
                intValue += b[i].toInt() and 0xFF shl 8 * (3 - i)
            }
            return intValue + f
        }

    }

}