package simple.ftpdroid

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import org.jetbrains.anko.find

class FtpFileAdapter(var c : Context) : RecyclerView.Adapter<FtpFileAdapter.FtpFileHolder>() {

    val sizeUnitList = listOf("b","Kb","Mb","Gb","Tb")

    var onFolderClick : (folderName : String) -> Unit = {}
    var onFileClick : (fileName : String, position : Int) -> Unit = {_,_ -> Unit}
    
    override fun getItemCount() = Global.currentFileList.size

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): FtpFileHolder {
        return FtpFileHolder(LayoutInflater.from(c).inflate(R.layout.ftp_file_item,p0,false))
    }

    override fun onBindViewHolder(viewHolder: FtpFileHolder, position: Int) {
        viewHolder.fileNameTextView.text = Global.currentFileList[position].name
        if(Global.currentFileList[position].isDirectory){
            viewHolder.imageView.setImageResource(R.drawable.ic_folder)
            viewHolder.fileSizeTextView.visibility = View.GONE
        }else{
            viewHolder.fileSizeTextView.visibility = View.VISIBLE
            viewHolder.imageView.setImageResource(R.drawable.ic_description)
            var fileSize = Global.currentFileList[position].size.toFloat()
            var unit = 0
            while(fileSize/1024 > 1 && unit < sizeUnitList.size-1){
                fileSize /= 1024
                unit++
            }
            fileSize = (fileSize*10).toInt().toFloat()/10
            viewHolder.fileSizeTextView.text = "$fileSize${sizeUnitList[unit]}"
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
                if(Global.currentFileList[p].isDirectory){
                    onFolderClick(Global.currentFileList[p].name)
                }else{
                    onFileClick(Global.currentFileList[p].name,p)
                }
            }
        }
    }
}