# Employee Directory

A native Android app that fetches employee data from a remote JSON API and lets the user
browse the list and view each employee's details. Built for the IDZ Digital Pvt. Ltd.
Software Developer assignment.

## What it does

- Fetches `GET https://aamras.com/dummy/EmployeeDetails.json` and displays every employee
  returned (no hardcoded data).
- Shows name, age, and salary for each employee in a scrollable, card-styled list.
- Tapping a row opens a separate `EmployeeDetailActivity` with that employee's full details.
- Handles loading, empty, and error states, including no internet / malformed JSON, with a
  Retry button and pull-to-refresh.

## Tech stack

| Concern        | Choice                                   |
|----------------|-------------------------------------------|
| Language        | Kotlin                                    |
| Min/Target SDK  | 24 / 34                                   |
| UI              | XML layouts + Material Components, ViewBinding |
| Networking      | Retrofit2 + Gson + OkHttp (+ logging interceptor) |
| Async           | Kotlin Coroutines + StateFlow             |
| List            | RecyclerView + ListAdapter/DiffUtil       |
| Navigation      | Explicit Intent to `EmployeeDetailActivity` |
| DI              | Hand-written `AppContainer` (no framework) |

## Architecture

Clean Architecture with three top-level source packages — `data`, `domain`, `presentation`
— and the feature name (`employeedirectory`) nested inside each, instead of a `feature/`
wrapper folder:

```
com.employeedirectory.app
├── data/employeedirectory/
│   ├── remote/        EmployeeApiService (Retrofit), EmployeeDto, NetworkModule
│   ├── mapper/         DTO → domain model mapping (drops entries with no name,
│   │                    preserves null age/salary instead of crashing)
│   └── repository/    EmployeeRepositoryImpl (implements the domain interface,
│                        turns exceptions into typed DataResult.Error)
├── domain/
│   ├── employeedirectory/model/        Employee (pure Kotlin, no Android deps)
│   ├── employeedirectory/repository/    EmployeeRepository interface
│   └── common/                          DataResult<T> (Success/Error) sealed wrapper
├── presentation/
│   ├── employeedirectory/list/         MainActivity, EmployeeListViewModel,
│   │                                    EmployeeAdapter, EmployeeListUiState
│   ├── employeedirectory/detail/        EmployeeDetailActivity
│   └── common/                          CurrencyFormatter
└── di/AppContainer.kt                   Single place wiring concrete implementations
```

**Why no UseCase classes / DI framework:** the assignment explicitly asks for a structure
"suitable for a junior Android developer assignment" and to avoid unnecessary complexity.
Repository-based Clean Architecture (domain owns the `Employee` model and
`EmployeeRepository` interface; data provides the implementation; presentation only talks
to the interface) gives real separation of concerns without one-method UseCase ceremony or
a DI framework's learning curve. Swapping in Hilt/Koin later only touches `AppContainer.kt`
and `ViewModelFactory`; nothing else changes.

**Presentation layer flow:** `MainActivity` observes `EmployeeListViewModel.uiState`
(`StateFlow<EmployeeListUiState>` — `Loading | Success | Empty | Error`) and toggles the
progress bar / RecyclerView / empty text / error+retry group accordingly. Tapping a row
passes the employee's name/age/salary as Intent extras (not a `Parcelable` whole object —
simpler, avoids extra serialization code, and is exactly as reliable for three primitive
fields) to `EmployeeDetailActivity`.

**Error handling (`EmployeeRepositoryImpl`):** catches `IOException` (no network) →
"No internet connection...", `HttpException` (non-2xx) → "Server error (code X)...",
`JsonSyntaxException` (malformed JSON) → "unreadable response", and generic exceptions as a
fallback. An empty `employees` array is a *different* UI state (`Empty`, "No employees
found") from a failed request (`Error`, with Retry). Entries missing a `name` are dropped
during mapping; entries missing only `age`/`salary` are kept and rendered as "N/A" — a
single malformed record never crashes the app or hides the rest of the list.

## Project structure (all files)

```
EmployeeDirectory/
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/employeedirectory/app/
│       │   │   ├── EmployeeDirectoryApp.kt
│       │   │   ├── di/AppContainer.kt
│       │   │   ├── data/employeedirectory/
│       │   │   │   ├── remote/EmployeeApiService.kt
│       │   │   │   ├── remote/EmployeeDto.kt
│       │   │   │   ├── remote/NetworkModule.kt
│       │   │   │   ├── mapper/EmployeeMapper.kt
│       │   │   │   └── repository/EmployeeRepositoryImpl.kt
│       │   │   ├── domain/
│       │   │   │   ├── common/DataResult.kt
│       │   │   │   └── employeedirectory/model/Employee.kt
│       │   │   │   └── employeedirectory/repository/EmployeeRepository.kt
│       │   │   └── presentation/
│       │   │       ├── common/CurrencyFormatter.kt
│       │   │       └── employeedirectory/
│       │   │           ├── list/MainActivity.kt
│       │   │           ├── list/EmployeeListViewModel.kt
│       │   │           ├── list/EmployeeListViewModelFactory.kt
│       │   │           ├── list/EmployeeListUiState.kt
│       │   │           ├── list/EmployeeAdapter.kt
│       │   │           └── detail/EmployeeDetailActivity.kt
│       │   └── res/
│       │       ├── layout/activity_main.xml
│       │       ├── layout/activity_employee_detail.xml
│       │       ├── layout/item_employee.xml
│       │       ├── drawable/ic_launcher.xml
│       │       ├── drawable/ic_chevron_right.xml
│       │       └── values/{colors,strings,themes}.xml
│       └── test/java/com/employeedirectory/app/
│           ├── data/employeedirectory/mapper/EmployeeMapperTest.kt
│           ├── data/employeedirectory/repository/EmployeeRepositoryImplTest.kt
│           ├── presentation/common/CurrencyFormatterTest.kt
│           └── presentation/employeedirectory/list/EmployeeListViewModelTest.kt
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── gradlew / gradlew.bat / gradle/wrapper/
```

## Currency formatting

The API returns `salary` as a plain number with no currency unit — the ₹ symbol is a
presentation choice, not something the API specifies. It lives in one place,
`CurrencyFormatter.CURRENCY_SYMBOL` (`presentation/common/CurrencyFormatter.kt`), so
switching to `$`, `€`, etc. is a one-line change.

## How this was verified (not just "it compiles")

This was built and run end-to-end, not just written:

1. **`./gradlew test`** — 21/21 unit tests pass (mapper edge cases, repository error
   mapping with a fake API, ViewModel state transitions with `kotlinx-coroutines-test`,
   currency formatting).
2. **`./gradlew assembleDebug`** — builds successfully, produces a ~6.6 MB debug APK.
3. **Installed and run on an Android emulator (API 36, arm64)**:
   - Launched the app, confirmed the live API response (7 employees) renders in the list.
   - Tapped a non-first row (Emma) and confirmed the detail screen shows the *correct*
     employee (₹45,000, not the first row's data) — verifies correct item passing, not just
     "a" detail screen opening.
   - Verified back navigation returns to the list.
   - Disabled Wi-Fi/mobile data on the emulator, relaunched, and confirmed the
     "No internet connection..." error view with a Retry button appears (not a crash or
     blank screen).
   - Re-enabled networking, tapped Retry, confirmed the list loads successfully.
   - One real bug was caught this way and fixed: the `RecyclerView` had no
     `LayoutManager` attached initially, so the first build loaded data correctly (per
     logcat) but rendered nothing — `No layout manager attached; skipping layout` in
     logcat. Fixed in `MainActivity.onCreate()` by setting
     `recyclerEmployees.layoutManager = LinearLayoutManager(this)`. This is exactly the
     kind of bug that "it compiles" would have missed.

## How to open and run in Android Studio

1. Open Android Studio (Giraffe/Koala or newer recommended).
2. **File → Open** and select the `EmployeeDirectory/` folder (the one containing
   `settings.gradle.kts`).
3. Let Gradle sync finish (first sync downloads dependencies — needs internet access).
4. Select a device/emulator with **API 24+** from the device dropdown.
5. Click **Run ▶** (or Shift+F10). The app launches straight into the employee list.

No local.properties setup is required beyond Android Studio's default `sdk.dir` detection;
if it's ever missing, create `local.properties` in the project root with:
```
sdk.dir=/path/to/your/Android/sdk
```

## Building a debug APK

**From Android Studio:** `Build → Build Bundle(s) / APK(s) → Build APK(s)`. When it
finishes, click the "locate" link in the notification, or find it at:

```
app/build/outputs/apk/debug/app-debug.apk
```

**From the command line** (from the project root):
```bash
./gradlew assembleDebug
```
The APK is written to the same `app/build/outputs/apk/debug/app-debug.apk` path.

## Installing on a real device

1. On the device: **Settings → About phone → tap "Build number" 7×** to enable Developer
   Options, then **Settings → Developer options → enable USB debugging**.
2. Connect the device via USB and accept the "Allow USB debugging" prompt.
3. From the project root:
   ```bash
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```
   (Or copy the APK to the device and open it directly — allow "install from unknown
   sources" for the file manager/browser used, if prompted.)
4. Launch "Employee Directory" from the app drawer. It needs an active internet connection
   to load the employee list.

## Manual testing checklist

- [ ] App launches and shows a loading indicator, then the employee list.
- [ ] All employees from the live API appear (currently 7 records — James, Robert, Emma,
      Mohammed, Kumar, Ganesh, Krish), not just the 2 sample records from the assignment
      spec.
- [ ] Each row shows name, age, and formatted salary (e.g. ₹17,000).
- [ ] Tapping a row opens the detail screen for *that* row's employee (test a row other
      than the first).
- [ ] Detail screen shows the correct name, age, and salary.
- [ ] Back button / back gesture from the detail screen returns to the list.
- [ ] Pull-to-refresh on the list re-fetches data.
- [ ] Turn off Wi-Fi/mobile data, relaunch or pull-to-refresh → error message + Retry
      button appear (no crash).
- [ ] Turn network back on, tap Retry → list loads successfully.
- [ ] (Optional, to see the empty state) point the API base URL at an endpoint returning
      `{"employees": []}` → "No employees found" message appears instead of an error.
- [ ] Rotate the device on both the list and detail screens → no crash (state may reload,
      which is expected since this app doesn't persist across configuration changes beyond
      what the OS/ViewModel lazy-recreation already provides).
- [ ] Run on an emulator at minSdk (API 24) and at a modern API level (34+) — UI renders
      correctly on both.

### Automated tests

```bash
./gradlew test
```
Covers: DTO→domain mapping (valid data, blank/null names dropped, missing age/salary
preserved as null, name trimming, empty list), repository error mapping (success, filtered
invalid entries, null `employees` field, `IOException` → network error message, unexpected
exception → generic error), ViewModel state transitions (Success, Empty, Error, and the
Loading→final-state sequence), and currency formatting (grouping, null → "N/A", rounding).

## Submission checklist

- [ ] Zip the project folder *excluding* `build/`, `.gradle/`, and `.idea/` (or `git clone`
      a clean copy) for source code submission.
- [ ] Attach `app/build/outputs/apk/debug/app-debug.apk` as the APK deliverable.
- [ ] Confirm `./gradlew test` passes and `./gradlew assembleDebug` succeeds one final time
      right before submitting.
- [ ] Double-check the APK installs and the employee list loads on a device/emulator with
      no other setup.
- [ ] Include this README (setup/run/build instructions) in the submission.
# employee-task
