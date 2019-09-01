package simple.ftpdroid

import java.io.File

data class UploadInfo (
        val localFile : File,
        val remotePath : String
)