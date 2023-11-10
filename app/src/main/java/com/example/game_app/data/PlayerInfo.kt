package com.example.game_app.data

import android.os.Parcel
import android.os.Parcelable
import kotlinx.serialization.Serializable


@Serializable
data class PlayerInfo (
    val username: String,
    val uid : String,
    val isHost: Boolean,
    val image: String?
):java.io.Serializable
//    : Parcelable {
//    constructor(parcel: Parcel) : this(
//        parcel.readString() ?: "",
//        parcel.readString() ?: "",
//        parcel.readByte() != 0.toByte(),
//        parcel.readString()
//    )
//
//    override fun writeToParcel(parcel: Parcel, flags: Int) {
//        parcel.writeString(username)
//        parcel.writeString(uid)
//        parcel.writeByte(if (isHost) 1 else 0)
//        parcel.writeString(image)
//    }
//
//    override fun describeContents(): Int {
//        return 0
//    }
//
//    companion object CREATOR : Parcelable.Creator<PlayerInfo> {
//        override fun createFromParcel(parcel: Parcel): PlayerInfo {
//            return PlayerInfo(parcel)
//        }
//
//        override fun newArray(size: Int): Array<PlayerInfo?> {
//            return arrayOfNulls(size)
//        }
//    }
//}