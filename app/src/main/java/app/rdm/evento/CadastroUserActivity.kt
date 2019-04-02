package app.rdm.evento

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import app.rdm.evento.R
import app.rdm.evento.controles.AccountControle
import app.rdm.evento.controles.OperacaoLogin
import app.rdm.evento.controles.validarLogin

import kotlinx.android.synthetic.main.frame_cadastrouser.*

class CadastroUserActivity : AppCompatActivity() , validarLogin{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro_user)
        btnCadastrarCadastro.setOnClickListener({ novoCadastro() })

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(edtEmailCadastro, InputMethodManager.RESULT_HIDDEN)
        imm.showSoftInput(edtSenhaCadastro, InputMethodManager.RESULT_HIDDEN)
        imm.showSoftInput(edtRepetirSenhaCadastro, InputMethodManager.RESULT_HIDDEN)
    }

    override fun onBackPressed() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
        super.onBackPressed()
    }

    fun novoCadastro(){
        showProgress(true)
        val email = edtEmailCadastro.text.toString()
        val senha = edtSenhaCadastro.text.toString()
        val repetirSenha = edtRepetirSenhaCadastro.text.toString()
        val accountControle = AccountControle(email, senha)
        accountControle.cadastrarUsuario(repetirSenha, this, this)
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            login_progressCadastro.visibility = if (show) View.VISIBLE else View.GONE
            login_progressCadastro.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 1 else 0).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            login_progressCadastro.visibility = if (show) View.VISIBLE else View.GONE
                        }
                    })
        } else {
            login_progressCadastro.visibility = if (show) View.VISIBLE else View.GONE
        }
    }

    override fun onDadosValidos(operacao: OperacaoLogin) {
        showProgress(false)
        if (operacao == OperacaoLogin.OK) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onDadosInvalidos(operacao: OperacaoLogin) {
        showProgress(false)
        if (operacao == OperacaoLogin.USUARIO_NAO_AUTENTICADO){
            return
        }
        if (operacao == OperacaoLogin.EMAIL_INVALIDO) {
            edtEmailCadastro.requestFocus()
            edtEmailCadastro.setError(getString(R.string.error_field_required))
            return
        }
        if (operacao == OperacaoLogin.SENHA_INVALIDA){
            edtSenhaCadastro.requestFocus()
            edtSenhaCadastro.setError(getString(R.string.error_field_required))
            return
        }
        if (operacao == OperacaoLogin.EMAIL_FORMATO_INVALIDO){
            edtEmailCadastro.text.clear()
            edtEmailCadastro.requestFocus()
            edtEmailCadastro.setError(getString(R.string.error_invalid_email))
            return
        }
        if (operacao == OperacaoLogin.SENHA_FORMATO_INVALIDO){
            edtSenhaCadastro.text.clear()
            edtSenhaCadastro.requestFocus()
            edtSenhaCadastro.setError(getString(R.string.error_invalid_password))

            Toast.makeText(applicationContext, getString(R.string.error_validation_password), Toast.LENGTH_LONG).show()
            return
        }
        if (operacao == OperacaoLogin.REPETICAO_SENHA_DIFERENTE){
            edtRepetirSenhaCadastro.text.clear()
            edtRepetirSenhaCadastro.requestFocus()
            edtRepetirSenhaCadastro.setError(getString(R.string.error_invalid_confirm_password))
            return
        }
        if (operacao == OperacaoLogin.USUARIO_INVALIDO){
            edtEmailCadastro.requestFocus()
            Toast.makeText(applicationContext, getString(R.string.error_invalid_login), Toast.LENGTH_LONG).show()
            return
        }
        if (operacao == OperacaoLogin.ERRO_CADASTRAR_USUARIO){
            Toast.makeText(applicationContext, getString(R.string.error_create_account), Toast.LENGTH_LONG).show()
            return
        }

    }
}
