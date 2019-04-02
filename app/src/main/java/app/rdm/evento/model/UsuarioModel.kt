package app.rdm.evento.model


import android.os.Parcel
import android.os.Parcelable

class UsuarioModel : Parcelable {

    var email: String? = null
    var token: String? = null

    constructor(parcel: Parcel) : this() {
        email = parcel.readString()
        token = parcel.readString()
    }

    constructor()
    constructor(email: String?, token: String?){
        this.email = email
        this.token = token
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(email)
        parcel.writeString(token)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UsuarioModel> {
        override fun createFromParcel(parcel: Parcel): UsuarioModel {
            return UsuarioModel(parcel)
        }

        override fun newArray(size: Int): Array<UsuarioModel?> {
            return arrayOfNulls(size)
        }
    }
}
