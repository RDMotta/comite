package app.rdm.evento.model

import android.os.Parcel
import android.os.Parcelable
import java.util.Date
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter.writeString



class EventoModel : Parcelable {

    var ativo: Boolean? = false
    var titulo: String? = null
    var detalhe: String? = null
    var previsao: Date? = null
    var thumbnailevento: String? = null
    var thumbnailgrupo: String? = null
    var listaTokens: Map<String,String>? = null

    constructor(parcel: Parcel) : this() {
        ativo = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        titulo = parcel.readString()
        detalhe = parcel.readString()
        previsao = parcel.readValue(Date::class.java.classLoader) as? Date
        thumbnailevento = parcel.readString()
        thumbnailgrupo = parcel.readString()
        listaTokens = parcel.readValue(hashMapOf<String, String>()::class.java.classLoader) as Map<String, String>
    }

    constructor()

    constructor(ativo: Boolean?, titulo: String?, detalhe: String?, previsao: Date?,
                thumbnailevento: String?, thumbnailgrupo: String?,
                listaTokens: Map<String,String>?) {
        this.ativo = ativo
        this.titulo = titulo
        this.detalhe = detalhe
        this.previsao = previsao
        this.thumbnailevento = thumbnailevento
        this.thumbnailgrupo = thumbnailgrupo
        this.listaTokens = listaTokens
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(ativo)
        parcel.writeString(titulo)
        parcel.writeString(detalhe)
        parcel.writeValue(previsao)
        parcel.writeString(thumbnailevento)
        parcel.writeString(thumbnailgrupo)
        parcel.writeValue(listaTokens)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EventoModel> {
        override fun createFromParcel(parcel: Parcel): EventoModel {
            return EventoModel(parcel)
        }

        override fun newArray(size: Int): Array<EventoModel?> {
            return arrayOfNulls(size)
        }
    }
}