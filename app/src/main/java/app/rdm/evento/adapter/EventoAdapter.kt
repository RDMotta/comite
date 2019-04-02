package app.rdm.evento.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.rdm.evento.model.EventoModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import kotlinx.android.synthetic.main.frame_evento.view.*
import com.google.firebase.storage.FirebaseStorage
import android.widget.ImageView
import app.rdm.evento.*
import app.rdm.evento.app.FirebaseUtils
import com.squareup.picasso.Picasso
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat.startActivity
import java.io.ByteArrayOutputStream
import app.rdm.evento.R.id.thumbnailevento




class EventoAdapter: FirestoreRecyclerAdapter<EventoModel, EventoAdapter.EventoAdapterHolder>{

    constructor (options: FirestoreRecyclerOptions<EventoModel>, interesseEventoListener: OnInteresseEventoListener) : super(options) {
        this.mEventoListener = interesseEventoListener
        mStorage = FirebaseUtils.firebaseStorage
        mToken = FirebaseUtils.firebaseAuth.currentUser!!.uid
    }

    lateinit var mStorage: FirebaseStorage
    lateinit var mContext: Context
    lateinit var mToken: String
    private var mEventoListener: OnInteresseEventoListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventoAdapterHolder {
        mContext = parent.context
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.card_evento, null, false)
        return EventoAdapterHolder(view)
    }

    override fun onBindViewHolder(holder: EventoAdapterHolder, position: Int, model: EventoModel) {
       holder.bind(model)
    }

    inner class EventoAdapterHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

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