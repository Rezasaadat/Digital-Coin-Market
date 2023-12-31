package apiManager


import apiManager.Models.ChartData
import apiManager.Models.CoinInfo
import apiManager.Models.NewsData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ApiManager {

    private val apiService: ApiService

    init {
        val retrofit = Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

    }

    fun getNews(apiCallback: ApiCallback<ArrayList<Pair<String, String>>>) {

        apiService.getTopNews().enqueue(object : Callback<NewsData> {

            override fun onResponse(call: Call<NewsData>, response: Response<NewsData>) {

                val data = response.body()!!
                val dataToSend: ArrayList<Pair<String, String>> = arrayListOf()
                data.data.forEach {

                    dataToSend.add(Pair(it.title, it.url))
                }

                apiCallback.onSuccess(dataToSend)
            }

            override fun onFailure(call: Call<NewsData>, t: Throwable) {

                apiCallback.onError(t.message!!)
            }
        })
    }

    fun getCoinDate(apiCallback: ApiCallback<List<CoinInfo.Data>>) {

        apiService.getTopCoins().enqueue(object : Callback<CoinInfo> {
            override fun onResponse(call: Call<CoinInfo>, response: Response<CoinInfo>) {

                val data = response.body()!!
                apiCallback.onSuccess(data.data)

            }

            override fun onFailure(call: Call<CoinInfo>, t: Throwable) {
                apiCallback.onError(t.message!!)
            }

        })
    }

    fun getChartData(
        symbol: String,
        period: String,
        apiCallback: ApiCallback<Pair<List<ChartData.Data>, ChartData.Data? > >)
    {

        var histoPeriod = ""
        var limit = 30
        var aggregate = 1

        when (period) {

            HOUR -> {
                histoPeriod = HISTO_MINUTE
                limit = 60
                aggregate = 12
            }

            HOURS24 -> {
                histoPeriod = HISTO_HOUR
                limit = 24
            }

            WEEK -> {
                histoPeriod = HISTO_HOUR
                aggregate = 6
            }

            MONTH -> {
                histoPeriod = HISTO_DAY
                limit = 30
            }

            MONTH3 -> {
                histoPeriod = HISTO_DAY
                limit = 90
            }

            YEAR -> {
                histoPeriod = HISTO_DAY
                aggregate = 13
            }

            ALL -> {
                histoPeriod = HISTO_DAY
                aggregate = 30
                limit = 2000
            }
        }

        apiService.getChartData(histoPeriod, symbol, limit, aggregate)
            .enqueue(object : Callback<ChartData> {
                override fun onResponse(call: Call<ChartData>, response: Response<ChartData>) {

                    val dataFull = response.body()!!
                    val data1 = dataFull.data
                    val data2 = dataFull.data.maxByOrNull { it.close.toFloat() }
                    val returnData = Pair(data1, data2)
                    apiCallback.onSuccess(returnData)


                }

                override fun onFailure(call: Call<ChartData>, t: Throwable) {
                    apiCallback.onError(t.message!!)


                }

            }
            )

    }

    interface ApiCallback<T> {

        fun onSuccess(data: T)

        fun onError(errorMessage: String)
    }

}
