package com.quyetthang.flutter_social_share

import android.content.Intent
import android.content.pm.ResolveInfo
import android.text.TextUtils
import android.util.Log
import androidx.annotation.NonNull
import com.quyetthang.flutter_social_share.util.FileUtil
import com.twitter.sdk.android.tweetcomposer.TweetComposer
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.net.MalformedURLException


/** FlutterSocialSharePlugin */
class FlutterSocialSharePlugin(private val registrar: Registrar) : MethodCallHandler {

    companion object {
        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val channel = MethodChannel(registrar.messenger(), "flutter_social_share")
            channel.setMethodCallHandler(FlutterSocialSharePlugin(registrar))
        }
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        val msg: String? = call.argument("msg")
        val image: String? = call.argument("image")
        when (call.method) {
            "shareTwitter" -> shareToTwitter(msg, image, result)
            "shareSMS" -> shareToSMS(msg, image, result)
            else -> result.notImplemented()
        }
    }

    private fun shareToTwitter(msg: String?, image: String?, result: Result) {
        try {
            val builder: TweetComposer.Builder =
                TweetComposer.Builder(registrar.activity()).text(msg)
            if (!TextUtils.isEmpty(image)) {
                val fileHelper = FileUtil(registrar.activity(), image!!)
                if (fileHelper.isFile) {
                    builder.image(fileHelper.getUri())
                }
            }
            builder.show()
            result.success("success")
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
    }

    private fun shareToSMS(msg: String?, image: String?, result: Result) {
        try {
            val messaging = hasPackage("com.samsung.android.messaging")
            val mms = hasPackage("com.android.mms")
            val sendIntent = Intent(Intent.ACTION_SEND)
            if (messaging) sendIntent.setClassName(
                "com.samsung.android.messaging",
                "com.samsung.android.messaging.ui.view.main.WithActivity"
            )
            if (mms) sendIntent.setClassName(
                "com.android.mms",
                "com.android.mms.ui.ComposeMessageActivity"
            )
            sendIntent.putExtra("sms_body", msg)
            sendIntent.type = "text/plain"
            image?.let { Log.e("Image", it) }
            if (!TextUtils.isEmpty(image)) {
                val fileHelper = FileUtil(registrar.activity(), image!!)
                fileHelper.let { Log.e("Image", it.isFile.toString()) }
                if (fileHelper.isFile) {
                    fileHelper.let { Log.e("Image", it.getUri().toString()) }
                    sendIntent.putExtra(Intent.EXTRA_STREAM, fileHelper.getUri())
                    sendIntent.type = "image/png"
                }
            }
            registrar.activity().startActivity(Intent.createChooser(sendIntent, "SEND"))
            result.success("success")
        } catch (e: MalformedURLException) {
            e.let { Log.e("Image", it.toString()) }
            e.printStackTrace()
        }
    }

    private fun hasPackage(type: String): Boolean {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        val resInfo: List<ResolveInfo> = registrar.activity()
            .getPackageManager()
            .queryIntentActivities(share, 0)
        if (resInfo.isNotEmpty()) {
            for (info in resInfo) {
                val packageName = info.activityInfo.packageName.toLowerCase()
                val name = info.activityInfo.name.toLowerCase()
                if (packageName.contains(type) || name.contains(type)) {
                    return true;
                }
            }
        }
        return false
    }
}
