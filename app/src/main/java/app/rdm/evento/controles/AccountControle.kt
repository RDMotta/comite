package app.rdm.evento.controles

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.widget.Toast
import app.rdm.evento.R
import app.rdm.evento.app.FirebaseUtils
import app.rdm.evento.model.UsuarioModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Pattern

enum class OperacaoLogin{
   OK,
   EMAIL_INVALIDO,
   SENHA_INVALIDA,
   REPETICAO_SENHA_DIFERENTE,
   USUARIO_NAO_AUTENTICADO,
   SENHA_FORMATO_INVALIDO,
   EMAIL_FORMATO_INVALIDO,
   USUARIO_INVALIDO,
   ERRO_CADASTRAR_USUARIO

}

interface validarLogin{
    fun onDadosValidos(operacao: OperacaoLogin)
    fun onDadosInvalidos(operacao: OperacaoLogin)
}

class AccountControle(val email: String, val senha: String){
    init {

    }
    fun isEmailValid(): Boolean {
        return Pattern.compile(
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(email).matches()
    }

    fun isPasswordValid(): Boolean {
        return Pattern.compile("^(?=.*[0-9].*[0-9])(?=.*[a-z].*[A-Z]).{8,}\$"
        ).matcher(senha).matches()
    }

    fun testarUsuarioAutenticado(): Boolean {
        var mAuth: FirebaseAuth = FirebaseUtils.firebaseAuth
        return mAuth.currentUser != null
    }


    fun isValidFields(confirmaSenha: String): OperacaoLogin{
        if (TextUtils.isEmpty(email)) {
            return OperacaoLogin.EMAIL_INVALIDO
        } else if (TextUtils.isEmpty(senha)) {
            return OperacaoLogin.SENHA_INVALIDA
        } else if (!isEmailValid()) {
            return OperacaoLogin.EMAIL_FORMATO_INVALIDO
        } else if (!isPasswordValid()){
            return OperacaoLogin.SENHA_FORMATO_INVALIDO
        } else if (!confirmaSenha.isEmpty() && !confirmaSenha.contentEquals(senha)) {
            return OperacaoLogin.REPETICAO_SENHA_DIFERENTE
        } else {
           return OperacaoLogin.OK
        }
    }

    fun fazerLogin(validarLogin: validarLogin){

        if (testarUsuarioAutenticado()){
            validarLogin.onDadosInvalidos(OperacaoLogin.OK)
        } else{

            val validacao = isValidFields("")
            if (validacao != OperacaoLogin.OK){
                validarLogin.onDadosInvalidos(validacao)
                return
            }

            var mAuth: FirebaseAuth = FirebaseUtils.firebaseAuth
            mAuth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(
                    OnCompleteListener {
                        if (it.isSuccessful) {
                            validarLogin.onDadosValidos(OperacaoLogin.OK)
                        }
                    }
            ).addOnFailureListener(
                    OnFailureListener {
                        validarLogin.onDadosInvalidos(OperacaoLogin.USUARIO_INVALIDO)
                    }
            )
        }
    }

    fun cadastrarUsuario(confirmaSenha: String, activity: Activity, validarLogin: validarLogin){
        val validacao = isValidFields(confirmaSenha)
        if (validacao != OperacaoLogin.OK){
            validarLogin.onDadosInvalidos(validacao)
            return
        }
        var mAuth: FirebaseAuth = FirebaseUtils.firebaseAuth
        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(activity, OnCompleteListener<AuthResult> { task ->

                    if (task.isSuccessful) {
                        var mEmail: String = task.result.user.email!!
                        var mTtoken: String = task.result.user.uid

                        val mUsuariosDB = FirebaseUtils.reference.firebaseFirestore
                        var usuario = UsuarioModel(mEmail, mTtoken)
                        mUsuariosDB.collection(activity.getString(R.string.table_user)).document(mTtoken).set(usuario)
                        validarLogin.onDadosValidos(OperacaoLogin.OK)
                    }
                }).addOnFailureListener(
                        OnFailureListener { exception ->
                            validarLogin.onDadosInvalidos(OperacaoLogin.ERRO_CADASTRAR_USUARIO)
                        }
                )
    }

}