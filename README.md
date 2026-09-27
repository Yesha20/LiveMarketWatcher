# Live Market Watcher

A small Android app that displays live cryptocurrency prices using Binance WebSocket streams.

Built as an Android technical assessment with Kotlin and Jetpack Compose.

## What it does

The app connects to Binance's WebSocket API and displays live ticker updates for:

- Bitcoin (BTCUSDT)
- Ethereum (ETHUSDT)
- BNB (BNBUSDT)
- Solana (SOLUSDT)
- XRP (XRPUSDT)

Each row updates independently. A price increase flashes green and a decrease flashes red.

The connection status is also displayed in the UI, and the WebSocket automatically reconnects after a connection failure.

## Tech Stack

- Kotlin
- Jetpack Compose
- Kotlin Coroutines
- Kotlin Flow / StateFlow
- OkHttp WebSocket
- Android Lifecycle
- JUnit
- MVVM
- Clean Architecture principles

## Architecture

The project is split into three main layers:

```text
Presentation
    |
    v
Domain
    |
    v
Data
```

### Data

Responsible for communicating with Binance.

```text
MarketWebSocketDataSource
        |
        v
MarketRepositoryImpl
```

`MarketWebSocketDataSource` uses OkHttp's WebSocket API and exposes the incoming updates as a `Flow` using `callbackFlow`.

### Domain

Contains the models, repository contract and price direction logic.

```text
PriceUpdate
PriceUiModel
PriceDirection
ConnectionState
MarketRepository
DeterminePriceDirectionUseCase
```

The `DeterminePriceDirectionUseCase` compares the previous and current price for each symbol and returns:

```text
UP
DOWN
NONE
```

### Presentation

The ViewModel collects market updates and converts them into UI state.

```text
WatchlistViewModel
        |
        v
WatchlistScreen
        |
        v
PriceRow
```

The UI collects state using `collectAsStateWithLifecycle()`.

## WebSocket

The Binance connection is created in:

```text
data/remote/MarketWebSocketDataSource.kt
```

The app subscribes to the ticker streams:

```text
btcusdt@ticker
ethusdt@ticker
bnbusdt@ticker
solusdt@ticker
xrpusdt@ticker
```

Incoming WebSocket messages are parsed and converted into `PriceUpdate` objects before being exposed through the repository.

## Reconnection

If the WebSocket connection fails, the app retries using a bounded backoff:

```text
2s → 4s → 8s → 15s
```

The retry counter is reset after a successful connection.

When the Flow collection is cancelled, the WebSocket is closed to avoid keeping the connection alive unnecessarily.

## UI

The watchlist uses a `LazyColumn` with stable keys based on the symbol:

```kotlin
items(
    items = prices,
    key = { it.symbol }
)
```

prices can update frequently and the list should not unnecessarily recreate its rows, hence keybased LazyColumn is used.

Price movement is represented using a short color animation:

- Green → price increased
- Red → price decreased
- No color → first update / no movement

## Testing

The main business logic and ViewModel behavior are covered with unit tests.

Tests cover:

- First price update
- Price increase
- Price decrease
- Same price
- Independent symbol tracking

UI tests were not included as part of the final implementation.

## Dependency Injection

The project uses constructor-based manual dependency injection.

For example:

```kotlin
class MarketRepositoryImpl(
    private val dataSource: MarketWebSocketDataSource
) : MarketRepository
```

and:

```kotlin
class WatchlistViewModel(
    private val repository: MarketRepository,
    private val determinePriceDirection: DeterminePriceDirectionUseCase
) : ViewModel()
```

A `ViewModelFactory` is used to create the ViewModel and provide its dependencies.

For this small dependency graph, manual DI keeps the project simple without adding a DI framework.

## Running the app

1. Clone the repository.
2. Open the project in Android Studio.
3. Let Gradle sync.
4. Run the app on an emulator or physical device.
5. Make sure the device has internet access.

No Binance API key is required because the application uses the public ticker WebSocket stream.

## Notes

The main focus of the implementation was handling a continuous stream of updates efficiently rather than building a complex trading UI.

Some areas that could be extended in a production application:

- User-configurable watchlists
- Search and add/remove symbols
- Historical price charts
- Persistence
- More robust WebSocket retry handling
- Hilt or another DI framework as the dependency graph grows
- More extensive integration/UI testing

## Author

Yesha Shah
