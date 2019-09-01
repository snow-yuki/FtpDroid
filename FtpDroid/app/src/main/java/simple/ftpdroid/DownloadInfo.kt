package simple.ftpdroid

data class DownloadInfo (
        val remotePath : String,
        val remoteFileName : String,
        val localPath : String
)