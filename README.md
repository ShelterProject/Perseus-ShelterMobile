# Shelter Mobile v2

Remake dari [`zailbreck/Shelter_Mobile`](https://github.com/zailbreck/Shelter_Mobile) — Jetpack
Compose + Material 3, `minSdk 31` (Android 12+), Firebase Authentication (email/password + Google
Sign-In), Hilt, Room.

```bash
./gradlew :app:assembleDebug
```

APK hasil build ada di `app/build/outputs/apk/debug/app-debug.apk`.

Setup environment Ubuntu 24.04/Zorin OS 18 dari nol (JDK, Android SDK, opsional Android
Studio/emulator): lihat [`scripts/setup-ubuntu-dev-env.sh`](scripts/setup-ubuntu-dev-env.sh).

```bash
chmod +x scripts/setup-ubuntu-dev-env.sh
./scripts/setup-ubuntu-dev-env.sh                 # SDK command-line saja
./scripts/setup-ubuntu-dev-env.sh --with-studio   # + Android Studio & emulator
```

## Kenapa strukturnya begini

- **Firebase Authentication** (email/password + Google Sign-In lewat Credential Manager) untuk
  login/register — bukan sistem token PHP lama yang menyimpan password plaintext.
- **Firestore** hanya untuk data kecil & per-user: profile (`users/{uid}`), `news`, `reports`
  (dua yang terakhir masih scope iterasi berikutnya). Security rules ada di
  [`firestore.rules`](firestore.rules).
- **Prediksi bencana & cuaca** (~7rb baris × 6 tipe) **bukan** di Firestore/Cloud Storage —
  di-fetch sebagai file JSON dari GitHub raw, di-cache ke Room, disinkron mingguan lewat
  `WorkManager`, dengan progress bar non-dismissable selama proses. Alasan lengkap & kontraknya
  ada di [`docs/DATA_CONTRACT.md`](docs/DATA_CONTRACT.md).
- **Tidak ada satupun komponen yang butuh kartu kredit/Blaze plan** — murni Firebase Spark +
  GitHub raw (gratis) + Room lokal.

## Struktur package

```
id.my.shelter.app/
├── core/        # DI (Hilt), Firebase providers, OkHttp+cache, Room database, Resource/SyncState, tema Compose
├── domain/      # model (murni Kotlin) + repository interfaces — tidak tahu soal Firebase/Room/GitHub
├── data/        # implementasi repository per fitur (auth, disaster, weather, sync)
└── feature/     # Compose screens + ViewModel per fitur (auth, dashboard, sync, navigation)
```

**Pola yang dipakai ulang untuk fitur berikutnya (News, Report, Location, Profile):**
1. `domain/model` — model murni Kotlin, tidak bergantung Firestore/Room.
2. `domain/repository` — interface, dipakai ViewModel.
3. `data/<fitur>` — implementasi repository (Firestore untuk data kecil/per-user; Room+GitHub raw
   kalau datanya besar/batch, ikuti pola `data/disaster`+`data/sync`).
4. `core/di/RepositoryModule` — `@Binds` interface ke implementasinya.
5. `feature/<fitur>` — `@HiltViewModel` + Composable screen, ditambahkan ke `ShelterNavHost`.

## Yang belum digarap (scope iterasi berikutnya)

- Fitur News, Report (kirim laporan + telepon darurat), daftar Location, edit Profile.
- Rewrite backend `Shelter_Cloud` (pipeline ML meng-upload `predictions/*.json` — lihat
  `docs/DATA_CONTRACT.md`).
- Release signing sudah di-wiring (lihat di bawah) tapi `isMinifyEnabled` masih `false` —
  ProGuard/R8 belum diaktifkan buat build release.

## Setup Firebase (sekali di awal)

1. Buat project Firebase, aktifkan **Authentication → Email/Password** dan **Google** (jangan
   aktifkan Cloud Storage/Cloud Functions — keduanya butuh Blaze plan berbayar).
2. Tambahkan app Android dengan package name **persis** `id.my.shelter.app`.
3. Tambahkan SHA-1 & SHA-256 dari keystore (debug: `keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android`) di Project Settings → app tersebut.
4. Download `google-services.json`, taruh di `app/google-services.json` (gitignored, tidak
   ke-commit). **Download ulang** setiap kali fingerprint atau provider sign-in berubah — cek
   `grep client_type app/google-services.json` harus ada entri `client_type: 3` (Web client,
   dipakai Google Sign-In lewat `default_web_client_id`).
5. Deploy `firestore.rules` (paste manual di Console → Firestore → Rules, atau
   `firebase deploy --only firestore:rules` kalau sudah install `firebase-tools`).
6. Sesuaikan `GithubPredictionDataSource.RAW_BASE_URL` ke repo/branch tempat file
   `predictions/*.json` sebenarnya di-host.

## Release signing

Keystore rilis **tidak boleh masuk repo**. Copy [`keystore.properties.example`](keystore.properties.example)
jadi `keystore.properties` di root repo, isi dengan path & password keystore kamu sendiri (file ini
sudah gitignored). Detail lengkap generate keystore ada di histori chat/dokumentasi setup — intinya:

```bash
keytool -genkeypair -v -keystore ~/shelter-release.jks -alias shelter -keyalg RSA -keysize 2048 -validity 10000
```

lalu isi `keystore.properties`, dan `./gradlew :app:assembleRelease` otomatis akan
menandatanganinya.
