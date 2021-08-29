package br.com.gilbercs.messenger.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import br.com.gilbercs.messenger.adapter.AdapterChatOne
import br.com.gilbercs.messenger.adapter.AdapterChatTwo
import br.com.gilbercs.messenger.databinding.ActivityChatConversaBinding
import br.com.gilbercs.messenger.model.ModelChat
import br.com.gilbercs.messenger.model.ModelUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder

class ChatConversaActivity : AppCompatActivity() {
    //declaração de variavel global
    private val binding by lazy { ActivityChatConversaBinding.inflate(layoutInflater)}
    val adapter = GroupAdapter<ViewHolder>()
    var toUser: ModelUser? = null
    companion object{
        val  TAG = "Chat Conversa Activity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.idRecyclerviewConversa.adapter = adapter
        //val userName = intent.getStringExtra(ChatUserActivity.USER_KEY)
        toUser = intent.getParcelableExtra<ModelUser>(ChatUserActivity.USER_KEY)
        //set name no title: get name
        supportActionBar?.title = toUser?.name
        //adapter.add(AdapterConversaTwo("Conversa two"))
        //lista da conversa
        listConversa()
        //evento clique enviar
        binding.idBtnEnviar.setOnClickListener {
            Log.d(TAG,"Mensagem enviada com sucesso")
            //metodo para enviar mensagens
            sendMessenger()
            //Toast.makeText(this, "texto enviado com sucesso!!",Toast.LENGTH_LONG).show()
        }
    }
    //metodo lista conversa
    private fun listConversa(){
        val oneId = FirebaseAuth.getInstance().uid
        val twoId = toUser?.id
        val refDatabase = FirebaseDatabase.getInstance().getReference("/User-messages/$oneId/$twoId")
        refDatabase.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val mensagens = snapshot.getValue(ModelChat::class.java)
                if (mensagens!= null){
                    Log.d(TAG,mensagens.text)
                    if (mensagens.oneId == FirebaseAuth.getInstance().uid) {
                        val userOne = ChatHomeActivity.currentUser ?: return
                        adapter.add(AdapterChatOne(mensagens.text, userOne))
                    }else {
                        adapter.add(AdapterChatTwo(mensagens.text, toUser!!))
                    }
                }
                binding.idRecyclerviewConversa.scrollToPosition(adapter.itemCount -1)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun sendMessenger(){
        //Entrada de dados: Mensagens
        val messeger = binding.idMessege.text.toString()
        val userOne = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<ModelUser>(ChatUserActivity.USER_KEY)
        val userTwo = user!!.id
        if (userOne == null) return
        //Instanciar firebase database
        val refOne = FirebaseDatabase.getInstance().getReference("/User-messages/$userOne/$userTwo").push()
        val refTwo = FirebaseDatabase.getInstance().getReference("/User-messages/$userTwo/$userOne").push()
        val conversa = ModelChat(refOne.key!!,messeger,userOne, userTwo,System.currentTimeMillis()/100)
        //referencia user one
        refOne.setValue(conversa)
            .addOnSuccessListener {
                Log.d(TAG,"Mensagem enviada com sucesso: ${refOne.key}")
                binding.idMessege.text.clear()
                binding.idRecyclerviewConversa.scrollToPosition(adapter.itemCount -1)
            }
        refTwo.setValue(conversa)
        val messegerUserOne = FirebaseDatabase.getInstance().getReference("/New-messages/$userOne/$userTwo")
        messegerUserOne.setValue(conversa)
        val messegerUserTwo = FirebaseDatabase.getInstance().getReference("/New-messages/$userTwo/$userOne")
        messegerUserTwo.setValue(conversa)
    }
}
