package org.proninyaroslav.opencomicvine.model

import android.content.Context
import android.content.pm.PackageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.supportGetPackageInfo
import javax.inject.Inject

interface AppInfoProvider {
    sealed interface State {
        data class Success(
            val appName: String,
            val version: String,
        ) : State

        data class Failed(val exception: Exception) : State
    }

    fun getAppInfo(): State
}

class AppInfoProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : AppInfoProvider {
    override fun getAppInfo(): AppInfoProvider.State {
        return try {
            context.packageManager.supportGetPackageInfo(context.packageName).run {
                AppInfoProvider.State.Success(
                    appName = context.getString(R.string.app_name),
                    version = versionName,
                )
            }
        } catch (e: PackageManager.NameNotFoundException) {
            AppInfoProvider.State.Failed(e)
        }
    }
}