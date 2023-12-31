package Activity

import Activity.Adapter.CHartAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import apiManager.*
import apiManager.Models.ChartData
import apiManager.Models.CoinAboutItem
import apiManager.Models.CoinInfo
import com.reza.digitalcoinapp.R
import com.reza.digitalcoinapp.databinding.ActivityCoinBinding

@Suppress("DEPRECATION")

class CoinActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCoinBinding
    private lateinit var dataThisCoin: CoinInfo.Data
    lateinit var dataThisCoinAbout: CoinAboutItem
      val  apiManager = ApiManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCoinBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val fromIntent = intent.getBundleExtra("bundle")!!

        dataThisCoin = fromIntent.getParcelable<CoinInfo.Data>("bundle1")!!

        if( fromIntent.getParcelable<CoinInfo.Data>("bundle2") != null )  {

        dataThisCoinAbout = fromIntent.getParcelable("bundle2")!!

        } else{
            dataThisCoinAbout = CoinAboutItem()
        }


        binding.layoutToolbar.toolbar.title = dataThisCoin.coinInfo.fullName

        initUi()


    }

    private fun initUi() {
        initChartUi()
        initStatisticUi()
        initAboutUi()

    }

    @SuppressLint("SetTextI18n")
    private fun initAboutUi() {

        binding.layoutAbout.txtWebsite.text = dataThisCoinAbout.coinWebsite
        binding.layoutAbout.txtGithub.text = dataThisCoinAbout.coinGithub
        binding.layoutAbout.txtReddit.text = dataThisCoinAbout.coinReddit
        binding.layoutAbout.txtTwitter.text = "@" + dataThisCoinAbout.coinTwitter
        binding.layoutAbout.txtSeedaata.text = dataThisCoinAbout.coinDesc

        binding.layoutAbout.txtWebsite.setOnClickListener {
            openWebsiteDataCoin(dataThisCoinAbout.coinWebsite!!)
        }
        binding.layoutAbout.txtGithub.setOnClickListener {
            openWebsiteDataCoin(dataThisCoinAbout.coinGithub!!)
        }
        binding.layoutAbout.txtReddit.setOnClickListener {
            openWebsiteDataCoin(dataThisCoinAbout.coinReddit!!)
        }
        binding.layoutAbout.txtTwitter.setOnClickListener {
            openWebsiteDataCoin(BASE_URL_TWITTER + dataThisCoinAbout.coinTwitter!!)
        }

    }

    private fun openWebsiteDataCoin(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)

    }

    private fun initStatisticUi() {

        binding.layoutStatistics.txtOpen.text = dataThisCoin.dISPLAY.uSD.oPEN24HOUR
        binding.layoutStatistics.txtTodeyshigh.text = dataThisCoin.dISPLAY.uSD.hIGH24HOUR
        binding.layoutStatistics.txtTodeyLow.text = dataThisCoin.dISPLAY.uSD.lOW24HOUR
        binding.layoutStatistics.txtTodeyChange.text = dataThisCoin.dISPLAY.uSD.cHANGEPCTDAY
        binding.layoutStatistics.txtAlgoritm.text = dataThisCoin.coinInfo.algorithm
        binding.layoutStatistics.txtVolume.text = dataThisCoin.dISPLAY.uSD.vOLUME24HOUR
        binding.layoutStatistics.txtMarketCap.text = dataThisCoin.dISPLAY.uSD.mKTCAP
        binding.layoutStatistics.txtSupply.text = dataThisCoin.dISPLAY.uSD.sUPPLY

    }

    @SuppressLint("SetTextI18n")
    private fun initChartUi() {

        var period : String = HOUR
        requestAndShowData(period)

        binding.layoutChart.radioGropMain.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId){
                R.id.radio_12h -> {  period = HOUR    }
                R.id.radio_1d ->  {  period = HOURS24 }
                R.id.radio_1w ->  {  period = WEEK    }
                R.id.radio_1m ->  {  period = MONTH   }
                R.id.radio_3m ->  {  period = MONTH3  }
                R.id.radio_1y ->  {  period = YEAR    }
                R.id.radio_all -> {  period = ALL     }
            }
            requestAndShowData(period)
        }

        binding.layoutChart.txtChartPrice.text = dataThisCoin.dISPLAY.uSD.pRICE
        binding.layoutChart.txtChartChang1.text = "  " + dataThisCoin.dISPLAY.uSD.cHANGE24HOUR

        if (dataThisCoin.coinInfo.fullName == "BUSD") {

            binding.layoutChart.txtChartChang2.text = "0%"
        }
        else{
            binding.layoutChart.txtChartChang2.text = "  " + dataThisCoin.rAW.uSD.cHANGEPCT24HOUR.toString().substring(0, 5) + "%"
        }

        val tagher = dataThisCoin.rAW.uSD.cHANGE24HOUR
        if ( tagher > 0) {

            binding.layoutChart.txtChartChang2.setTextColor(
                ContextCompat.getColor(
                    binding.root.context ,
                    R.color.colorGain
                ))


            binding.layoutChart.txtChartUpdown.setTextColor(
                ContextCompat.getColor(
                    binding.root.context ,
                    R.color.colorGain
                ))

            binding.layoutChart.txtChartUpdown.text = "▲"

            binding.layoutChart.sparkView.lineColor = ContextCompat.getColor(
                binding.root.context ,
                R.color.colorGain
            )

        } else  if (tagher < 0 ) {

            binding.layoutChart.txtChartChang2.setTextColor(
                ContextCompat.getColor
                    (binding.root.context ,
                    R.color.colorLoss
                ))

            binding.layoutChart.txtChartUpdown.setTextColor(
                ContextCompat.getColor(
                    binding.root.context ,
                    R.color.colorLoss
                ))
            binding.layoutChart.txtChartUpdown.text = "▼"


            binding.layoutChart.sparkView.lineColor = ContextCompat.getColor(
                binding.root.context ,
                R.color.colorLoss
            )
    }
          // show whole price
        binding.layoutChart.sparkView.setScrubListener {
            if ( it == null) {
                binding.layoutChart.txtChartPrice.text = dataThisCoin.dISPLAY.uSD.pRICE

            } else {

                binding.layoutChart.txtChartPrice.text = "$" + (it as ChartData.Data).close.toString()
            }
        }

    }

    fun requestAndShowData (period: String){
        apiManager.getChartData(
            dataThisCoin.coinInfo.name,
            period,
            object : ApiManager.ApiCallback<Pair<List<ChartData.Data>, ChartData.Data?>> {
                override fun onSuccess(data: Pair<List<ChartData.Data>, ChartData.Data?>) {

                    val chartAdapter = CHartAdapter( data.first , data.second?.open.toString())
                    binding.layoutChart.sparkView.adapter = chartAdapter

                }

                override fun onError(errorMessage: String) {
                    Toast.makeText(this@CoinActivity,"Error =>" + errorMessage, Toast.LENGTH_SHORT).show()
                }

            })
    }

}
