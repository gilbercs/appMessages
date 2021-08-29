package br.com.gilbercs.messenger.adapter

import android.widget.ImageView
import android.widget.TextView
import br.com.gilbercs.messenger.R
import br.com.gilbercs.messenger.model.ModelUser
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.adapter_user_one.view.*
import kotlinx.android.synthetic.main.adapter_user_two.view.*

class AdapterChatOne(val text: String, val user: ModelUser): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.id_adp_conversa_one.text = text
        //Imagen do perfil
        val uri = user.urlImg
        val imgPerfil= viewHolder.itemView.id_adp_perfil_one
        Picasso.get().load(uri).into(imgPerfil)

    }

    override fun getLayout(): Int {
        return R.layout.adapter_user_one
    }
}
class AdapterChatTwo(val text: String, val user: ModelUser): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.id_adp_conversa_two.text = text
        val uriTwo = user.urlImg
        val imgPerfilTwo = viewHolder.itemView.id_adp_perfil_two
        Picasso.get().load(uriTwo).into(imgPerfilTwo)
    }

    override fun getLayout(): Int {
        return R.layout.adapter_user_two
    }

}