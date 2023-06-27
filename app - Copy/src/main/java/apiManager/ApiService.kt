package apiManager


import apiManager.Models.ChartData
import apiManager.Models.CoinInfo
import apiManager.Models.NewsData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @Headers(API_KEY)
    @GET("v2/news/")
    fun getTopNews(
        @Query ("sortOder") sortOrder : String = "popular"
    ): Call<NewsData>


    @Headers(API_KEY)
    @GET("top/totalvolfull")
    fun getTopCoins(
        @Query ("tsym") to_symbol :String = "USD" ,
        @Query ("limit") limit_data :Int = 10
    ):Call<CoinInfo>


    @Headers(API_KEY)
    @GET("{period}")
    fun getChartData(
        @Path("period") peroid : String ,
        @Query("fsym")  fromsymbol: String,
        @Query("limit") limit: Int,
        @Query("aggregate") aggregate:Int,
        @Query("tsym") tosymbol: String = "USD"
    ):Call<ChartData>


}