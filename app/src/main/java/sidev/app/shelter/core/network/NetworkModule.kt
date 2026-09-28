package sidev.app.shelter.core.network

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * A single OkHttpClient with a disk [Cache] backs every GitHub raw-content fetch.
 * raw.githubusercontent.com serves ETag/Cache-Control headers, so this client alone is
 * what makes the weekly sync cheap: unchanged files come back as a 304 with (almost) no bytes,
 * no custom hash/version bookkeeping needed on our side.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val CACHE_SIZE_BYTES = 10L * 1024 * 1024 // 10MB

    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        val cache = Cache(File(context.cacheDir, "http_cache"), CACHE_SIZE_BYTES)
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder()
            .cache(cache)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()
    }
}
