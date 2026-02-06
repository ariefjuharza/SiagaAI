package studio.bonodigital.siagaai.data.model

data class CapResponse(
    val alerts: List<CapAlert>?
)

data class CapAlert(
    val headline: String?, val description: String?, val area: String?, val severity: String?
)

