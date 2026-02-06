package studio.bonodigital.siagaai.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import studio.bonodigital.siagaai.data.model.ChecklistUiItem
import studio.bonodigital.siagaai.R
import studio.bonodigital.siagaai.data.model.ChecklistItem

class ChecklistAdapter(
    private val items: List<ChecklistUiItem>, private val onCheckedChange: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_SECTION = 0
        private const val TYPE_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is ChecklistUiItem.Section -> TYPE_SECTION
        is ChecklistUiItem.Item -> TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_SECTION -> {
                val view = inflater.inflate(R.layout.item_section_header, parent, false)
                SectionViewHolder(view)
            }

            else -> {
                val view = inflater.inflate(R.layout.item_checklist, parent, false)
                ItemViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is ChecklistUiItem.Section -> (holder as SectionViewHolder).bind(item)
            is ChecklistUiItem.Item -> (holder as ItemViewHolder).bind(item.data)
        }
    }

    override fun getItemCount() = items.size

    class SectionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(section: ChecklistUiItem.Section) {
            itemView.findViewById<TextView>(R.id.tvSectionTitle).text = section.title
        }
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: ChecklistItem) {
            val checkBox = itemView.findViewById<CheckBox>(R.id.cbItem)
            val title = itemView.findViewById<TextView>(R.id.tvTitle)
            val description = itemView.findViewById<TextView>(R.id.tvDescription)


            title.text = item.title
            description.text = item.description
            checkBox.isChecked = item.isChecked

            checkBox.setOnCheckedChangeListener { _, checked ->
                item.isChecked = checked
                onCheckedChange()
            }
        }
    }
}