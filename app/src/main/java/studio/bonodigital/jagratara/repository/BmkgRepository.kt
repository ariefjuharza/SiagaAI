package studio.bonodigital.jagratara.repository

import studio.bonodigital.jagratara.api.JagrataraApi
import studio.bonodigital.jagratara.util.EdukasiCondition

class BmkgRepository(
    private val api: JagrataraApi
) {

    suspend fun determineCondition(): Triple<String, String?, String?> {

        try {
            val gempa = api.getGempaTerakhir()
            if (!gempa.magnitudo.isNullOrEmpty()) {
                return Triple(
                    EdukasiCondition.GEMPA, gempa.wilayah, "BMKG"
                )
            }
        } catch (_: Exception) {
        }

        try {
            val capResponse = api.getPeringatanDini()
            val alert = capResponse.alerts?.firstOrNull()

            if (alert != null) {
                val headline = alert.headline?.lowercase() ?: ""

                val condition = when {
                    headline.contains("hujan") -> EdukasiCondition.HUJAN

                    headline.contains("panas") -> EdukasiCondition.PANAS

                    else -> EdukasiCondition.EKSTREM
                }

                return Triple(
                    condition, alert.area, "BMKG"
                )
            }
        } catch (_: Exception) {
        }

        return Triple(EdukasiCondition.UMUM, null, "BMKG")
    }
}

