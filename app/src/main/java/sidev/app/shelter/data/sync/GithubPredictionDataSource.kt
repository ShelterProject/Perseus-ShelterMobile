package sidev.app.shelter.data.sync

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fetches a prediction JSON file straight from a public GitHub repo via raw.githubusercontent.com.
 * No Firebase/Cloud Storage involved (see docs/DATA_CONTRACT.md) — the [OkHttpClient] injected here
 * carries the disk cache from [sidev.app.shelter.core.network.NetworkModule], so an unchanged file
 * is served from the local HTTP cache instead of re-downloaded.
 */
@Singleton
class GithubPredictionDataSource @Inject constructor(
    private val okHttpClient: OkHttpClient,
) {
    companion object {
        // Update to the repo/branch that actually hosts the ML pipeline's weekly output.
        private const val RAW_BASE_URL =
            "https://raw.githubusercontent.com/zailbreck/Shelter_Cloud/main/predictions"
    }

    suspend fun fetchJson(fileName: String): String = withContext(Dispatchers.IO) {
        val request = Request.Builder().url("$RAW_BASE_URL/$fileName").build()
        okHttpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                error("Failed to fetch $fileName: HTTP ${response.code}")
            }
            requireNotNull(response.body) { "Empty body for $fileName" }.string()
        }
    }
}
