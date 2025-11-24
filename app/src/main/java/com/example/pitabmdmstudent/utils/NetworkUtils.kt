//package com.example.pitabmdmstudent.utils
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.net.*
//import android.os.Build
//import android.provider.Settings
//import android.telephony.TelephonyManager
//import android.text.TextUtils
//import android.webkit.WebSettings
//import androidx.lifecycle.asLiveData
//import com.example.pitabmdmstudent.BuildConfig
//import com.example.pitabmdmstudent.extension.EMPTY
//import com.google.gson.JsonObject
//import kotlinx.coroutines.channels.awaitClose
//import kotlinx.coroutines.flow.callbackFlow
//import kotlinx.coroutines.flow.catch
//import okhttp3.Request
//
//class NetworkUtils private constructor(private val context: Context) {
//
//
//    private fun getUserAgent(): String {
//        if (BuildConfig.DEBUG) {
//            return "Android"
//        }
//
//        return try {
//            WebSettings.getDefaultUserAgent(context)
//        } catch (ex: Exception) {
//            "Android";
//        }
//    }
//
//    @get:SuppressLint("HardwareIds")
//    val randomId: String?
//        get() = if (context == null) null else Settings.Secure.getString(
//            context.contentResolver,
//            Settings.Secure.ANDROID_ID,
//        )
//
//    @get:SuppressLint("HardwareIds")
//    val randomIdN: String
//        get() = randomId ?: String.EMPTY
//
//    fun getAppVersion(): String {
//        if (!TextUtils.isEmpty(currentVersion)) {
//            return currentVersion
//        }
//        try {
//            val pInfo = context!!.packageManager.getPackageInfo(
//                context.packageName, 0,
//            )
//            currentVersion = pInfo.versionName
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        return currentVersion
//    }
//
//    private fun getAppVersionCode(): String {
//        var getVersionCode = ""
//        try {
//            val pInfo = context!!.packageManager.getPackageInfo(
//                context.packageName, 0,
//            )
//            getVersionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                pInfo.longVersionCode.toString()
//            } else pInfo.versionCode.toString()
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//        }
//        return getVersionCode
//    }
//
//    private fun getDeviceModel(): String {
//        val model = Build.MODEL
//        return capitalize(model)
//    }
//
//    fun getDeviceMake(): String {
//        val manufacturer = Build.MANUFACTURER
//        return capitalize(manufacturer)
//    }
//
//    fun getDeviceOs(): String {
//        return Build.VERSION.RELEASE
//    }
//
//    private fun capitalize(s: String?): String {
//        if (s == null || s.isEmpty()) {
//            return ""
//        }
//        val first = s[0]
//        return if (Character.isUpperCase(first)) {
//            s
//        } else {
//            Character.toUpperCase(first).toString() + s.substring(1)
//        }
//    }
//
//    private fun getMiscData(): String {
//        val jsonObject = JsonObject()
//        jsonObject.addProperty(NetworkHeaders.APP_VERSION, getAppVersionCode())
//        jsonObject.addProperty(NetworkHeaders.APP_VERSION_NAME, getAppVersion())
//        jsonObject.addProperty(NetworkHeaders.DEVICE_MAKE, getDeviceMake())
//        jsonObject.addProperty(NetworkHeaders.DEVICE_MODEL, getDeviceModel())
//        jsonObject.addProperty(NetworkHeaders.OS_VERSION, getDeviceOs()) //getDeviceOs());
//        jsonObject.addProperty(NetworkHeaders.PACKAGE_NAME, getPackageName())
//        return jsonObject.toString()
//    }
//
//    private fun getPackageName(): String {
//        return context!!.applicationContext.packageName
//    }
//
//    fun getRequest(
//        original: Request,
//        organisationId: String,
//        userId: String,
//        authToken: () -> String,
//    ): Request {
//        val token = authToken()
////        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3MDAwNDYwODUuNDA0LCJkYXRhIjp7Il9pZCI6IjY1MjY1MzU2Yzk0YzVhM2FjN2Y4MGQ4MyIsInVzZXJuYW1lIjoiNzY2ODExNjQ0NCIsImZpcnN0TmFtZSI6IiIsImxhc3ROYW1lIjoiIiwib3JnYW5pemF0aW9uIjp7Il9pZCI6IjVlYjM5M2VlOTVmYWI3NDY4YTc5ZDE4OSIsIndlYnNpdGUiOiJwaHlzaWNzd2FsbGFoLmNvbSIsIm5hbWUiOiJQaHlzaWNzd2FsbGFoIn0sInR5cGUiOiJVU0VSIn0sImlhdCI6MTY5OTQ0MTI4NX0.gNl4ZwLlGuRM5xo7U_dy1JRpLr9CGDEq3U9vp-irL3M"
//        return original.newBuilder()
//            .removeHeader(NetworkHeaders.CONTENT_TYPE)
//            .header(NetworkHeaders.CONTENT_TYPE, "application/json")
//            .header(
//                "Authorization",
//                "Bearer $token",
//            )
////            .header("xx-kong-orgid", organisationId)
////            .header("xx-kong-userid", userId)
//            .header("x-device-id", "-")
////            .header("x-time-zone", "122345")
//            .method(original.method, original.body)
//            .build()
//    }
//
//    // TOKEN, APP_VERSION , client_type, random_id, from_app
//    fun getPreparedWebViewUrl(url: String, authToken: () -> String, redirectUrl: String): String {
//        val token = authToken()
//        return if (redirectUrl.isNotEmpty())
//            "$url?TOKEN=${token}&redirect_url=${redirectUrl}&APP_VERSION=${getAppVersionCode()}&client_type=${NetworkHeaders.DEVICE_TYPE_VALUE}&random_id=${randomId}&from_app=true"
//        else
//            "$url?TOKEN=${token}&APP_VERSION=${getAppVersionCode()}&client_type=${NetworkHeaders.DEVICE_TYPE_VALUE}&random_id=${randomId}&from_app=true"
//    }
//
//    fun getAdmitCardPreparedWebViewUrl(
//        url: String,
//        authToken: () -> String,
//        widget: String,
//        form_id: String,
//        trigger: String,
//    ): String {
//        val token = authToken()
//        return "$url?token=${token}&APP_VERSION=${getAppVersionCode()}&widget=${widget}&trigger=${trigger}&form_id=${form_id}&client_type=${NetworkHeaders.DEVICE_TYPE_VALUE}&random_id=${randomId}&from_app=true"
//    }
//
//    fun getBatchFormPreparedWebViewUrl(
//        url: String,
//        slug: String,
//        batchPrice: Float,
//        authToken: () -> String,
//        batchId: String,
//        cohortExam: String,
//        cohortClass: String,
//    ): String {
//        val token = authToken()
//        return "$url?token=${token}&APP_VERSION=${getAppVersionCode()}&client_type=${NetworkHeaders.DEVICE_TYPE_VALUE}&random_id=${randomId}" +
//            "&fromApp=true&batchSlug=$slug&showBatchForm=true&totalAmount=$batchPrice&widget=BATCH_FORM&batchId=$batchId&cohortName=$cohortExam&cohortClass=$cohortClass"
//    }
//
//    fun getBatchFormPreparedWebViewUrl(
//        url: String,
//        slug: String,
//        batchPrice: Float,
//        authToken: () -> String,
//        isFromCentres: Boolean,
//        dept: String,
//        cls: String,
//        centreId: String,
//        centreName: String,
//        cityId: String,
//        phase: String,
//        preference: String,
//        discountType: String,
//        schemeId: Int,
//        discountValue: Any,
//        courseDuration: String,
//        date: String,
//        batchId: String,
//        cohortExam: String,
//        cohortClass: String,
//    ): String {
//        val token = authToken()
//        val finalUrl =
//            "$url?token=${token}&APP_VERSION=${getAppVersionCode()}&client_type=${NetworkHeaders.DEVICE_TYPE_VALUE}&random_id=${randomId}" +
//                "&fromApp=true&batchSlug=$slug&showBatchForm=true&totalAmount=$batchPrice&widget=BATCH_FORM&isFromCentres=$isFromCentres&dept=$dept" +
//                "&cls=$cls&centreId=$centreId&centreName=$centreName&cityId=$cityId&phase=$phase&schemeId=$schemeId&discountName=$discountType&preference=$preference" +
//                "&discountAmount=$discountValue&courseDuration=$courseDuration&date=$date&batchId=$batchId&cohortName=$cohortExam&cohortClass=$cohortClass"
//        return finalUrl.replace(" ", "%20")
//    }
//
//    companion object {
//
//        private var currentVersion = String.EMPTY
//
//        @SuppressLint("StaticFieldLeak")
//        private var INSTANCE: NetworkUtils? = null
//
//        @JvmStatic
//        fun checkConnection(context: Context) = callbackFlow {
//
//            val callback = object : ConnectivityManager.NetworkCallback() {
//                override fun onAvailable(network: Network) {
//                    super.onAvailable(network)
//                    trySend(true)
//                }
//
//                override fun onLost(network: Network) {
//                    super.onLost(network)
//                    trySend(false)
//                }
//            }
//
//            val manager =
//                context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//            manager.registerNetworkCallback(
//                NetworkRequest.Builder()
//                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
//                    .build(),
//                callback,
//            )
//
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
//                trySend(manager.activeNetwork != null)
//            } else {
//                trySend(isConnectedToInternet(context))
//            }
//
//            awaitClose {
//                manager.unregisterNetworkCallback(callback)
//            }
//
//        }.catch { exception ->
//            exception.printStackTrace()
//        }.asLiveData()
//
//        @JvmStatic
//        fun isConnectedToInternet(context: Context): Boolean {
//            try {
//                val connectivityManager =
//                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//
//                val capabilities =
//                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
//                        ?: return false
//                return when {
//                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
//                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
//                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
//                    else -> false
//                }
//            } catch (e: Exception) {
//                return true
//            }
//        }
//
//        @JvmStatic
//        fun getConnectedNetworkType(context: Context): String {
//            try {
//
//                if (isConnectedToInternet(context).not()) {
//                    return CoreConstant.OFFLINE
//                }
//
//                val connectivityManager =
//                    context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//
//                val isConnectedToMobileData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    val networkCapabilities =
//                        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
//                    networkCapabilities != null && networkCapabilities.hasTransport(
//                        NetworkCapabilities.TRANSPORT_CELLULAR,
//                    )
//                } else {
//                    val networkInfo =
//                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
//                    networkInfo != null && networkInfo.isConnected
//                }
//
//                return if (isConnectedToMobileData) CoreConstant.MOBILE_DATA else CoreConstant.WIFI_DATA
//
//            } catch (e: java.lang.Exception) {
//
//            }
//
//            return String.EMPTY
//
//        }
//
//        @JvmStatic
//        fun getNetworkServiceProvider(context: Context): String {
//            try {
//                val telephonyManager =
//                    context.applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//                return telephonyManager.networkOperatorName
//            } catch (e: java.lang.Exception) {
//
//            }
//            return String.EMPTY
//        }
//
//
//        @JvmStatic
//        fun getInstance(context: Context): NetworkUtils {
//            if (INSTANCE == null) INSTANCE = NetworkUtils(context.applicationContext)
//            return INSTANCE!!
//        }
//    }
//
//}
