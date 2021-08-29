package br.com.gilbercs.messenger.adapter

import br.com.gilbercs.messenger.R
import br.com.gilbercs.messenger.model.ModelChat
import br.com.gilbercs.messenger.model.ModelUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.adapter_home_conversa.view.*

class AdapterHomeConversa(val modelChat: ModelChat): Item<ViewHolder>() {
    //declaração de variavel
    var modelUser: ModelUser?=null
    override fun bind(viewHolder: ViewHolder, position: Int) {
        //mensagens do usuario no bate-papo
        viewHolder.itemView.id_adapter_home_messages_recentes.text = modelChat.text
        //id do usuario
        val userPartnerId: String
        if (modelChat.oneId == FirebaseAuth.getInstance().uid){
            userPartnerId = modelChat.twoId
        }else{
            userPartnerId = modelChat.oneId
        }
        val database = FirebaseDatabase.getInstance().getReference("/Users/$userPartnerId")
        database.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                modelUser = snapshot.getValue(ModelUser::class.java)
                viewHolder.itemView.id_adapter_home_user_name.text = modelUser!!.name
                val imgPerfil = viewHolder.itemView.id_adapter_home_img_perfil
               Picasso.get().load(modelUser!!.urlImg).into(imgPerfil)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun getLayout(): Int {
        return R.layout.adapter_home_conversa
    }
}