package com.matatov.movere.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import java.util.*

data class UserModel(
    var userId: String? = null,
    var userName: String? = null,
    var userUriPhoto: String? = null,
    var userTypeVehicle: Int = 0,
    var userToken: String? = null,
    var g: String? = null,  //geoHash
    var l: GeoPoint? = null,
    var userTimeStamp: Timestamp? = null
) : Parcelable {

    constructor(source: Parcel) : this(
        source.readString(),
        source.readString(),
        source.readString(),
        source.readInt(),
        source.readString(),
        source.readString(),
        GeoPoint(source.readDouble(), source.readDouble()),
        Timestamp(Date(source.readString()))
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(this.userId)
        dest?.writeString(this.userName)
        dest?.writeString(this.userUriPhoto)
        dest?.writeInt(this.userTypeVehicle)
        dest?.writeString(this.userToken)
        dest?.writeString(this.g)
        dest?.writeDouble(this.l!!.latitude)
        dest?.writeDouble(this.l!!.longitude)
        dest?.writeString(this.userTimeStamp!!.toDate().toString())
    }

    companion object CREATOR : Parcelable.Creator<UserModel> {
        override fun createFromParcel(parcel: Parcel): UserModel {
            return UserModel(parcel)
        }

        override fun newArray(size: Int): Array<UserModel?> {
            return arrayOfNulls(size)
        }
    }
}
