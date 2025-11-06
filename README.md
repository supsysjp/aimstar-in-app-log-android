# Aimstar In App Log Android SDK

## Requirements

- ランタイム: Android 6.0 (API 23) 以上
- 開発環境: Android Studio Iguana 以上、JDK 17

## 制限事項

- マルチウインドウモードの同時表示には対応していません

## SDKで提供する機能について

- ページ閲覧イベントをはじめとした、ユーザーのアプリ操作イベントを Aimstar に送信する
- ログイン・ログアウト状態を管理し、ユーザー単位でログを紐づける
- バッチ送信間隔や一度に送信するログの件数上限など、ログ送信の挙動を設定する

## 用語

| 用語 | 説明 |
| --- | --- |
| API Key | Aimstar In App Log を利用するために必要な API キーで、Aimstar 側で事前にアプリ開発者に発行されます。 |
| Tenant ID | Aimstar In App Log を利用するために必要なテナント ID で、Aimstar 側で事前にアプリ開発者に発行されます。 |
| Batch Interval | Aimstar In App Log では、アプリ開発者がログを送信する「間隔」（秒単位）を指定できます。 |
| Max Log Count |  Aimstar In App Log では、アプリ開発者が一度に送信するログの「件数上限」を指定できます。 |
| Customer ID | アプリ開発者がユーザーを識別する ID で、アプリ開発者が独自に発行、生成、または利用します。 |
| Session ID |  Aimstar In App Log 側で、アプリ起動ごとに新規発行するセッション識別子です。 |
| Installation ID | Aimstar In App Log 側で、アプリインストールごとに一意となる識別子です。 |

## 導入手順

### 1. SDK をアプリに追加する

1. Aimstar が提供する `AimstarInAppLogSdk.aar` をダウンロードし、`app/libs` ディレクトリに配置します
2. モジュールの `build.gradle.kts`（または `build.gradle`）に以下を追記します

```kotlin
repositories {
    flatDir { dirs("libs") }
}

dependencies {
    implementation(files("libs/AimstarInAppLogSdk.aar"))
}
```

3. ProGuard / R8 を利用している場合は、Aimstar 提供のルールファイルを併せて追加してください

### 2. SDKの初期化を行う

アプリ起動時に APIキーとテナントID を設定します。`MainActivity` クラスでの初期化例を示します。

```kotlin
import androidx.activity.ComponentActivity
import jp.co.aimstar.logging.android.AimstarInAppLog
import jp.co.aimstar.logging.android.AimstarLogSDKConfig

class MainActivity : ComponentActivity() {
    private val apiKey = "YOUR API KEY"
    private val tenantId = "YOUR TENANT ID"

    override fun onCreate() {
        super.onCreate()

        val config = AimstarLogSDKConfig(apiKey = apiKey, tenantId = tenantId)
        AimstarInAppLog.setup(context = this.applicationContext, config = config)
    }
}
```

### 3. Batch Interval と Max Log Countの設定

setup時に必要に応じてログの送信に関する設定をしてください。

```kotlin
val config = AimstarLogSDKConfig(apiKey = apiKey, tenantId = tenantId)

// ログを送信する「間隔」（秒単位）を指定できます。
config.batchInterval = 20

// 一度に送信するログの「件数上限」を指定できます。
config.maxLogCount = 50
```

### 4. Customer IDの設定

アプリでユーザーが識別可能になったタイミングで `customerId` を設定します。

```kotlin
// ログイン直後など、ユーザーが識別できる状態で実行
AimstarInAppLog.updateLoginState(customerId = "user_001")

// ログアウト時
AimstarInAppLog.updateLoginState(customerId = null)
```

### 5.ユーザー操作イベントの記録

スクリーン名やイベント名を指定してログを送出します。バッチ設定に従って Aimstar に送信されます。

```kotlin
AimstarInAppLog.trackPageView(
    pageUrl: "http//...",
    pageTitle: self.title,
    referrerUrl: "http//...",
    customParams: mapOf(
        "is_logged_in" to CustomValueType.BooleanValue(true),
        "membership_level" to CustomValueType.StringValue("gold")
    )
)
```

# SDK References

## AimstarLogSDKConfig

```kotlin
class AimstarLogSDKConfig
```

SDK 初期化時の設定を管理するクラスです。必須項目としてAPI Key・Tenant ID、オプション項目としてBatch Interval・Max Log Countを指定します。

### Properties

#### batchInterval: Int?

```kotlin
var batchInterval: Int?
```

Aimstar へログイベントをバッチ送信する「間隔」（秒）を設定します。

#### maxLogCount: Int?

```kotlin
var maxLogCount: Int?
```

Aimstar へ一度に送信するログイベントの「件数上限」を指定します。

## AimstarInAppLog

```kotlin
object AimstarInAppLog
```

SDK のエントリーポイントです。`setup` メソッドを通じて初期化を行います。初期化が完了するまでイベント送出は利用できません。

### Properties

#### sessionId: String?

```kotlin
val sessionId: String?
```

アプリ起動ごとに新規発行されるセッションIDを参照できます。

#### installationId: String?

```kotlin
val installationId: String?
```

アプリインストールごとに一意となるIDを参照できます。

### Functions

#### setup(config:)

```kotlin
fun setup(config: AimstarLogSDKConfig)
```

SDK の初期化を行います。

#### updateLoginState(customerId:)

```kotlin
fun updateLoginState(customerId: String?)
```

ユーザーのログイン状態を更新します。`null` を渡すとログアウト状態になります。

#### updateDeepLink(campaign:, content:, medium:, source:, term:)

```kotlin
fun updateDeepLink(
    campaign: String? = null,
    content: String? = null,
    medium: String? = null,
    source: String? = null,
    term: String? = null
)
```

Deep Link情報を更新します。

#### trackPageView(pageUrl:, pageTitle:, referrerUrl:, customParams:)

```kotlin
fun trackPageView(
    pageUrl: String?,
    pageTitle: String?,
    referrerUrl: String?,
    customParams: Map<String, CustomValueType>? = null
)
```

ページ閲覧イベントを Aimstar に送信します。

#### trackProductInfo(brand:, productCategory:, productId:, productName:, productPrice:, skuId:, customParams:)

```kotlin
fun trackProductInfo(
    brand: String?,
    productCategory: String?,
    productId: String?,
    productName: String?,
    productPrice: Double?,
    skuId: String?,
    customParams: Map<String, CustomValueType>? = null
)
```

商品情報閲覧イベントを Aimstar に送信します。

#### trackClickButton(action:, buttonId:, buttonName:, buttonText:, customParams:)

```kotlin
fun trackClickButton(
    action: String? = null,
    buttonId: String? = null,
    buttonName: String? = null,
    buttonText: String? = null,
    customParams: Map<String, CustomValueType>? = null
)
```

ボタンクリックイベントを Aimstar に送信します。

```kotlin
fun trackSearch(
    pageNumber: Int? = null,
    requestUrl: String? = null,
    resultsCount: Int? = null,
    searchQuery: String? = null,
    searchType: String? = null,
    sortKey: String? = null,
    sortOrder: String? = null,
    statusCode: Int? = null,
    customParams: Map<String, CustomValueType>? = null
)
```

検索行動イベントを Aimstar に送信します。

#### trackCartProduct(amount:, cartId:, productId:, productName:, skuId:, customParams:)

```kotlin
fun trackCartProduct(
    amount: Int? = null,
    cartId: String? = null,
    productId: String? = null,
    productName: String? = null,
    skuId: String? = null,
    customParams: Map<String, CustomValueType>? = null
)
```

カート操作イベントを Aimstar に送信します。

#### trackFavoriteProduct(isUnfavorite:, productId:, productName:, skuId:, customParams:)

```kotlin
fun trackFavoriteProduct(
    isUnfavorite: Boolean? = null,
    productId: String? = null,
    productName: String? = null,
    skuId: String? = null,
    customParams: Map<String, CustomValueType>? = null
)
```

お気に入り操作イベントを Aimstar に送信します。

#### trackPurchase(cartId:, itemCount:, orderId:, paymentMethod:, shippingAmount:, taxAmount:, totalAmount:, customParams:)

```kotlin
fun trackPurchase(
    cartId: String? = null,
    itemCount: Int? = null,
    orderId: String? = null,
    paymentMethod: String? = null,
    shippingAmount: Double? = null,
    taxAmount: Double? = null,
    totalAmount: Double? = null,
    customParams: Map<String, CustomValueType>? = null
)
```

購入完了イベントを Aimstar に送信します。

#### trackPushLog(notificationId:, notificationAction:, customParams:)

```kotlin
fun trackPushLog(
    notificationId: String? = null,
    notificationAction: String? = null,
    customParams: Map<String, CustomValueType>? = null
)
```

プッシュ通知イベントを Aimstar に送信します。

#### trackRequestApi(latencyMs:, errorMessage:, queryParams:, requestMethod:, requestOrigin:, requestSize:, requestUrl:, statusCode:, customParams:)

```kotlin
fun trackRequestApi(
    latencyMs: Int? = null,
    errorMessage: String? = null,
    queryParams: String? = null,
    requestMethod: String? = null,
    requestOrigin: String? = null,
    requestSize: Int? = null,
    requestUrl: String? = null,
    statusCode: Int? = null,
    customParams: Map<String, CustomValueType>? = null
)
```

外部API呼び出しイベントを Aimstar に送信します。

## Exampleプロジェクト

リポジトリ内の `Example` にサンプルアプリを同梱しています。初期化やイベント送出の動作確認にご活用ください。
