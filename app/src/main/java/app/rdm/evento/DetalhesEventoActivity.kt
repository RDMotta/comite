package app.rdm.evento

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import app.rdm.evento.app.FirebaseUtils
import app.rdm.evento.controles.EventoControle
import app.rdm.evento.model.EventoModel
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detalhes_evento.*
import kotlinx.android.synthetic.main.content_detalhes_evento.*
import java.text.SimpleDateFormat

class DetalhesEventoActivity : AppCompatActivity() , OnInteresseEventoListener{

    lateinit var mEvento: EventoModel
    lateinit var mStorage: FirebaseStorage
    lateinit var storageReference: StorageReference
    lateinit var mTokenUser: String
    var participando: Boolean = false
    var totalParticipantes: Long = 0
    var mTempParticipantes: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_evento)
        setSupportActionBar(toolbar)
        if (FirebaseUtils.firebaseAuth.currentUser == null){
            finish()
        }
        mStorage = FirebaseUtils.instance.firebaseStorage
        mTokenUser = FirebaseUtils.firebaseAuth.currentUser!!.uid!!

        setupDataState(savedInstanceState)
        setupDetalhes(mEvento)
    }

    protected fun removerParticipacaoEvento(){
        setRemoverEvento(mEvento)
        participando =  false
        totalParticipantes = totalParticipantes - 1
        tratamentoParticipantes()
    }

    protected fun adicionarParticipacaoEvento(){
        setAdicionarEvento(mEvento)
        participando =  true
        totalParticipantes = totalParticipantes + 1
        tratamentoParticipantes()
    }

    protected fun tratamentoParticipantes() {
        if (participando) {
            fabInteresseEvento.setImageResource(R.drawable.ic_outline_delete_sweep_24px)
            fabInteresseEvento.setOnClickListener({ v -> removerParticipacaoEvento() })
        } else {
            fabInteresseEvento.setImageResource(R.drawable.ic_outline_person_add_24px)
            fabInteresseEvento.setOnClickListener({ v -> adicionarParticipacaoEvento() })
        }
        txtParticipantes.text = totalParticipantes.toString() + " " + getString(R.string.txt_participantes)
    }

    protected fun setupDetalhes(evento: EventoModel) {
        setTitle(evento.titulo.toString())
        txtDetalhes.text = evento.detalhe.toString()
        val fmt = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        txtPeriodo.text = fmt.format(mEvento.previsao)
        totalParticipantes = evento.listaTokens!!.size.toLong()
        tratamentoParticipantes()


        val eventImage = evento.thumbnailevento.toString()
        if (eventImage.length > 0) {
            storageReference = mStorage.reference.child("Eventos").child(eventImage)
            Picasso.get()
                    .load(eventImage)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)

                    .into(imgToolBar)
        }
    }

    protected fun setupDataState(savedInstanceState: Bundle?){
        if (savedInstanceState?.getParcelable<EventoModel>("mevento") != null) {
            mEvento = savedInstanceState?.getParcelable<EventoModel>("mevento")
            participando = savedInstanceState?.getBoolean("mparticipando", false)
            mTempParticipantes = savedInstanceState?.getLong("mTempParticipantes", totalParticipantes)
        } else {
            mEvento = intent.getParcelableExtra(getString(R.string.param_evento_detalhe))
            participando = intent.getBooleanExtra(getString(R.string.param_evento_detalhe_participar), false)
            mTempParticipantes = intent.getLongExtra("mTempParticipantes", totalParticipantes)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if (mStorage != null) {
            outState?.putString("reference", mStorage.toString())
        }
        if (mEvento != null){
            outState?.putParcelable("mevento", mEvento)
            outState?.putBoolean("mparticipando", participando)
        }
    }

    override fun setAdicionarEvento(model: EventoModel) {
        EventoControle.addEvento(mTokenUser, FirebaseUtils.reference.firebaseFirestore, this, model)
    }
    override fun setRemoverEvento(model: EventoModel) {
        EventoControle.removerEvento(mTokenUser, FirebaseUtils.reference.firebaseFirestore, this, model)
    }
}
