package id.my.kulitech.nippia.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import id.my.kulitech.nippia.MainActivity
import id.my.kulitech.nippia.R

data class WidgetStatsSnapshot(
    val streakDays: Int = 12,
    val dailyXp: Int = 35,
    val dailyXpTarget: Int = 50,
    val hearts: Int = 5,
    val leagueLabel: String = "Liga Emas #4",
    val lastTryoutScore: Int = 412,
    val lastTryoutPassed: Boolean = true,
    val reminderTime: String = "19:30 WIB"
)

object NippiaWidgetStore {
    private const val PREFS = "nippia_widget_stats"

    fun save(context: Context, stats: WidgetStatsSnapshot) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putInt("streak", stats.streakDays)
            .putInt("dailyXp", stats.dailyXp)
            .putInt("dailyXpTarget", stats.dailyXpTarget)
            .putInt("hearts", stats.hearts)
            .putString("leagueLabel", stats.leagueLabel)
            .putInt("lastTryoutScore", stats.lastTryoutScore)
            .putBoolean("lastTryoutPassed", stats.lastTryoutPassed)
            .putString("reminderTime", stats.reminderTime)
            .apply()
        NippiaWidgetUpdater.updateAll(context)
    }

    fun load(context: Context): WidgetStatsSnapshot {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return WidgetStatsSnapshot(
            streakDays = prefs.getInt("streak", 12),
            dailyXp = prefs.getInt("dailyXp", 35),
            dailyXpTarget = prefs.getInt("dailyXpTarget", 50).coerceAtLeast(1),
            hearts = prefs.getInt("hearts", 5),
            leagueLabel = prefs.getString("leagueLabel", "Liga Emas #4") ?: "Liga Emas #4",
            lastTryoutScore = prefs.getInt("lastTryoutScore", 412),
            lastTryoutPassed = prefs.getBoolean("lastTryoutPassed", true),
            reminderTime = prefs.getString("reminderTime", "19:30 WIB") ?: "19:30 WIB"
        )
    }
}

object NippiaWidgetUpdater {
    fun updateAll(context: Context) {
        val mgr = AppWidgetManager.getInstance(context)
        listOf(
            NippiaSmallWidgetProvider::class.java to R.layout.widget_small,
            NippiaMediumWidgetProvider::class.java to R.layout.widget_medium,
            NippiaLargeWidgetProvider::class.java to R.layout.widget_large
        ).forEach { (cls, layoutId) ->
            val ids = mgr.getAppWidgetIds(ComponentName(context, cls))
            if (ids.isNotEmpty()) {
                val intent = Intent(context, cls).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                }
                context.sendBroadcast(intent)
            }
        }
    }
}

private fun launchAppPendingIntent(context: Context, requestCode: Int): PendingIntent {
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    return PendingIntent.getActivity(
        context,
        requestCode,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}

class NippiaSmallWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val stats = NippiaWidgetStore.load(context)
        val pct = ((stats.dailyXp * 100) / stats.dailyXpTarget).coerceIn(0, 100)
        val pi = launchAppPendingIntent(context, 101)
        for (id in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_small).apply {
                setTextViewText(R.id.widget_streak_text, "🔥 ${stats.streakDays} Hari")
                setTextViewText(R.id.widget_xp_text, "${stats.dailyXp}/${stats.dailyXpTarget} XP")
                setProgressBar(R.id.widget_xp_progress, 100, pct, false)
                setOnClickPendingIntent(R.id.widget_root, pi)
                setOnClickPendingIntent(R.id.widget_btn_action, pi)
            }
            appWidgetManager.updateAppWidget(id, views)
        }
    }
}

class NippiaMediumWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val stats = NippiaWidgetStore.load(context)
        val pct = ((stats.dailyXp * 100) / stats.dailyXpTarget).coerceIn(0, 100)
        val pi = launchAppPendingIntent(context, 102)
        for (id in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_medium).apply {
                setTextViewText(R.id.widget_streak_text, "🔥 ${stats.streakDays} Hari")
                setTextViewText(R.id.widget_hearts_text, "${stats.hearts} ❤️")
                setTextViewText(R.id.widget_xp_text, "Target Harian: ${stats.dailyXp}/${stats.dailyXpTarget} XP")
                setProgressBar(R.id.widget_xp_progress, 100, pct, false)
                setOnClickPendingIntent(R.id.widget_root, pi)
                setOnClickPendingIntent(R.id.widget_btn_action, pi)
            }
            appWidgetManager.updateAppWidget(id, views)
        }
    }
}

class NippiaLargeWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val stats = NippiaWidgetStore.load(context)
        val pct = ((stats.dailyXp * 100) / stats.dailyXpTarget).coerceIn(0, 100)
        val piQuiz = launchAppPendingIntent(context, 103)
        val piCat = launchAppPendingIntent(context, 104)
        for (id in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_large).apply {
                setTextViewText(R.id.widget_streak_text, "🔥 ${stats.streakDays} Hari")
                setTextViewText(R.id.widget_league_text, "🏆 ${stats.leagueLabel}")
                setTextViewText(R.id.widget_xp_text, "${stats.dailyXp}/${stats.dailyXpTarget} XP ($pct%)")
                setProgressBar(R.id.widget_xp_progress, 100, pct, false)
                setTextViewText(R.id.widget_tryout_score, "${stats.lastTryoutScore}/550")
                setTextViewText(
                    R.id.widget_tryout_badge,
                    if (stats.lastTryoutPassed) "Lolos PG" else "Belum PG"
                )
                setTextViewText(R.id.widget_reminder_text, "🔔 Pengingat: ${stats.reminderTime}")
                setOnClickPendingIntent(R.id.widget_root, piQuiz)
                setOnClickPendingIntent(R.id.widget_btn_quiz, piQuiz)
                setOnClickPendingIntent(R.id.widget_btn_cat, piCat)
            }
            appWidgetManager.updateAppWidget(id, views)
        }
    }
}
