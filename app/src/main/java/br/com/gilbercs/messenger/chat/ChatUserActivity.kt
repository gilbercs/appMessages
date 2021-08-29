package br.com.gilbercs.messenger.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import br.com.gilbercs.messenger.adapter.AdapterUser
import br.com.gilbercs.messenger.databinding.ActivityChatUserBinding
import br.com.gilbercs.messenger.model.ModelUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder

class ChatUserActivity : AppCompatActivity() {
    private val binding by  lazy { ActivityChatUserBinding.inflate(layoutInflater) }
    companion object {
        val USER_KEY = "USER_KEY"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        users()
    }
    private fun users(){
        val ref = FirebaseDatabase.getInstance().getReference("/Users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                snapshot.children.forEach {
                    Log.d("Nova mensagem: ",it.toString())
                    val user = it.getValue(ModelUser::class.java)
                    if (user !=null){
                        adapter.add(AdapterUser(user))
                    }
                }
                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as AdapterUser
                    val intent = Intent(view.context, ChatConversaActivity::class.java)
                    intent.putExtra(USER_KEY,userItem.user)
                    startActivity(intent)
                    //finish()
                }
                binding.recyclerviewNewConversa.adapter = adapter
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}
