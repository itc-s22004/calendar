package jp.ac.it_college.std.s22004.calendar.api
//
//import io.ktor.client.call.body
//import jp.ac.it_college.std.s22004.calendar.model.holidayList
//
///**
// * PokeAPI の Games カテゴリにあるエンドポイントへのアクセスを実装
// *
// * いま時点では、世代の一覧を取る機能のみ
// */
//object Api {
//    /**
//     * /generation エンドポイントへパラメータなしだと
//     * [NamedApiResourceList] 型で取得できる。
//     */
//    public suspend fun getApi(): holidayList {
//        return ApiClient.get("/JP").body()
//    }
//}