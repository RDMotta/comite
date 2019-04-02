package app.rdm.evento

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.app.LoaderManager
import android.content.Context
import android.content.CursorLoader
import android.content.Intent
import android.content.Loader
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.support.design.widget.Snackbar
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Toast
import app.rdm.evento.app.FirebaseUtils
import app.rdm.evento.controles.AccountControle
import app.rdm.evento.controles.OperacaoLogin
import app.rdm.evento.controles.validarLogin
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.frame_eventologin.*
import java.util.ArrayList

class LoginActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor>, validarLogin {

    override fun onDadosValidos(operacao: OperacaoLogin) {
        showProgress(false)
        if (operacao == OperacaoLogin.OK) {
            callMain()
        }
    }

    override fun onDadosInvalidos(operacao: OperacaoLogin) {
        showProgress(false)
        if (operacao == OperacaoLogin.USUARIO_NAO_AUTENTICADO){
            return
        }
        if (operacao == OperacaoLogin.EMAIL_INVALIDO) {
            edtEmail.requestFocus()
            edtEmail.setError(getString(R.string.error_field_required))
            return
        }
        if (operacao == OperacaoLogin.SENHA_INVALIDA){
            edtSenha.requestFocus()
            edtSenha.setError(getString(R.string.error_field_required))
            return
        }
        if (operacao == OperacaoLogin.EMAIL_FORMATO_INVALIDO){
            edtEmail.text.clear()
            edtEmail.requestFocus()
            edtEmail.setError(getString(R.string.error_invalid_email))
            return
        }
        if (operacao == OperacaoLogin.SENHA_FORMATO_INVALIDO){
            edtSenha.text.clear()
            edtSenha.requestFocus()
            edtSenha.setError(getString(R.string.error_invalid_password))

            Toast.makeText(applicationContext, getString(R.string.error_validation_password), Toast.LENGTH_LONG).show()
            return
        }
        if (operacao == OperacaoLogin.USUARIO_INVALIDO){
            edtEmail.requestFocus()
            Toast.makeText(applicationContext, getString(R.string.error_invalid_login), Toast.LENGTH_LONG).show()
            return
        }
        if (operacao == OperacaoLogin.ERRO_CADASTRAR_USUARIO){
            Toast.makeText(applicationContext, getString(R.string.error_create_account), Toast.LENGTH_LONG).show()
            return
        }

    }

    lateinit var mUsuariosDB: FirebaseFirestore
    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mUsuariosDB = FirebaseUtils.reference.firebaseFirestore
        mAuth = FirebaseUtils.firebaseAuth
        setupLayout()
        setupControls()
    }

    fun setupLayout() {
        login_progress.visibility = ProgressBar.GONE
        btnCadastrar.visibility = View.VISIBLE
        btnLogin.visibility = View.VISIBLE

        btnLogin.setOnClickListener({ v -> efetuarLogin() })
        btnCadastrar.setOnClickListener({ v -> habilitarNovoCadastro() })

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(edtEmail, InputMethodManager.RESULT_HIDDEN)
        imm.showSoftInput(edtSenha, InputMethodManager.RESULT_HIDDEN)
    }

    fun setupControls() {
        carregarListaeAutoComplete()
    }

    fun callMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun efetuarLogin() {
        showProgress(true)
        val email = edtEmail.text.toString()
        val senha = edtSenha.text.toString()
        val accountControle = AccountControle(email, senha)
        accountControle.fazerLogin(this)
    }

    fun habilitarNovoCadastro() {
        val intent = Intent(this, CadastroUserActivity::class.java)
        startActivity(intent)
        finish()
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            login_progress.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 1 else 0).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            login_progress.visibility = if (show) View.VISIBLE else View.GONE
                        }
                    })
        } else {
            login_progress.visibility = if (show) View.VISIBLE else View.GONE
        }
    }

    private fun solicitarAcessoAosContatos(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
            Snackbar.make(edtEmail, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok,
                            { requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_READ_CONTACTS) })
        } else {
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_READ_CONTACTS)
        }
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                carregarListaeAutoComplete()
            }
        }
    }

    private fun addEmailsToAutoComplete(emailAddressCollection: List<String>) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        val adapter = ArrayAdapter(this@LoginActivity,
                android.R.layout.simple_dropdown_item_1line, emailAddressCollection)

        edtEmail.setAdapter(adapter)
    }

    private fun carregarListaeAutoComplete() {
        if (!solicitarAcessoAosContatos()) {
            return
        }
        loaderManager.initLoader(0, null, this)
    }


    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE + " = ?", arrayOf(ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE),

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC")
    }

    override fun onLoadFinished(cursorLoader: Loader<Cursor>, cursor: Cursor) {
        val emails = ArrayList<String>()
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS))
            cursor.moveToNext()
        }
        addEmailsToAutoComplete(emails)
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {}

    object ProfileQuery {
        val PROJECTION = arrayOf(
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY)
        val ADDRESS = 0
        val IS_PRIMARY = 1
    }

    companion object {
        private val REQUEST_READ_CONTACTS = 0
    }
}
