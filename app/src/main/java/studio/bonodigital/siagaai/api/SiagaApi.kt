package studio.bonodigital.siagaai.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import studio.bonodigital.siagaai.data.model.GempaTerakhir
import studio.bonodigital.siagaai.data.model.CapResponse
import studio.bonodigital.siagaai.data.request.AskRequest
import studio.bonodigital.siagaai.data.request.AskResponse

interface SiagaApi {

    @POST("/")
    suspend fun askSiagaAI(
        @Body request: AskRequest
    ): AskResponse

    @GET("/gempa-terakhir")
    suspend fun getGempaTerakhir(): GempaTerakhir

    @GET("/peringatan-dini-cuaca")
    suspend fun getPeringatanDini(): CapResponse
}