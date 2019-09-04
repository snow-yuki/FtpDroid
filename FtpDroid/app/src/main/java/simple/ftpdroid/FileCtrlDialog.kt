package simple.ftpdroid

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import kotlinx.android.synthetic.main.dialog_file_ctrl.*

class FileCtrlDialog(c : Context) : AlertDialog(c){

    var fileName = "Unknown File"
    var onDelClick : ()->Unit = {}
    var onDownloadClick : ()->Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_file_ctrl)
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)

        fileNameTextView.text = fileName
        downloadBtn.setOnClickListener { onDownloadClick() }
        delBtn.setOnClickListener { onDelClick() }
        cancelBtn.setOnClickListener { cancel() }
    }
}