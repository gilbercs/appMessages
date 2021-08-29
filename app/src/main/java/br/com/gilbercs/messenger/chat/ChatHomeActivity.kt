package br.com.gilbercs.messenger.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import br.com.gilbercs.messenger.R
import br.com.gilbercs.messenger.adapter.AdapterHomeConversa
import br.com.gilbercs.messenger.databinding.ActivityChatHomeBinding
import br.com.gilbercs.messenger.model.ModelUser
import br.com.gilbercs.messenger.firebase.LoginActivity
import br.com.gilbercs.messenger.model.ModelChat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder

class ChatHomeActivity : AppCompatActivity() {
    //declaração de variavel global
    val adapterHome = GroupAdapter<ViewHolder>()
    val newMessagesHasMap = HashMap<String, ModelChat>()
    companion object{
        var currentUser: ModelUser? = null
    }
    private  val binding by lazy { ActivityChatHomeBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar!!.title = "Mensagens recentes"
        binding.idRecyclearviewHome.adapter = adapterHome
        //binding.idRecyclearviewHome.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
        adapterHome.setOnItemClickListener { item, view ->
            Log.d("ChaHome","Adapter clique")
            val intent = Intent(this, ChatConversaActivity::class.java)
            val user = item as AdapterHomeConversa
            intent.putExtra(ChatUserActivity.USER_KEY, user.modelUser)
            startActivity(intent)
        }
        //conversar recentes
        listMessagesNew()
        //buscar usuario atual
        fecthCurrentUser()
        //verificar user logado
        userLogado()
    }
    //Metodo para atualizar recyclear view
    private fun refreshRecyclearViewMessages(){
        adapterHome.clear()
        newMessagesHasMap.values.forEach {
            adapterHome.add(AdapterHomeConversa(it))
        }
    }
    //lista mensagens recentes
    private fun listMessagesNew(){
        val userOneId = FirebaseAuth.getInstance().uid
        val database = FirebaseDatabase.getInstance().getReference("/New-messages/$userOneId")
        database.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val modelChat = snapshot.getValue(ModelChat::class.java) ?: return
                newMessagesHasMap[snapshot.key!!] = modelChat
                refreshRecyclearViewMessages()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val modelChat = snapshot.getValue(ModelChat::class.java) ?: return
                newMessagesHasMap[snapshot.key!!] = modelChat
                refreshRecyclearViewMessages()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                Log.d("Chat Home Activity: ", "onChildRemoved -> ${snapshot.key}")
                val modelChat = snapshot.getValue(ModelChat::class.java)?:return

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    //Metodo para buscar usuario atual
    private fun fecthCurrentUser(){
        val userUid = FirebaseAuth.getInstance().uid
        val refBase = FirebaseDatabase.getInstance().getReference("/Users/$userUid")
        refBase.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(ModelUser::class.java)
                Log.d("Enviada: ","Nova mensagens pelo ${currentUser?.urlImg}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ERRO CHAT HOME ACTIVITY", "$error")
            }

        })
    }
    //metodo para verificar usuario logado
    private fun userLogado(){
        val uid = FirebaseAuth.getInstance().uid
        if (uid==null){
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.id_menu_new_chat ->{
                val intent = Intent(this, ChatUserActivity::class.java)
                startActivity(intent)
            }
            R.id.id_menu_sair ->{
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this,LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}