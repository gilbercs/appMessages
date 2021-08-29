package br.com.gilbercs.messenger.adapter

import android.widget.ImageView
import android.widget.TextView
import br.com.gilbercs.messenger.R
import br.com.gilbercs.messenger.model.ModelUser
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class AdapterUser(val user: ModelUser): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        val nome = viewHolder.itemView.findViewById<TextView>(R.id.id_name_user)
        val img =viewHolder.itemView.findViewById<ImageView>(R.id.id_imageView_chat)
        Picasso.get().load(user.urlImg).into(img)
        nome.text = user.name
    }

    override fun getLayout(): Int {
        return R.layout.adapter_user_new
    }
}