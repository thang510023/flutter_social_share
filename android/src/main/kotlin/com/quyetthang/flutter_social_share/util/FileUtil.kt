package com.quyetthang.flutter_social_share.util

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import androidx.loader.content.CursorLoader
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class FileUtil(applicationContext: Context, private val url: String) {
    private val authorities: String = applicationContext.packageName + ".com.shekarmudaliyar.social_share"
    private val context: Context = applicationContext
    private var type: String? = null
    private val uri: Uri = Uri.parse(url)
    private fun getMimeType(url: String): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }

    val isFile: Boolean
        get() = isBase64File || isLocalFile
    private val isBase64File: Boolean
        private get() {
            val scheme = uri.scheme
            if (scheme != null && scheme == "data") {
                type = uri.schemeSpecificPart.substring(0, uri.schemeSpecificPart.indexOf(";"))
                return true
            }
            return false
        }
    private val isLocalFile: Boolean
        private get() {
            val scheme = uri.scheme
            if (scheme != null && (scheme == "content" || scheme == "file")) {
                if (type != null) {
                    return true
                }
                type = getMimeType(uri.toString())
                if (type == null) {
                    val realPath = getRealPath(uri)
                    type = (realPath?.let { getMimeType(it) } ?: return false)
                    if (type == null) {
                        type = "*/*"
                    }
                }
                return true
            }
            return false
        }

    fun getType(): String {
        return type ?: "*/*"
    }

    private fun getRealPath(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val loader = CursorLoader(context, uri, projection, null, null, null)
        val cursor = loader.loadInBackground()
        var result: String? = null
        if (cursor != null && cursor.moveToFirst()) {
            val col_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            result = cursor.getString(col_index)
            cursor.close()
        }
        return result
    }

    fun getUri(): Uri? {
        val mime = MimeTypeMap.getSingleton()
        val extension = mime.getExtensionFromMimeType(getType())
        if (isBase64File) {
            val tempPath = context.cacheDir.path
            val prefix = "" + System.currentTimeMillis() / 1000
            val encodedFile = uri.schemeSpecificPart
                .substring(uri.schemeSpecificPart.indexOf(";base64,") + 8)
            try {
                val tempFile = File(tempPath, "$prefix.$extension")
                val stream = FileOutputStream(tempFile)
                stream.write(Base64.decode(encodedFile, Base64.DEFAULT))
                stream.flush()
                stream.close()
                return FileProvider.getUriForFile(context, authorities, tempFile)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else if (isLocalFile) {
            val uri = Uri.parse(url)
            return FileProvider.getUriForFile(context, authorities, File(uri.path))
        }
        return null
    }

}