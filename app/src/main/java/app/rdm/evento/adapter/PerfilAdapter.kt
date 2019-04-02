package app.rdm.evento.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import app.rdm.evento.R
import app.rdm.evento.app.FirebaseUtils
import app.rdm.evento.controles.CircleTransform
import app.rdm.evento.model.PerfilModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.frame_perfil.view.*

class PerfilAdapter(options: FirestoreRecyclerOptions<PerfilModel>): FirestoreRecyclerAdapter<PerfilModel, PerfilAdapter.PerfilAdapterAdapterHolder>(options){

    lateinit var mStorage: FirebaseStorage
    lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PerfilAdapterAdapterHolder {
        mStorage = FirebaseUtils.firebaseStorage
        mContext = parent.context

        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.frame_perfil, null, false)

        return PerfilAdapterAdapterHolder(view)
    }

    override fun onBindViewHolder(holder: PerfilAdapterAdapterHolder, position: Int, model: PerfilModel) {
        holder.bind(model)
    }

    inner class PerfilAdapterAdapterHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        protected fun loadImage(strURI: String?, thumbnail: ImageView) {
            if (strURI?.length!! > 0) {
                Picasso.get()
                        .load(strURI).transform(CircleTransform())
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.ic_menu_gallery)
                        .into(thumbnail)
            }
        }

        fun bind(model: PerfilModel) {
            with(itemView) {
                txtNome.text = model.nome
                txtEmail.text = model.email
                txtCargo.text = model.cargo
                loadImage(model.thumbnailperfil, imgThumbnail)
            }
        }
    }
}