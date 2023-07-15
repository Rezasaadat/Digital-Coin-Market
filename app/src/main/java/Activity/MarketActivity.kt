package Activity

import Activity.Adapter.MarketAdapter
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import apiManager.ApiManager
import apiManager.Models.CoinAboutDeta
import apiManager.Models.CoinAboutItem
import apiManager.Models.CoinInfo
import com.google.gson.Gson
import com.reza.digitalcoinapp.databinding.ActivityMarketBinding

class MarketActivity : AppCompatActivity(), MarketAdapter.RecyclerCallback {

    lateinit var binding: ActivityMarketBinding
    var apiManager = ApiManager()
    lateinit var dataNews: ArrayList<Pair<String, String>>
    lateinit var aboutDataMap: MutableMap<String , CoinAboutItem>
    lateinit var adapter: MarketAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMarketBinding.inflate(layoutInflater)
        setContentView(binding.root)
         binding.layoutToolbar.toolbar.title = "Digital Coin Market"


        binding.layoutwhatchList.btnshowmore.setOnClickListener {
            val  intent = Intent(Intent.ACTION_VIEW , Uri.parse("https://www.livecoinwatch.com/"))
            startActivity(intent)
        }
        binding.swipeRefreshMain.setOnRefreshListener {
            initUi()

            Handler(Looper.getMainLooper()).postDelayed({

              binding.swipeRefreshMain.isRefreshing = false

            } , 1700)

        }

        getAboutDataFromAssets()

    }
    override fun onResume() {
        super.onResume()

        initUi()
    }

    private fun initUi() {

        getNewsFromApi()
        getTopCoinFromApi()



    }

    private fun getNewsFromApi() {
        apiManager.getNews(object : ApiManager.ApiCallback<ArrayList<Pair<String, String>>> {
            override fun onSuccess(data: ArrayList<Pair<String, String>>) {
                dataNews = data
                refreshNews()
            }

            override fun onError(errorMessage: String) {
                Toast.makeText(this@MarketActivity, "error" + errorMessage, Toast.LENGTH_SHORT)
                    .show()
            }
        }
        )
    }
    private fun refreshNews() {

        val randomAccess = (0..49).random()
        binding.layoutNews.textNews.text = dataNews[randomAccess].first
        binding.layoutNews.imgItem.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(dataNews[randomAccess].second))
            startActivity(intent)
        }
        binding.layoutNews.textNews.setOnClickListener {
            refreshNews()
        }

    }

    private fun getTopCoinFromApi() {

        apiManager.getCoinDate(object : ApiManager.ApiCallback<List<CoinInfo.Data>> {
            override fun onSuccess(data: List<CoinInfo.Data>) {

                showDataOnRecycler(data)

            }

            override fun onError(errorMessage: String) {

                Toast.makeText(this@MarketActivity, "error" + errorMessage, Toast.LENGTH_LONG)
                    .show()

            }


         })

    }
    private fun showDataOnRecycler(data: List<CoinInfo.Data>) {

        adapter = MarketAdapter(ArrayList(data), this)
        binding.layoutwhatchList.recicelermain.adapter = adapter
        binding.layoutwhatchList.recicelermain.layoutManager = LinearLayoutManager(this)

    }

    override fun onCoinItemClick(dataCoinInfo: CoinInfo.Data) {
        val intent = Intent(this, CoinActivity::class.java)

        val bundle = Bundle()
        bundle.putParcelable("bundle1" , dataCoinInfo   )
        bundle.putParcelable("bundle2" , aboutDataMap[dataCoinInfo.coinInfo.name])

        intent.putExtra("bundle", bundle)
        startActivity(intent)
    }

    private fun getAboutDataFromAssets() {

        val fileInString = applicationContext.assets
            .open("currencyinfo.json")
            .bufferedReader()
            .use { it.readText() }

        aboutDataMap = mutableMapOf<String , CoinAboutItem>()
        val gson = Gson()
        val dataAboutAll = gson.fromJson(fileInString, CoinAboutDeta::class.java)
        dataAboutAll.forEach {
            aboutDataMap[it.currencyName] = CoinAboutItem(
                it.info.web,
                it.info.github,
                it.info.twt,
                it.info.desc,
                it.info.reddit
            )
        }

    }
}