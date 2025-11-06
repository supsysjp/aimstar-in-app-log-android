# Aimstar In-App Messaging Android SDK

## Requirements

- ランタイム: Android 6.0 (API 23) 以上
- 開発環境: Android Studio Iguana 以上、JDK 17

## 制限事項

- マルチウインドウモードの同時表示には対応していません
- Jetpack Compose など完全な Compose ベースの UI では表示レイアウトが崩れる可能性があるため、現時点では非推奨です

## SDK で提供する機能について

- アプリのページ閲覧イベントを Aimstar に送信する
- コンバージョンボタンのタップといった各イベントを送信する

## 用語

| 用語 | 説明 |
| - | - |
| API Key | Aimstar In-App Messaging を利用するために必要な API キーで、Aimstar 側で事前にアプリ開発者に発行されます。 |
| Tenant ID | Aimstar In-App Messaging を利用するために必要なテナント ID で、Aimstar 側で事前にアプリ開発者に発行されます。 |
| Customer ID | アプリ開発者がユーザーを識別する ID で、アプリ開発者が独自に発行、生成、または利用します。 |

## 導入手順

### 1. SDK をアプリに追加する

1. Aimstar が提供する `aimstar-in-app-log.aar` をダウンロードし、`app/libs` ディレクトリに配置します
2. モジュールの `build.gradle.kts`（または `build.gradle`）に以下を追記します

```kotlin
repositories {
    flatDir { dirs("libs") }
}

dependencies {
    implementation(files("libs/aimstar-in-app-log.aar"))
}
```

3. ProGuard / R8 を利用している場合は、Aimstar 提供のルールファイルを併せて追加してください

### 2. SDK の初期化とイベントリスナーを設定する

アプリ起動時に API キーとテナント ID を設定し、イベントリスナーを登録します。`Application` クラスでの初期化例を示します。

```kotlin
import android.app.Application
import jp.aimstar.messaging.AimstarInAppMessaging
import jp.aimstar.messaging.AimstarInAppMessagingListener
import jp.aimstar.messaging.model.InAppMessage

class SampleApp : Application() {

    override fun onCreate() {
        super.onCreate()

        val apiKey = "YOUR API KEY"
        val tenantId = "YOUR TENANT ID"

        AimstarInAppMessaging.apiKey = apiKey
        AimstarInAppMessaging.tenantId = tenantId
        AimstarInAppMessaging.listener = this
    }
}
```

### 3. Customer ID の設定

アプリでユーザーが識別可能になったタイミングで `customerId` を設定します。

```kotlin
// 例: ログイン完了後に実行
AimstarInAppMessaging.customerId = "ユーザーを識別する ID"

// ログアウト時は null にリセット
AimstarInAppMessaging.customerId = null
```

### 4. ページ閲覧イベントの送出

該当画面で `trackPageView` を呼び出し、対応する `screenName` を渡します。条件に合致した場合はポップアップが表示されます。

```kotlin
AimstarInAppMessaging.trackPageView(activity = this, screenName = "Your Screen Name")
```

---

# SDK References

## AimstarInAppMessaging

```kotlin
object AimstarInAppMessaging
```

SDK のエントリーポイントです。`apiKey` と `tenantId` の設定が完了するまでは、SDK の機能は利用できません。

### Properties

#### apiKey: String (必須)

```kotlin
var apiKey: String
```

SDK 利用のための API キーを設定します。アプリ起動直後に設定してください。

#### tenantId: String (必須)

```kotlin
var tenantId: String
```

SDK 利用のためのテナント ID を設定します。

#### customerId: String?

```kotlin
var customerId: String?
```

ユーザーを識別する ID を設定します。`null` の場合はログアウト状態として扱われます。

### Functions

#### trackPageView(activity: Activity, screenName: String)

```kotlin
fun trackPageView(activity: Activity, screenName: String)
```

任意のタイミングで呼び出し、指定した `screenName` に紐づくメッセージを取得・表示します。ポップアップは引数で渡した `activity` 上に描画されます。
