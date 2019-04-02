package app.rdm.evento

import android.support.v7.app.AppCompatActivity
import app.rdm.evento.model.EventoModel

interface OnInteresseEventoListener{
    fun setAdicionarEvento(model: EventoModel)
    fun setRemoverEvento(model: EventoModel)
}

//d08dcd0bd041dbb81f3dea50c8b80758e4df5165
open class EventoActivity : AppCompatActivity() {
}