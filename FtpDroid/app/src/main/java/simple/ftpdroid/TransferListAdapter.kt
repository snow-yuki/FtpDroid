package simple.ftpdroid

import android.content.Context
import android.content.res.ColorStateList
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import org.jetbrains.anko.find

class TransferListAdapter(var c : Context,val data : MutableList<TransferBean>) : RecyclerView.Adapter<TransferListAdapter.TransferListHolder>() {

    val speedUnitArray = arrayOf("b/s","Kb/s","Mb/s","Gb/s","Tb/s")

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): TransferListHolder {
        return TransferListHolder(LayoutInflater.from(c).inflate(R.layout.transfer_item,p0,false))
    }

    override fun onBindViewHolder(viewHolder: TransferListHolder, position: Int) {
        val bean = data[position]
        viewHolder.itemNameTextView.text = bean.name
        if(bean.isDownload){
            viewHolder.transferTypeImage.setImageResource(R.drawable.ic_file_download)
            viewHolder.transferTypeImage.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(c,R.color.blue))
        }else{
            viewHolder.transferTypeImage.setImageResource(R.drawable.ic_file_upload)
            viewHolder.transferTypeImage.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(c,R.color.accentPink))
        }
        if(bean.hasBegan){
            viewHolder.progressBar.visibility = View.VISIBLE
            viewHolder.progressBar.isIndeterminate = false
            if(bean.isDownload){
                viewHolder.ratioTextView.text = "下载中..."
//                viewHolder.progressBar.progress = (bean.progress*100/bean.size).toInt()
//                var speed : Float = (bean.progress - bean.lastProgress).toFloat()
//                var unit = 0
//                while(speed/1024 > 1f && unit < speedUnitArray.size-1){
//                    speed /= 1024
//                    unit++
//                }
//                speed = (speed*10).toInt().toFloat()/10
//                viewHolder.ratioTextView.text = "$speed${speedUnitArray[unit]}"
            }else{
                viewHolder.ratioTextView.text = "上传中..."
            }
        }else{
            viewHolder.ratioTextView.text = "等待中"
            viewHolder.progressBar.visibility = View.GONE
        }
    }

    inner class TransferListHolder(view : View) : RecyclerView.ViewHolder(view){
        val transferTypeImage = view.find<ImageView>(R.id.transferTypeImage)
        val itemNameTextView = view.find<TextView>(R.id.transferItemNameTextView)
        val ratioTextView = view.find<TextView>(R.id.ratioTextView)
        val progressBar = view.find<ProgressBar>(R.id.progressBar)
    }
}