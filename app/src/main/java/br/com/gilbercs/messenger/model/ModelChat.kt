package br.com.gilbercs.messenger.model

import android.os.Parcelable

class ModelChat(val id: String, val text: String, val oneId: String, val twoId: String, val timestamp: Long){
    constructor() : this("", "", "", "", -1)
}