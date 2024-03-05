package jp.ac.it_college.std.s22004.calendar.api
//
//import io.ktor.client.HttpClient
//import io.ktor.client.engine.cio.CIO
//import io.ktor.client.engine.cio.endpoint
//import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
//import io.ktor.client.request.get
//import io.ktor.client.request.url
//import io.ktor.serialization.kotlinx.json.json
//import kotlinx.serialization.json.Json
//
///**
// * Ktor client オブジェクトを管理しつつ、PokeAPI の各エンドポイントで
// * 共通の部分を実装しているオブジェクト
// */
//object ApiClient {
//    /**
//     * 全てのベースとなる URL
//     */
////    private const val BASE_URL = "https://pokeapi.co/api/v2/"
////    private const val BASE_URL = "https://holidays-jp.github.io/api/v1/"
//    private const val BASE_URL = "https://date.nager.at/api/v2/PublicHolidays/2020/"
//    private val ktor = HttpClient(CIO) {
//        engine {
//            endpoint {
//                connectTimeout = 5000
//                requestTimeout = 5000
//                socketTimeout = 5000
//            }
//        }
//        install(ContentNegotiation) {
//            json(
//                Json {
//                    ignoreUnknownKeys = true
//                    prettyPrint = true
//                    isLenient = true
//                }
//            )
//        }
//    }
//
//    public suspend fun get(endpoint: String) =
//        ktor.get {
//            url("$BASE_URL$endpoint")
//        }
//}