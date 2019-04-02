package app.rdm.evento.model

import android.os.Parcel
import android.os.Parcelable

class EventosUsuarioModel : Parcelable{

    var ativo: Boolean? = false
    var eventos: ArrayList<EventoModel>? = null
    var usuarioToken: String? = null

    constructor(parcel: Parcel) : this() {
        ativo = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        usuarioToken = parcel.readString()
        eventos = parcel.readValue(ArrayList::class.java.classLoader) as ArrayList<EventoModel>
    }

    constructor()

    constructor(ativo: Boolean?, usuarioToken: String?, eventos: ArrayList<EventoModel>?){
        this.ativo = ativo
        this.usuarioToken = usuarioToken
        this.eventos = eventos
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(ativo)
        parcel.writeString(usuarioToken)
        parcel.writeValue(eventos)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EventosUsuarioModel> {
        override fun createFromParcel(parcel: Parcel): EventosUsuarioModel {
            return EventosUsuarioModel(parcel)
        }

        override fun newArray(size: Int): Array<EventosUsuarioModel?> {
            return arrayOfNulls(size)
        }
    }

}