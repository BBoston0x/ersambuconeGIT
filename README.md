# Er Sambucone Client

Un client Minecraft ottimizzato per prestazioni migliori.

## Ottimizzazioni Effettuate

### Pulizia del Repository
- Rimosse directory di cache e build non necessarie (`.gradle`, `build`, `.idea`)
- Rimossa directory `.zencoder` vuota
- Pulita la directory `run` da dati di test non necessari

### Ottimizzazioni del Codice
- Unificati i package `mixin` e `mixins` in un unico package
- Risolto il conflitto tra le classi `ConfigManager` rinominando quella in `utils` a `ModuleConfigManager`
- Migliorato il sistema di gestione delle configurazioni
- Aggiunto shutdown corretto del thread pool nel `ClientMain`

### Miglioramenti delle Prestazioni
- Ottimizzato `PerformanceMonitor` utilizzando `LongAdder` per una migliore concorrenza
- Aggiunti metodi per rilevare problemi di prestazioni e fornire raccomandazioni
- Implementati threshold per avvisi di prestazioni (FPS bassi, utilizzo elevato della memoria)

### Versione
- Aggiornata la versione del client a 1.0.1

## Utilizzo

1. Avvia il client Minecraft con il mod installato
2. Usa i keybind configurati per accedere alle funzionalità
3. Monitora le prestazioni tramite l'HUD dinamico

## Sviluppo

Per contribuire al progetto:
1. Clona il repository
2. Importa il progetto in un IDE compatibile con Gradle
3. Esegui `./gradlew build` per compilare

## Licenza

Questo progetto è distribuito sotto licenza MIT.