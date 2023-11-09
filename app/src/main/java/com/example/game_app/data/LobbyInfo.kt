package com.example.game_app.data

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import java.io.Serializable

data class LobbyInfo (
    val lobbyName: String,
    val lobbyUid: String,
    val ownerIp: String,
    var players: MutableList<PlayerInfo>,
    val maxPlayerCount:Int,
    val gamemode:String,
    val gamemodeId: Int,
    val connection:String,
) : Serializable
// : Parcelable {
//    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
//    constructor(parcel: Parcel) : this(
//        parcel.readString() ?: "",
//        parcel.readString() ?: "",
//        parcel.readString() ?: "",
//        mutableListOf<PlayerInfo>().apply {
//            parcel.readList(this, PlayerInfo::class.java.classLoader,PlayerInfo::class.java)
//        },
//        parcel.readInt(),
//        parcel.readString() ?: "",
//        parcel.readInt(),
//        parcel.readString() ?: ""
//    )
//
//    override fun writeToParcel(parcel: Parcel, flags: Int) {
//        parcel.writeString(lobbyName)
//        parcel.writeString(lobbyUid)
//        parcel.writeString(ownerIp)
//        parcel.writeList(players)
//        parcel.writeInt(maxPlayerCount)
//        parcel.writeString(gamemode)
//        parcel.writeInt(gamemodeId)
//        parcel.writeString(connection)
//    }
//
//    override fun describeContents(): Int {
//        return 0
//    }
//
//    companion object CREATOR : Parcelable.Creator<LobbyInfo> {
//        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
//        override fun createFromParcel(parcel: Parcel): LobbyInfo {
//            return LobbyInfo(parcel)
//        }
//
//        override fun newArray(size: Int): Array<LobbyInfo?> {
//            return arrayOfNulls(size)
//        }
//    }
//}
