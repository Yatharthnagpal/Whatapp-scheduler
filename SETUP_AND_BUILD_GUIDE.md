# WhatsApp Scheduler — Android Studio Setup & APK Generation Guide

This guide provides step-by-step instructions for importing, building, running, testing, and generating APKs (Debug and Signed Release) for the **WhatsApp Scheduler** Android application.

---

## 📋 Prerequisites

Before starting, ensure your development environment has the following installed:

- **Android Studio**: Jellyfish (2024.1.1), Ladybug, or newer.
- **JDK**: Java Development Kit 17 (bundled with modern Android Studio).
- **Android SDK**: API Level 35 (Android 15) or API Level 34 (Android 14) SDK installed via SDK Manager.
- **Device / Emulator**: Android 8.0 (API 26) or higher. (Android 13+ recommended for notification permission testing).
- **Target Apps**: WhatsApp or WhatsApp Business installed on the test device.

---

## 🛠️ Step 1: Importing the Project into Android Studio

1. Open **Android Studio**.
2. On the Welcome screen (or via top menu `File > Open`), select the project root folder:
   ```
   c:\Users\Yatharth nagpal\Desktop\whatsapp message secheduler
   ```
3. Android Studio will automatically detect the Gradle setup and initialize the project structure.
4. Click **File > Sync Project with Gradle Files** to download all required dependencies (`Hilt`, `Room`, `Jetpack Compose`, `Coroutines`, etc.).
5. Confirm that the **Build** tool window shows `BUILD SUCCESSFUL`.

---

## 🧪 Step 2: Running Unit Tests

Run the test suite to verify Room database operations and domain business logic:

### Option A: Via Android Studio GUI
1. In the Project tool window, navigate to:
   `app/src/test/java/com/yatharth/whatsappscheduler`
2. Right-click the folder and select **Run 'Tests in com.yatharth...'**.
3. Verify all test cases pass:
   - `ScheduledMessageDaoTest`: Database CRUD, atomic claims, and status filtering.
   - `UseCasesTest`: Input validation and time calculation rules.

### Option B: Via Terminal / Command Line
Open the terminal at the project root and run:
```bash
./gradlew test
```
*(On Windows Command Prompt: `gradlew.bat test`)*

---

## 📱 Step 3: Running the App on Emulator / Device

1. Connect your physical Android device via USB (with **USB Debugging** enabled) or launch an Android Virtual Device (AVD) emulator.
2. Ensure your target device is selected in the top toolbar.
3. Click the green **Run 'app'** button (`Shift + F10`).
4. **App Permissions Setup**:
   - **Contacts Permission**: Open the **Contacts** tab -> Tap **Grant Contacts Permission**.
   - **Notifications**: On Android 13+, allow notification popups when prompted.
   - **Exact Alarms**: On Android 12+, ensure exact alarm permissions are granted under Android system settings if prompted.

---

## 📦 Step 4: Generating Debug APK

The Debug APK can be installed on any test device or emulator directly without manual signing keys.

### Method 1: Using Android Studio UI (Recommended)
1. In Android Studio, go to menu:
   **Build > Build Bundle(s) / APK(s) > Build APK(s)**
2. Wait for Gradle to complete building.
3. A notification popup will appear at the bottom right corner:
   > *APK(s) generated successfully for 1 module.*
4. Click **locate** inside the notification banner to open the output folder in File Explorer.
5. **APK Output Location**:
   ```
   app/build/outputs/apk/debug/app-debug.apk
   ```

### Method 2: Using Terminal / Command Line
Run the following command from the project root:
```bash
./gradlew assembleDebug
```
*(On Windows Command Prompt / PowerShell: `.\gradlew.bat assembleDebug`)*

---

## 🔑 Step 5: Generating Signed Release APK

For production distribution or sharing an optimized, minified APK, build a Signed Release APK with R8 code shrinking enabled.

### 1. Create a Keystore (If you don't have one)
1. In Android Studio menu, go to:
   **Build > Generate Signed Bundle / APK...**
2. Select **APK** and click **Next**.
3. Under **Key store path**, click **Create new...**.
4. Set a path (e.g., `release-key.jks`), set passwords, and fill out Key Alias details:
   - **Alias**: `whatsappscheduler`
   - **Password**: (Choose a secure password)
   - **Validity**: `25` years
5. Click **OK**.

### 2. Build the Signed APK
1. Back in the **Generate Signed Bundle / APK** dialog, click **Next**.
2. Select build variant: **release**.
3. Select signature versions: Check **V1 (Jar Signature)** and **V2 (Full APK Signature)**.
4. Click **Create**.
5. **Signed APK Output Location**:
   ```
   app/build/outputs/apk/release/app-release.apk
   ```

---

## 📲 Step 6: Installing the APK on a Device

### Via ADB (Android Debug Bridge):
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Via Direct Transfer:
1. Copy `app-debug.apk` or `app-release.apk` to your phone's storage.
2. Open the **Files** app on your phone.
3. Tap the `.apk` file and grant "Install unknown apps" permission when prompted.
4. Tap **Install** and open the app!

---

## 📄 Summary of Architecture & Security Boundaries

- **Database**: Local SQLite database `whatsapp_scheduler.db` managed via Room.
- **Scheduling**: Powered by `AlarmManager` with atomic status updates and exact alarm dispatch.
- **WhatsApp Integration**: Complies strictly with Android security policies — opens WhatsApp via official `Intent` dispatches with pre-filled target number and text (`SendResult.RequiresUserAction`). No hidden background scraping or account risk.
