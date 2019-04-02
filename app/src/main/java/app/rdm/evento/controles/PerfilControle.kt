package app.rdm.evento.controles

import android.content.Context
import android.support.v7.widget.RecyclerView
import app.rdm.evento.R
import app.rdm.evento.adapter.PerfilAdapter
import app.rdm.evento.model.PerfilModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore

class PerfilControle private constructor() {

    companion object {

        fun consultarTodos(perfilReference: FirebaseFirestore, context: Context, recyclerView: RecyclerView) {
            val tableMembers = context.getString(R.string.table_members)
            val perfilReferencia = perfilReference.collection(tableMembers)
                    .whereEqualTo("ativo", true)
                    .limit(50)
            val options = FirestoreRecyclerOptions.Builder<PerfilModel>()
                    .setQuery(perfilReferencia, PerfilModel::class.java)
                    .build()

            var adapterPerfil = PerfilAdapter(options)
            adapterPerfil.startListening()
            recyclerView.adapter = adapterPerfil
        }

    }
}
