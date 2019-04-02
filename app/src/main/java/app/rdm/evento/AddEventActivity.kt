package app.rdm.evento

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import kotlinx.android.synthetic.main.activity_add_event.*

class AddEventActivity : AppCompatActivity() {

    lateinit var mTitulo: Editable
    lateinit var mDescricao: Editable
    lateinit var mDataStr: Editable

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

    }

    fun setupDataState(savedInstanceState: Bundle?){
        if (savedInstanceState != null) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)
        setupDataState(savedInstanceState)
        setupFabButton()

    }

    fun setupFabButton(){
        showOptions(false)
        fabMais.tag = false
        fabMais.setOnClickListener({
            if (fabMais.tag == false){
                fabMais.tag = true
                showOptions(true)
            } else {
                fabMais.tag = false
                showOptions(false)
            }
            }
        )

        fabSalvar.setOnClickListener({
            showOptions(false)
            salvarEvento()
        })
        fabGaleria.setOnClickListener({
            showOptions(false)
            carregarImagem()
        })
        fabCamera.setOnClickListener({
            showOptions(false)
            openCamera()
        })
    }

    fun showOptions(show: Boolean){
        var idVisible = View.GONE
        if (show) {
            idVisible = View.VISIBLE
        }
        fabCamera.visibility = idVisible
        fabGaleria.visibility = idVisible
        fabSalvar.visibility = idVisible
        fabMais.visibility =  View.VISIBLE
    }

    fun salvarEvento(){}
    fun carregarImagem(){}
    fun openCamera(){}
}
