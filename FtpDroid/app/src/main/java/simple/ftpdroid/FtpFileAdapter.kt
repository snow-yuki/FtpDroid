package simple.ftpdroid

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.jetbrains.anko.find

class FtpFileAdapter(var c : Context,val data : ArrayList<String>) : RecyclerView.Adapter<FtpFileAdapter.FtpFileHolder>() {

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): FtpFileHolder {
        return FtpFileHolder(LayoutInflater.from(c).inflate(R.layout.ftp_file_item,p0,false))
    }

    override fun onBindViewHolder(p0: FtpFileHolder, p1: Int) {
        p0.fileNameTextView.text = data[p1]
    }

    inner class FtpFileHolder(view : View) : RecyclerView.ViewHolder(view){
        val fileNameTextView = view.find<TextView>(R.id.fileNameTextView)
    }
}