package id.my.shelter.app.data.sync

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/** First-install seed: reads the bundled copy in assets/predictions/ so Room isn't empty offline. */
@Singleton
class AssetPredictionDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    suspend fun readJson(fileName: String): String = withContext(Dispatchers.IO) {
        context.assets.open("predictions/$fileName").bufferedReader().use { it.readText() }
    }
}
