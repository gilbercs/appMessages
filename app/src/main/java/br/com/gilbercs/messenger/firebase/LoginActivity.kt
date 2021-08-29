package br.com.gilbercs.messenger.firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import br.com.gilbercs.messenger.R
import br.com.gilbercs.messenger.chat.ChatHomeActivity
import br.com.gilbercs.messenger.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater)}
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar!!.title = "Entrar Chat"
        //inicializar firebase
        auth = Firebase.auth
        //chamar metodo logar
        binding.idBtnEnter.setOnClickListener { validarCampos() }
        //chamar metodo register
        binding.idRegister.setOnClickListener { registerUser() }
    }
    //metodo p/ login
    private fun validarCampos(){
        val email = binding.idCampoEmail.text.toString()
        val pass = binding.idCampoPass.text.toString()
        //condicional
        if (!email.isEmpty()){
            if (!pass.isEmpty()){
                //Firebase auth
                userAuth(email, pass)
            }else{
                Toast.makeText(this,"Por favor preencha o campo senha!!",Toast.LENGTH_LONG).show()
            }
        }else{
            Toast.makeText(this,"Por favor preencha o campo email!!",Toast.LENGTH_LONG).show()
        }

    }
    //metodo Registro de usuario
    private fun registerUser(){
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
    //Login no App
    private fun userAuth(email: String, pass: String){
        auth.signInWithEmailAndPassword(email,pass)
            .addOnSuccessListener {
                Toast.makeText(this,resources.getString(R.string.toast_login_registro_sucesso),Toast.LENGTH_LONG).show()
                val intent = Intent(this, ChatHomeActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Falha ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}