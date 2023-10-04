package Activity.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import apiManager.BASE_URL_IMAGE
import apiManager.Models.CoinInfo
import com.bumptech.glide.Glide
import com.reza.digitalcoinapp.R
import com.reza.digitalcoinapp.databinding.RecyclerlistBinding

class MarketAdapter(
    private val data: ArrayList<CoinInfo.Data>,
    private val recyclerCallback: RecyclerCallback
) : RecyclerView.Adapter<MarketAdapter.MarketViewHolder>() {

    lateinit var binding: RecyclerlistBinding

    inner class MarketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bindViews(dataCoinInfo: CoinInfo.Data) {

            binding.txtCionName.text = dataCoinInfo.coinInfo.fullName
            binding.tetPrice.text = dataCoinInfo.dISPLAY.uSD.pRICE
            binding.tetMarcetCap.text = dataCoinInfo.dISPLAY.uSD.mKTCAP
            val coinBaseChang = dataCoinInfo.rAW.uSD.cHANGEPCT24HOUR

            if (coinBaseChang > 0) {
                binding.tetCoinbase.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.colorGain
                    )
                )
                binding.tetCoinbase.text =
                    dataCoinInfo.rAW.uSD.cHANGEPCT24HOUR.toString().substring(0, 4) + "%"

            } else if (coinBaseChang < 0) {
                binding.tetCoinbase.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.colorLoss
                    )
                )
                binding.tetCoinbase.text = dataCoinInfo.rAW.uSD.cHANGEPCT24HOUR.toString().substring(0, 5) + "%"

            } else  {
                binding.tetCoinbase.text = "0.00" + "%"
            }


            Glide
                .with(itemView)
                .load(BASE_URL_IMAGE + dataCoinInfo.coinInfo.imageUrl)
                .into(binding.imgItem)

            itemView.setOnClickListener {
                recyclerCallback.onCoinItemClick(dataCoinInfo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        binding = RecyclerlistBinding.inflate(inflater, parent, false)
        return MarketViewHolder(binding.root)

    }

    override fun onBindViewHolder(holder: MarketViewHolder, position: Int) {

        holder.bindViews(data[position] )
    }

    override fun getItemCount(): Int = data.size


    interface RecyclerCallback {
        fun onCoinItemClick(dataCoinInfo: CoinInfo.Data)

    }

}

