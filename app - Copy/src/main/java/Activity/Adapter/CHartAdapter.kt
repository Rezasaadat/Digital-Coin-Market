package Activity.Adapter

import apiManager.Models.ChartData
import com.robinhood.spark.SparkAdapter

class CHartAdapter(
    private val historyCallData :List<ChartData.Data> ,
    private val baseLine : String?):SparkAdapter() {

    override fun getCount(): Int {
        return  historyCallData.size
    }

    override fun getItem(index: Int): Any {
        return historyCallData[index]
    }

    override fun getY(index: Int): Float {
        return historyCallData[index].close.toFloat()
    }

    override fun hasBaseLine(): Boolean {
        return true
    }

    override fun getBaseLine(): Float {
        return    baseLine?.toFloat()  ?:super.getBaseLine()
    }

}