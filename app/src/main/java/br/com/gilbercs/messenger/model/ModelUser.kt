package br.com.gilbercs.messenger.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class ModelUser(
    val id: String, val name: String, val email: String, val urlImg: String
): Parcelable {
    constructor(): this ("","","","")
}