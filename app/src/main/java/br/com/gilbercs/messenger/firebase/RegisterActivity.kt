package br.com.gilbercs.messenger.firebase

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.gilbercs.messenger.chat.ChatHomeActivity
import br.com.gilbercs.messenger.databinding.ActivityRegisterBinding
import br.com.gilbercs.messenger.model.ModelUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private val binding by lazy { ActivityRegisterBinding.inflate(layoutInflater) }
    lateinit var selectUrlPhoto: Uri

    companion object {
        const val TAG = "RegisterActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar!!.title = "Registrar Usuario"
        //chamar metodo registro
        binding.idBtnRegister.setOnClickListener { registerUser() }
        binding.idSelectButton.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "images"), 111)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == Activity.RESULT_OK && data != null) {
            selectUrlPhoto = data.data!!
            val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectUrlPhoto)
            binding.idSelectImages.setImageBitmap(bitmap)
            //ocultar button
            binding.idSelectButton.alpha = 0f
        }
    }

    //create user
    private fun registerUser() {
        val userName = binding.idRegisterName.text.toString()
        val userEmail = binding.idRegisterEmail.text.toString()
        val userPass = binding.idRegisterPassword.text.toString()
        val userPassConfirm = binding.idRegisterConfirmPassword.text.toString()
        //codição
        if (userName.isNotEmpty()) {
            if (userEmail.isNotEmpty()) {
                if (userPass.isNotEmpty() && userPassConfirm == userPass) {
                    //create user, firebase
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(userEmail, userPass)
                        .addOnCompleteListener { task ->
                            //if (!it.isSuccessful) return@addOnCompleteListener
                            //chamar metodo salva firebase database
                            if (task.isSuccessful){
                                //val firebaseUser: FirebaseUser = task.result!!.user!!
                               // Toast.makeText(this, "Registro realizado com sucesso",Toast.LENGTH_LONG).show()
                            upLoadImagenFirebaseStorage()
                                Log.d(TAG,"Registro realizado com sucesso!!")
                            }else{
                                Toast.makeText(this, task.exception!!.message.toString(),Toast.LENGTH_LONG).show()
                            }
                        }
                    //upLoadImagenFirebaseStorage()

                } else {
                    Toast.makeText(
                        this,
                        "Preencha o campo Senha de forma correta!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                Toast.makeText(this, "Preencha o campo e-mail!", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "Preencha o campo Nome!", Toast.LENGTH_LONG).show()
        }
    }

    //upload da image
    private fun upLoadImagenFirebaseStorage() {
        if (selectUrlPhoto == null) return
        val fileImagen = UUID.randomUUID().toString()
        val ref: StorageReference =
            FirebaseStorage.getInstance().reference.child("images/$fileImagen")
        ref.putFile(selectUrlPhoto)
            .addOnSuccessListener { p0 ->
                Log.d(TAG, "Sucesso File Location")
                ref.downloadUrl.addOnSuccessListener {
                    //Salvar dados no RealTimeDatabase
                    salvaUserFirebaseDatabase(it.toString())
                    Log.d(TAG, "Imagen de perfil gravado com sucesso: $it")
                }
            }
            .addOnFailureListener { p0 ->
                Log.d(TAG, "Falha a grava imgen de perfil na base de dados")
                Toast.makeText(this, "Erro Salvar imagem", Toast.LENGTH_LONG).show()
            }

    }

    private fun salvaUserFirebaseDatabase(perfilImgUrl: String) {
        val id = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/Users/$id")
        val user = id?.let {
            ModelUser(
                it,
                binding.idRegisterName.text.toString(),
                binding.idRegisterEmail.text.toString(),
                perfilImgUrl
            )
        }
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d(TAG, "Dados do usuario gravado com sucesso no firebaseDatabase")
                //limpar campos
                limparCampo()
                //chamar nova activity
                intentNewChat()
                //Mensagens para usuario
                Toast.makeText(this,"Dados gravados com sucesso!!",Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Log.d(TAG, "Falha ao salvar no firebase  database: ${it.message}")
                Toast.makeText(this,"Falha ao gravar dados!!",Toast.LENGTH_LONG).show()
            }
    }

    //limpar campo
    private fun limparCampo() {
        binding.idRegisterName.text!!.clear()
        binding.idRegisterEmail.text?.clear()
        binding.idRegisterPassword.text?.clear()
        binding.idRegisterConfirmPassword.text?.clear()
    }

    //redirecionar para login
    private fun intentNewChat() {
        val intent = Intent(this, ChatHomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

}