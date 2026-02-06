package studio.bonodigital.siagaai.ui.edukasi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import studio.bonodigital.siagaai.data.model.EdukasiItem
import studio.bonodigital.siagaai.databinding.ItemEdukasiBinding

class EdukasiAdapter(
    private var items: List<EdukasiItem>
) : RecyclerView.Adapter<EdukasiAdapter.EdukasiViewHolder>() {

    class EdukasiViewHolder(
        private val binding: ItemEdukasiBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: EdukasiItem) {
            binding.tvTitle.text = item.title
            binding.tvDescription.text = item.description
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): EdukasiViewHolder {
        val binding = ItemEdukasiBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return EdukasiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EdukasiViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<EdukasiItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
