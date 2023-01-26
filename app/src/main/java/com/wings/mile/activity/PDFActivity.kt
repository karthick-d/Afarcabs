package com.wings.mile.activity

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Toast
import com.wings.mile.R
import com.wings.mile.Utils.BaseActivity
import com.wings.mile.databinding.PdfViewBinding
import java.io.File


class PDFActivity : BaseActivity() {
    var pdfView: WebView? = null
    var pdfurl:String = ""
    var downloadID: Long = 0
    private lateinit var binding: PdfViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PdfViewBinding.inflate(layoutInflater)
        setContentView(R.layout.pdf_view)
        val bundle = intent.getBundleExtra("Arguments")
         pdfurl = bundle?.getString("pdffile")!!
        pdfView = findViewById(R.id.idPDFView)
        pdfView!!.settings.javaScriptEnabled = true
        pdfView!!.getSettings().setPluginState(WebSettings.PluginState.ON);
        pdfView!!.loadUrl("https://docs.google.com/gview?embedded=true&url=$pdfurl")



       // setContentView(pdfView!!)

//        PRDownloader.download(
//            pdfurl,
//            applicationContext.getExternalFilesDir(null)!!.absolutePath,
//            "MyPDF"
//        ).build().start(object : OnDownloadListener {
//            override fun onDownloadComplete() {
//                val file1 = File(
//                    applicationContext.getExternalFilesDir(null)!!.absolutePath, "MyPDF"
//                )
//                binding.idPDFView.fromFile(file1)
//                    .password(null)
//                    .defaultPage(0)
//                    .enableSwipe(true)
//                    .swipeHorizontal(false)
//                    .enableDoubletap(true)
//                    .load()
//            }
//
//            override fun onError(error: Error) {
//                Log.e("Error:", error.toString())
//            }
//        })
    }
    fun onClickCloseBottom() {
        finish()
    }
    fun downloadFiles() {
        var f: File? = null
        var ads: File? = null
        f = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString() + "/" + "AfarCabs"
            )
        } else {
            File(Environment.getExternalStorageDirectory().toString() + "/" + "AfarCabs")
        }

        // Make sure the path directory exists.
        if (!f.exists()) {
            // Make it, if it doesn't exit
            val success = f.mkdirs()
            if (!success) {
                f = null
            }
        }
        ads = File(f,pdfurl)
        if (ads.exists()) {
            ads.delete()
        }
        downloadFile(
            this,
            pdfurl,
            ads.name,
            "application/pdf"
        )
        Toast.makeText(this,"File downloading.....", Toast.LENGTH_SHORT).show()
    }

    protected fun downloadFile(
        activity: Context?,
        url: String?,
        fileName: String,
        mimeType: String?
    ) {
        try {
            if (url != null && !url.isEmpty()) {
                val path =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path + File.separator + "Genesis" + File.separator + fileName
                // File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Genesis");
                //should be local path of downloaded video
                val fileWithinMyDir = File(path)
                fileWithinMyDir.deleteOnExit()
                Log.w("path", url)
                val uri = Uri.parse(url)
                val request = DownloadManager.Request(uri)
                request.setTitle(fileName)
                request.setDescription("Downloading...")
                request.allowScanningByMediaScanner()
                request.setMimeType(mimeType)
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                request.setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "/Afarcabs/$fileName"
                )
                val dm = activity!!.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                // dm.enqueue(request);
                downloadID = dm.enqueue(request) // enqueue puts the download request in the queue.

            }
        } catch (e: IllegalStateException) {
            Toast.makeText(
                activity,
                "Please insert an SD card to download file",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}