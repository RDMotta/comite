package app.rdm.evento

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import app.rdm.evento.app.FirebaseUtils
import app.rdm.evento.controles.EventoControle
import app.rdm.evento.controles.PerfilControle
import app.rdm.evento.controles.validarLogin
import app.rdm.evento.model.EventoModel
import app.rdm.evento.model.EventosUsuarioModel
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_drawer_layout.*
import kotlinx.android.synthetic.main.app_bar_main_layout.*
import kotlinx.android.synthetic.main.nav_header_main_layout.*
import java.util.*


class MainActivity : EventoActivity(), OnInteresseEventoListener, NavigationView.OnNavigationItemSelectedListener {

    lateinit var mRecyclerViewEvents: RecyclerView
    lateinit var mRecyclerViewMyEvents: RecyclerView
    lateinit var mRecyclerViewPerfil: RecyclerView
    lateinit var mRecyclerViewMedal: RecyclerView

    lateinit var mEventosDB: FirebaseFirestore
    lateinit var mSettingsEventosDB: FirebaseFirestoreSettings
    lateinit var mTokenUser: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer_layout)

        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        mSettingsEventosDB = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()



        setTitle(R.string.title_activity_eventos)
        setConfigurationAdapter()
        setConfigurationDB()

      //  addEventos()
        callEventos()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // menuInflater.inflate(R.menu.menu_main, menu)

        if (FirebaseUtils.firebaseAuth.currentUser != null) {
            txtUserName.setText(getString(R.string.txt_hello))
            val emailUser = FirebaseUtils.firebaseAuth.currentUser!!.email
            txtUserMail.setText(emailUser.toString())


            val menuNav = nav_view.menu
            val menuAdd = menuNav.findItem(R.id.nav_addevent)
            menuAdd.isVisible = false

            if (mTokenUser.toString().equals(getString(R.string.user_master))){
                menuAdd.isVisible = true
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.im_logout -> {
                logoutapp()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_my_events -> {
                callMyEvents()
            }
            R.id.nav_all_events -> {
                callEventos()
            }
            R.id.nav_refresh -> {
                callEventos()
            }
            R.id.nav_logout -> {
               logoutapp()
            }
            R.id.nav_member -> {
                callMembers()
            }
            R.id.nav_addevent ->{
                callAddEvent()
            }
            R.id.nav_medal ->{
                callMedals()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun logoutapp(){
        FirebaseUtils.firebaseAuth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    protected fun setConfigurationAdapter() {
        mRecyclerViewEvents = findViewById(app.rdm.evento.R.id.recycler_evento)
        mRecyclerViewMyEvents = findViewById(app.rdm.evento.R.id.recycler_meus_eventos)
        mRecyclerViewPerfil = findViewById(app.rdm.evento.R.id.recycler_perfil)
        mRecyclerViewMedal = findViewById(app.rdm.evento.R.id.recycler_medal)


//        mRecyclerViewEvents.addItemDecoration(
//                DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
//        mRecyclerViewMyEvents.addItemDecoration(
//                DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
//        mRecyclerViewPerfil.addItemDecoration(
//                DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
//

        mRecyclerViewEvents.layoutManager = LinearLayoutManager(this)
        mRecyclerViewMyEvents.layoutManager = LinearLayoutManager(this)
        mRecyclerViewPerfil.layoutManager = LinearLayoutManager(this)
        mRecyclerViewMedal.layoutManager = LinearLayoutManager(this)

    }

    protected fun setConfigurationDB() {
        val reference: FirebaseUtils = FirebaseUtils.reference.instance
        mTokenUser =  reference.firebaseAuth.currentUser!!.uid

        mEventosDB = reference.firebaseFirestore
        mEventosDB.firestoreSettings = mSettingsEventosDB
    }

    private fun callMembers() {
        setTitle(R.string.txt_im_member)
        mRecyclerViewPerfil.visibility = View.VISIBLE
        mRecyclerViewMyEvents.visibility = View.GONE
        mRecyclerViewEvents.visibility = View.GONE
        mRecyclerViewMedal.visibility = View.GONE

        PerfilControle.consultarTodos(mEventosDB, this, mRecyclerViewPerfil)
    }

    private fun callEventos() {
        setTitle(R.string.txt_im_all_events)
        mRecyclerViewEvents.visibility = View.VISIBLE
        mRecyclerViewMyEvents.visibility = View.GONE
        mRecyclerViewPerfil.visibility = View.GONE
        mRecyclerViewMedal.visibility = View.GONE

        EventoControle.consultarTodos(mEventosDB, this, this, mRecyclerViewEvents)
    }

    private fun callMyEvents() {
        setTitle(R.string.txt_im_my_events)
        mRecyclerViewMyEvents.visibility = View.VISIBLE
        mRecyclerViewEvents.visibility = View.GONE
        mRecyclerViewPerfil.visibility = View.GONE
        mRecyclerViewMedal.visibility = View.GONE

        EventoControle.consultarMeusEventos(mTokenUser, mEventosDB, this, this,  mRecyclerViewMyEvents)
    }

    private fun loadEventsFromVisibility(){
        if (mRecyclerViewEvents.visibility == View.VISIBLE) {
           callEventos()
        } else {
           callMyEvents()
        }
    }

    override fun setRemoverEvento(model: EventoModel) {
        EventoControle.removerEvento(mTokenUser, mEventosDB, this, model)
        var mDelayHandler = Handler()
        mDelayHandler.postDelayed(Runnable {
            loadEventsFromVisibility()
        }, 2000)
    }

    override fun setAdicionarEvento(model: EventoModel) {
        EventoControle.addEvento(mTokenUser, mEventosDB, this, model)
    }

    fun callMedals(){
        setTitle(R.string.txt_im_medal)
        mRecyclerViewMedal.visibility = View.VISIBLE
        mRecyclerViewEvents.visibility = View.GONE
        mRecyclerViewMyEvents.visibility = View.GONE
        mRecyclerViewPerfil.visibility = View.GONE
        mRecyclerViewMedal.adapter = null
    }


    fun callAddEvent(){
        val intent = Intent(this, AddEventActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun addEventos(){

        val token: String = FirebaseUtils.firebaseAuth.currentUser!!.uid
        val lista = HashMap<String, String>()
        lista.put(token, token)

        mEventosDB.collection(getString(R.string.table_events)).document().set(
                EventoModel(true, "Publicação do dia", "Publicação do dia", Date(), "","", lista )

                , SetOptions.merge())
    }

    private fun addDados(model: EventoModel) {
        val token: String = FirebaseUtils.firebaseAuth.currentUser!!.uid
        var lista = ArrayList<EventoModel>()
        lista.add(model)
        val eventoUsuario = EventosUsuarioModel()
        eventoUsuario.usuarioToken = token
        eventoUsuario.ativo = true
        eventoUsuario.eventos = lista
        mEventosDB.collection(getString(R.string.table_user_events)).document().set(eventoUsuario, SetOptions.merge())
    }
}
