package app.rdm.evento.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import app.rdm.evento.DetalhesEventoActivity
import app.rdm.evento.OnInteresseEventoListener
import app.rdm.evento.R
import app.rdm.evento.app.FirebaseUtils
import app.rdm.evento.model.EventoModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.frame_evento.view.*
import java.io.ByteArrayOutputStream

class EventoUsuarioAdapter: FirestoreRecyclerAdapter<EventoModel, EventoUsuarioAdapter.EventoUsuarioAdapterHolder> {

    lateinit var mContext: Context
    var mToken: String
    private var mEventoListener: OnInteresseEventoListener

    constructor (options: FirestoreRecyclerOptions<EventoModel>, interesseEventoListener: OnInteresseEventoListener) : super(options) {
        this.mEventoListener = interesseEventoListener
        mToken = FirebaseUtils.firebaseAuth.currentUser!!.uid
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventoUsuarioAdapterHolder {
        mContext = parent.context
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.card_evento, null, false)
        return EventoUsuarioAdapterHolder(view)
    }

    override fun onBindViewHolder(holder: EventoUsuarioAdapterHolder, position: Int, model: EventoModel) {
        holder.bind(model)
    }

    inner class EventoUsuarioAdapterHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        protected fun loadResourceImage(model: EventoModel) {
            val strEventoImage: String? = model.thumbnailevento.toString()
            val strGrupoImage: String? = model.thumbnailgrupo.toString()

            loadImage(strEventoImage, itemView.thumbnailevento)
            loadImage(strGrupoImage, itemView.thumbnailgrupo)
        }

        protected fun loadImage(strURI: String?, thumbnail: ImageView) {
            if (strURI?.length!! > 0) {
                Picasso.get()
                        .load(strURI)
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.ic_menu_gallery)
                        .into(thumbnail)
            }
        }

        fun bind(model: EventoModel) {

            with(itemView) {
                txtTitulo.text = model.titulo
                txtDetalhes.text = model.detalhe

                val strData = DateFormat.format("dd MMM yyyy", model.previsao).toString()
                txtDia.text = strData.split(" ").get(0).toString()
                txtMes.text = strData.split(" ").get(1).toUpperCase().toString()
                loadResourceImage(model)

                swtParticipar.isChecked = model.listaTokens?.containsKey(mToken)!!
                txtParticipantes.text = model.listaTokens?.size.toString()

                swtParticipar.setOnClickListener( { v ->
                    if (swtParticipar.isChecked){
                        mEventoListener.setAdicionarEvento(model)
                    } else {
                        mEventoListener.setRemoverEvento(model)
                    }
                })

                setOnClickListener({ v ->
                    val intentDetalhe = Intent(mContext, DetalhesEventoActivity::class.java)
                    intentDetalhe.putExtra(mContext.getString(R.string.param_evento_detalhe), model)
                    intentDetalhe.putExtra(context.getString(R.string.param_evento_detalhe_participar), swtParticipar.isChecked)

                    val drawable = thumbnailevento.drawable
                    val bitmap = (drawable as BitmapDrawable).bitmap
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    val imageInByte = stream.toByteArray()

                    intentDetalhe.putExtra("imagem_drawable", imageInByte)

                    mContext.startActivity(intentDetalhe)
                })
            }
        }
    }
}