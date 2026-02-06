package studio.bonodigital.jagratara.util

object AiFormatter {

    fun format(raw: String): String {
        var text = raw

        text = text.replace("**", "")

        text = text.replace(Regex("[\\p{So}\\p{Cn}]"), "")

        text = text.replace(Regex("(?m)^-\\s*"), "• ")

        text = text.replace(Regex("\n{3,}"), "\n\n")

        return text.trim()
    }
}
