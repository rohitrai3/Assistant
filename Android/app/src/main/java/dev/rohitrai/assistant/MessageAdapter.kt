package dev.rohitrai.assistant

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.runtime.mutableIntStateOf
import androidx.recyclerview.widget.RecyclerView

data class Message(
    val id: String,
    val content: String,
    val isUser: Boolean
)

class MessageAdapter(
    private val messages: List<Message>
) {

    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_ASSISTANT = 2
    }

    fun getItemViewType(position: Int) { }

    fun onCreateViewHolder(parent: ViewGroup, viewType: Int) { }

    fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) { }

    fun getItemCount(): Int = messages.size

    class UserMessageViewHolder(view: View) : RecyclerView.ViewHolder(view)
    class AssistantMessageViewHolder(view: View) : RecyclerView.ViewHolder(view)
}