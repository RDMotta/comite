package app.rdm.evento.controles

import android.content.Context
import android.support.v7.widget.RecyclerView
import app.rdm.evento.OnInteresseEventoListener
import app.rdm.evento.R
import app.rdm.evento.adapter.EventoAdapter
import app.rdm.evento.adapter.EventoUsuarioAdapter
import app.rdm.evento.model.EventoModel
import app.rdm.evento.model.EventosUsuarioModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import java.util.stream.Collectors

class EventoControle private constructor() {


    companion object {

        fun consultarTodos(eventoReference: FirebaseFirestore, context: Context, listener: OnInteresseEventoListener, recyclerView: RecyclerView) {
            val tableEvents = context.getString(R.string.table_events)
            val eventoReferencia = eventoReference.collection(tableEvents)
                    .whereEqualTo("ativo", true)
                    .limit(50)

            val options = FirestoreRecyclerOptions.Builder<EventoModel>()
                    .setQuery(eventoReferencia, EventoModel::class.java)
                    .build()

            var adapterEventos = EventoAdapter(options, listener)
            adapterEventos.startListening()
            recyclerView.adapter = adapterEventos
        }

        fun consultarMeusEventos(token: String, eventoReference: FirebaseFirestore, context: Context, listener: OnInteresseEventoListener, recyclerView: RecyclerView) {
            val tableEvents = context.getString(R.string.table_events)
            recyclerView.adapter = null

            val eventoReferencia = eventoReference.collection(tableEvents)
                    .whereEqualTo("ativo", true)
                    .whereEqualTo("listaTokens." + token, token)
                    .limit(50)

            val options = FirestoreRecyclerOptions.Builder<EventoModel>()
                    .setQuery(eventoReferencia, EventoModel::class.java)
                    .build()

            var adapterEventos = EventoUsuarioAdapter(options, listener)
            adapterEventos.startListening()
            recyclerView.adapter = adapterEventos
        }

        fun removerEvento(token: String, eventoReference: FirebaseFirestore, context: Context, model: EventoModel) {
            val tableEvents = context.getString(R.string.table_events)
            val column = "ativo"

            eventoReference.collection(tableEvents)
                    .whereEqualTo(column, true)
                    .get()
                    .addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
                        if (!task.isSuccessful) {
                            return@OnCompleteListener
                        }
                        if (task.result.documentChanges.size == 0) {
                            return@OnCompleteListener
                        }

                        if (task.result.isEmpty) {
                            return@OnCompleteListener
                        }

                        for (eventos in task.result.documentChanges) {
                            if (!eventos.document.exists()) {
                                break
                            }

                            val evento = eventos.document.toObject(EventoModel::class.java)

                            if (evento.titulo.equals(model.titulo)
                                    && evento.detalhe.equals(model.detalhe)
                                    && evento.thumbnailevento.equals(model.thumbnailevento)
                                    && (evento.previsao == model.previsao)
                                    && (evento.ativo == model.ativo)) {

                                val lista = hashMapOf<String,String>()

                                for (item in evento.listaTokens!!.entries) {
                                    if (item.value != token) {
                                        lista.put(item.key, item.value)

                                    }
                                }
                                evento.listaTokens = lista
                                eventoReference.collection(tableEvents)
                                        .document(eventos.document.id)
                                       // .set(evento, SetOptions.merge())
                                        .update("listaTokens",evento.listaTokens!!)
                            }
                        }
                    })
        }

        fun addEvento(token: String, eventoReference: FirebaseFirestore, context: Context, model: EventoModel) {

            val tableEvents = context.getString(R.string.table_events)
            val column = "ativo"

            eventoReference.collection(tableEvents)
                    .whereEqualTo(column, true)
                    .get()
                    .addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
                        if (!task.isSuccessful) {
                            return@OnCompleteListener
                        }
                        if (task.result.documentChanges.size == 0) {
                            return@OnCompleteListener
                        }

                        if (task.result.isEmpty) {
                            eventoReference.collection(tableEvents)
                                    .document()
                                    .set(model, SetOptions.merge())
                            return@OnCompleteListener
                        }

                        for (eventos in task.result.documentChanges) {
                            if (!eventos.document.exists()) {
                                break
                            }

                            val evento = eventos.document.toObject(EventoModel::class.java)

                            if (evento.titulo.equals(model.titulo)
                                    && evento.detalhe.equals(model.detalhe)
                                    && evento.thumbnailevento.equals(model.thumbnailevento)
                                    && (evento.previsao == model.previsao)
                                    && (evento.ativo == model.ativo)) {


                                val lista = HashMap<String,String>()

                                for (item in evento.listaTokens!!.entries) {
                                    if (!item.key.equals(token, true)) {
                                        lista.put(item.key, item.value)

                                    }
                                }
                                lista.put(token, token)
                                evento.listaTokens = lista
                                eventoReference.collection(tableEvents)
                                        .document(eventos.document.id)
                                        .update("listaTokens",evento.listaTokens!!)

                            }
                        }
                    })
        }
    }

}