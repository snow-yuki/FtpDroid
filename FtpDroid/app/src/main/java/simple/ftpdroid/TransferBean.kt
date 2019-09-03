package simple.ftpdroid

data class TransferBean (
        var localPath : String,
        var remotePath : String,
        var name : String,
        var size : Long,
        var isDownload : Boolean,
        var hasBegan : Boolean,
        var progress : Long,
        var lastProgress : Long
)