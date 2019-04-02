package app.rdm.evento.model

import android.os.Parcel
import android.os.Parcelable
import java.util.*

class PerfilModel: Parcelable {

    var nome: String? = null
    var email: String? = null
    var cargo: String? = null
    var ativo: Boolean? = null
    var thumbnailperfil: String? = null

    constructor()

    constructor(nome: String?, email: String?, cargo: String?, thumbnailperfil: String?, ativo: Boolean?) {
        this.nome = nome
        this.email = email
        this.cargo = cargo
        this.ativo = ativo
        this.thumbnailperfil = thumbnailperfil
    }

    constructor(parcel: Parcel) : this() {
        nome = parcel.readString()
        email = parcel.readString()
        cargo = parcel.readString()
        ativo = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        thumbnailperfil = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nome)
        parcel.writeString(email)
        parcel.writeString(cargo)
        parcel.writeValue(ativo)
        parcel.writeString(thumbnailperfil)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PerfilModel> {
        override fun createFromParcel(parcel: Parcel): PerfilModel {
            return PerfilModel(parcel)
        }

        override fun newArray(size: Int): Array<PerfilModel?> {
            return arrayOfNulls(size)
        }
    }
}