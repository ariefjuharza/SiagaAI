package studio.bonodigital.jagratara.data.model

data class ChecklistItem(
    val id: Int, val title: String, val description: String, var isChecked: Boolean
)