package simple.ftpdroid

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import org.apache.commons.net.ftp.FTPFile
import org.jetbrains.anko.find

class FtpFileAdapter(var c : Context,val data : MutableList<FTPFile>) : RecyclerView.Adapter<FtpFileAdapter.FtpFileHolder>() {

    var onFolderClick : (folderName : String) -> Unit = {}
    var onFildClick : (fileName : String) -> Unit = {}
    
    override fun getItemCount() = data.size

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): FtpFileHolder {
        return FtpFileHolder(LayoutInflater.from(c).inflate(R.layout.ftp_file_item,p0,false))
    }

    override fun onBindViewHolder(viewHolder: FtpFileHolder, position: Int) {
        viewHolder.fileNameTextView.text = data[position].name
        if(data[position].isDirectory){
            viewHolder.imageView.setImageResource(R.drawable.ic_folder)
            viewHolder.fileSizeTextView.visibility = View.GONE
        }else{
            viewHolder.fileSizeTextView.visibility = View.VISIBLE
            viewHolder.imageView.setImageResource(R.drawable.ic_description)
            viewHolder.fileSizeTextView.text = data[position].size.toString()
        }
    }

    inner class FtpFileHolder(view : View) : RecyclerView.ViewHolder(view){
        val fileItem = view.find<View>(R.id.fileItem)
        val imageView = view.find<ImageView>(R.id.imageView)
        val fileNameTextView = view.find<TextView>(R.id.fileNameTextView)
        val fileSizeTextView = view.find<TextView>(R.id.fileSizeTextView)
        init {
            fileItem.setOnClickListener {
                val p = layoutPosition
                if(data[p].isDirectory){
                    onFolderClick(data[p].name)
                }else{
                    onFildClick(data[p].name)
                }
            }
        }
    }
}