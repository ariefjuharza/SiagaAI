package studio.bonodigital.siagaai.ui.tanyaai

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import studio.bonodigital.siagaai.R
import studio.bonodigital.siagaai.data.model.ChatMessage

class ChatAdapter(
    private val items: MutableList<ChatMessage>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_USER = 1
        const val TYPE_AI = 2
    }

    override fun getItemViewType(position: Int): Int = if (items[position].isUser) TYPE_USER else TYPE_AI

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_USER) {
            val view = inflater.inflate(R.layout.item_chat_user, parent, false)
            UserViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_chat_ai, parent, false)
            AiViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = items[position]

        if (message.text == "__INTRO_ADDED__") {
            holder.itemView.visibility = View.GONE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
            return
        }

        if (holder is UserViewHolder) holder.bind(message)
        if (holder is AiViewHolder) holder.bind(message)

    }

    override fun getItemCount() = items.size

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(msg: ChatMessage) {
            itemView.findViewById<TextView>(R.id.tvMessage).text = msg.text
        }
    }

    class AiViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(msg: ChatMessage) {
            itemView.findViewById<TextView>(R.id.tvMessage).text = msg.text
        }
    }


}