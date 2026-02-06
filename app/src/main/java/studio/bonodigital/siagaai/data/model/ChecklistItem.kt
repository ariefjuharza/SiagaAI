package studio.bonodigital.siagaai.data.model

data class ChecklistItem(
    val id: Int, val title: String, val description: String, var isChecked: Boolean
)