package simple.ftpdroid

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import org.jetbrains.anko.find

class TransferListAdapter(var c : Context,val data : MutableList<String>) : RecyclerView.Adapter<TransferListAdapter.TransferListHolder>() {

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): TransferListHolder {
        return TransferListHolder(LayoutInflater.from(c).inflate(R.layout.transfer_item,p0,false))
    }

    override fun onBindViewHolder(viewHolder: TransferListHolder, position: Int) {
        viewHolder.itemNameTextView.text = data[position]
    }

    inner class TransferListHolder(view : View) : RecyclerView.ViewHolder(view){
        val itemNameTextView = view.find<TextView>(R.id.transferItemNameTextView)
        val ratioTextView = view.find<TextView>(R.id.ratioTextView)
        val progressBar = view.find<ProgressBar>(R.id.progressBar)
    }
}