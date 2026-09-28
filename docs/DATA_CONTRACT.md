# Data Contract — Shelter Mobile v2

Referensi untuk backend (`Shelter_Cloud` remake) dan pipeline ML, sesuai keputusan arsitektur di
sesi remake app: **full gratis, tanpa kartu kredit** (lihat alasan di bawah).

## Kenapa begini (ringkas)

- Backend lama (`Shelter_Cloud/API/v1/shelter_api.php`) adalah satu "god endpoint" PHP yang
  dibedakan lewat field `_requestType`, dan password user disimpan **plaintext** di MySQL.
- Firebase Cloud Storage & Cloud Functions untuk project baru sekarang mewajibkan **Blaze plan**
  (perlu kartu kredit walau ada kuota gratis di dalamnya) — jadi **tidak dipakai** di sini.
- Data prediksi ML berjumlah besar (kira-kira 7.000 baris × 6 tipe). Kalau disimpan 1-baris-1-dokumen
  di Firestore, quota baca gratis (50rb read/hari) cepat habis. Solusinya: prediksi disimpan sebagai
  **file JSON batch di GitHub (raw.githubusercontent.com)**, bukan Firestore, bukan Cloud Storage.

## A. Firestore (Spark plan, gratis tanpa kartu kredit)

Dipakai untuk data yang volumenya kecil dan butuh write per user.

### `users/{uid}`
| field | tipe | keterangan |
|---|---|---|
| fullName | string | |
| gender | string | `MALE` \| `FEMALE` |
| address | string? | opsional |

`uid` dan `email` sendiri **tidak** disimpan di sini — keduanya berasal dari Firebase Authentication.

### `news/{autoId}` (iterasi berikutnya)
| field | tipe |
|---|---|
| timestamp | timestamp |
| title | string |
| briefDesc | string |
| linkImage | string |
| link | string |
| type | int (1 = news, 2 = article) |

### `reports/{autoId}` (iterasi berikutnya)
| field | tipe |
|---|---|
| uid | string |
| timestamp | timestamp |
| method | int (1 = call, 2 = message) |
| type | string (`Urgent`/`Feedback`) |
| status | bool |
| message | string? |
| responseText | string? |
| photoUrl | string? |
| location | geopoint? |

Security rules (dibuat saat sesi backend): user hanya boleh `create`/`read` dokumen `reports` miliknya
sendiri (`request.auth.uid == resource.data.uid`); admin dapat `read`/`update` semua.

## B. Prediksi Bencana & Cuaca — file JSON di GitHub, bukan Firestore/Cloud Storage

Di-fetch dari `https://raw.githubusercontent.com/<owner>/<repo>/<branch>/predictions/<file>.json`
(default di app: `zailbreck/Shelter_Cloud`, branch `main` — sesuaikan
`GithubPredictionDataSource.RAW_BASE_URL` kalau pipeline ML mengunggah ke repo/branch lain).

Setiap file berisi **array JSON** (bukan satu dokumen per baris), sehingga 1 fetch = seluruh dataset,
bukan ribuan request. Field wajib sama persis dengan DTO Kotlin (`data/disaster/remote/*.kt`,
`data/weather/remote/*.kt`) supaya parsing tidak perlu mapping tambahan.

### `predictions/earthquake.json`
```json
[{
  "location": "string", "date": "yyyy-MM-dd", "avgMagnitude": 0.0,
  "latMin": 0.0, "latMax": 0.0, "lonMin": 0.0, "lonMax": 0.0,
  "depthMinKm": 0.0, "depthMaxKm": 0.0
}]
```

### `predictions/landslide.json`
```json
[{ "location": "string", "date": "yyyy-MM-dd", "condition": "string" }]
```

### `predictions/flood.json`
```json
[{
  "village": "string", "address": "string",
  "lat": 0.0, "lon": 0.0, "date": "yyyy-MM-dd", "condition": "string"
}]
```

### `predictions/forest_fire.json`
Satu objek per tanggal (mengikuti shape asli `karhutla_from_server.json`: 1 baris, banyak kolom
kabupaten), bukan 1 objek per kabupaten:
```json
[{ "date": "yyyy-MM-dd", "regions": { "Asahan": 83, "Dairi": 48 } }]
```

### `predictions/weather.json`
```json
[{
  "date": "yyyy-MM-dd", "temperature": 0.0, "humidity": 0.0, "rainfall": 0.0,
  "windSpeed": 0.0, "uvIndex": 0.0, "condition": "string"
}]
```

## Mekanisme sync (app side)

1. `SyncEngine` mencoba fetch tiap file dari GitHub raw lewat `OkHttpClient` yang punya disk cache —
   file yang belum berubah otomatis kena `304`/dilayani dari cache (hemat kuota, tanpa hash manual).
2. Kalau fetch gagal **dan** dataset itu belum pernah sinkron sebelumnya, fallback ke seed di
   `app/src/main/assets/predictions/*.json` (contoh kecil, bukan dataset penuh) supaya Room tidak
   kosong di percobaan pertama tanpa internet.
3. Data di-decode lalu di-insert ke Room **per-chunk 500 baris**, bukan satu transaksi besar — progres
   dilaporkan lewat `SyncStatusRepository` (`StateFlow<SyncState>`) yang diobservasi
   `feature/sync/SyncOverlay.kt` (dialog non-dismissable, tidak bisa diganggu selama sync jalan).
4. Dijadwalkan **mingguan** lewat `WorkManager` (`PredictionSyncWorker`), plus dicek oportunistik
   setiap `DashboardViewModel` dibuka kalau cache lebih tua dari 7 hari.

## Untuk pipeline ML / Shelter_Cloud

Setelah proses ML mingguan selesai, cukup **push/replace file JSON** di atas ke path
`predictions/<jenis>.json` pada repo & branch yang dikonfigurasi di
`GithubPredictionDataSource.RAW_BASE_URL`. App menarik perubahan itu otomatis dalam ≤7 hari (atau
lebih cepat kalau user membuka Dashboard). Tidak perlu endpoint API tambahan untuk data ini.
