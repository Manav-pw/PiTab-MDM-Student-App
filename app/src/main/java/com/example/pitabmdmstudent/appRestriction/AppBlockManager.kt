package com.example.pitabmdmstudent.appRestriction

import android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_HOME
import android.content.Context
import android.util.Log
import androidx.core.content.edit
import com.example.pitabmdmstudent.models.AppBlockRule
import com.example.pitabmdmstudent.services.MyAccessibilityService
import kotlinx.coroutines.delay
import org.json.JSONArray
import org.json.JSONObject

object AppBlockManager {
    // pkg -> alwaysBlock, usageLimitSeconds
    private val rules = mutableMapOf<String, AppBlockRule>()

    private const val APP_BLOCKER = "app_blocker"
    private const val APP_BLOCK_RULES = "app_block_rules"

    fun initialize(context: Context) {
        rules.clear()
        rules.putAll(loadRules(context))
    }

    // TODO: Replace shared prefs with database or datastore for better performance/scalability
    fun saveRules(context: Context, rules: Map<String, AppBlockRule>) {
        val prefs = context.getSharedPreferences(APP_BLOCKER, Context.MODE_PRIVATE)
        prefs.edit {
            val json = JSONArray().apply {
                rules.forEach { (pkg, rule) ->
                    put(
                        JSONObject().apply {
                            put("packageName", pkg)
                            put("alwaysBlocked", rule.alwaysBlocked)
                            put("usageLimitSeconds", rule.usageLimitSeconds)
                        }
                    )
                }
            }

            putString(APP_BLOCK_RULES, json.toString())
        }
    }

    fun loadRules(context: Context): MutableMap<String, AppBlockRule> {
        Log.d("BlockTest", "Loading rules from prefs")
        val prefs = context.getSharedPreferences(APP_BLOCKER, Context.MODE_PRIVATE)
        val jsonStr = prefs.getString(APP_BLOCK_RULES, null) ?: return mutableMapOf()

        val result = mutableMapOf<String, AppBlockRule>()
        val arr = JSONArray(jsonStr)

        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            val pkg = obj.getString("packageName")
            val always = obj.getBoolean("alwaysBlocked")
            val limit = obj.getLong("usageLimitSeconds")

            result[pkg] = AppBlockRule(
                alwaysBlocked = always,
                usageLimitSeconds = limit
            )
        }
        return result
    }

    fun updateRuleFromSocket(
        context: Context,
        packageName: String,
        alwaysBlocked: Boolean,
        usageLimitSeconds: Long
    ) {
        if (!alwaysBlocked && usageLimitSeconds <= 0) {
            // remove restriction
            rules.remove(packageName)
            Log.d("BlockTest", "Rule removed for $packageName")
        } else {
            rules[packageName] = AppBlockRule(alwaysBlocked, usageLimitSeconds)
            Log.d("BlockTest", "Rule added/updated for $packageName")
        }
        saveRules(context, rules)
    }

    fun getRule(packageName: String): AppBlockRule? =
        rules[packageName]

    // block, kill and show overlay
    suspend fun checkAndEnforceBlocking(
        packageName: String,
    ) {
        val rule = getRule(packageName) ?: return

        val isAppBlock = rule.alwaysBlocked

        if (!isAppBlock) return

        val title = "App blocked"
        val msg = "This app is blocked by your parent."

        blockAppNow(packageName, title, msg)
    }

    suspend fun blockAppNow(packageName: String, title: String, message: String) {
        Log.d("BlockTest", "Moving to home screen n launching block screen for $packageName")

        MyAccessibilityService.instance?.performGlobalAction(GLOBAL_ACTION_HOME)
        delay(200)
        MyAccessibilityService.instance?.showBlockScreen(packageName, title, message)
    }
}
