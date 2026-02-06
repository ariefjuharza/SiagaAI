package studio.bonodigital.jagratara.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import studio.bonodigital.jagratara.data.model.GempaTerakhir
import studio.bonodigital.jagratara.data.model.CapResponse
import studio.bonodigital.jagratara.data.request.AskRequest
import studio.bonodigital.jagratara.data.request.AskResponse

interface JagrataraApi {

    @POST("/")
    suspend fun askJagratara(
        @Body request: AskRequest
    ): AskResponse

    @GET("/gempa-terakhir")
    suspend fun getGempaTerakhir(): GempaTerakhir

    @GET("/peringatan-dini-cuaca")
    suspend fun getPeringatanDini(): CapResponse
}