# Shelter Mobile v2

Remake dari [`zailbreck/Shelter_Mobile`](https://github.com/zailbreck/Shelter_Mobile) — Jetpack
Compose + Material 3, `minSdk 31` (Android 12+), Firebase Authentication, Hilt, Room.

Backend lama ([`zailbreck/Shelter_Cloud`](https://github.com/zailbreck/Shelter_Cloud)) juga sedang
direncanakan untuk di-remake terpisah; lihat [`docs/DATA_CONTRACT.md`](docs/DATA_CONTRACT.md) untuk
kontrak data yang jadi acuannya.

```bash
./gradlew :app:assembleDebug
```

Setup environment Ubuntu 24.04/Zorin OS 18 dari nol (JDK, Android SDK, opsional Android
Studio/emulator): lihat [`scripts/setup-ubuntu-dev-env.sh`](scripts/setup-ubuntu-dev-env.sh).

```bash
chmod +x scripts/setup-ubuntu-dev-env.sh
./scripts/setup-ubuntu-dev-env.sh            # SDK command-line saja
./scripts/setup-ubuntu-dev-env.sh --with-studio   # + Android Studio & emulator
```

## Kenapa strukturnya begini

- **Firebase Authentication** untuk login/register — bukan sistem token PHP lama yang menyimpan
  password plaintext.
- **Firestore** hanya untuk data kecil & per-user: profile (`users/{uid}`), `news`, `reports`.
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
- Setup nyata project Firebase (`google-services.json`) — module ini build tanpa file itu
  (plugin `google-services` hanya diterapkan kalau filenya ada), tapi layar Login/Register baru
  bisa dicoba end-to-end setelah `google-services.json` valid ditaruh di `app/`.
- Rewrite backend `Shelter_Cloud` (Firestore security rules, dan tempat pipeline ML meng-upload
  `predictions/*.json` — lihat `docs/DATA_CONTRACT.md`).

## Setup manual yang diperlukan sebelum run di device/emulator

1. Buat project Firebase baru (atau pakai yang lama kalau masih ada), aktifkan **Authentication
   (Email/Password)** dan **Firestore** (mode production, Spark plan — jangan aktifkan Storage).
2. Download `google-services.json`, taruh di `app/google-services.json`.
3. Sesuaikan `GithubPredictionDataSource.RAW_BASE_URL` ke repo/branch tempat file
   `predictions/*.json` sebenarnya di-host.
