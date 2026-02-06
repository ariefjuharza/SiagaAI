package studio.bonodigital.siagaai.data.model

sealed class ChecklistUiItem {
    data class Section(val title: String) : ChecklistUiItem()
    data class Item(val data: ChecklistItem) : ChecklistUiItem()
}