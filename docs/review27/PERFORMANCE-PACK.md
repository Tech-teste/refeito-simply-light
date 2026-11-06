# Alpha27 / FTB Evolution2 — Sodium and Iris/Complementary

Measured2026-11-06, isolated muted auxiliary; current jar18adcec123d3d067f6129a1abdb0720acae2337cbed2ecb69f9a0c3ddb5bdba5. Minecraft26.1.2 / NeoForge26.1.2.126 / Java25; i5-14600K / RTX5060, shared user PC. Offscreen1280×720; no visible-monitor FPS measurement. No concurrent builds or browser uploads during the timed windows.

60s world warmup plus30s per shader mode.16×30s =480.05s recorded:4workshop RGB-off/on/on/off windows,2fast-redstone normal/inverse windows, and2dense4096lamp windows per renderer mode. Chunk/scene setup and shader switches excluded from the timed windows. Background pack work/user activity not fully controlled.

| # | Shader | Scene | RGB | Callback p99 ms | Callback max ms | Tick max ms | TPS | End heap MiB |
|---|---|---|---|---:|---:|---:|---:|---:|
| 1 | Sodium only | workshop | off | 5.2077 | 286.2039 | 38.9418 | 19.9965 | 5084.9 |
| 2 | Sodium only | workshop | on | 2.8323 | 25.7016 | 21.9798 | 19.9984 | 3795.6 |
| 3 | Sodium only | workshop | on | 2.8614 | 31.4231 | 26.4553 | 19.9986 | 2927.1 |
| 4 | Sodium only | workshop | off | 2.7563 | 54.2219 | 21.7706 | 19.9988 | 2866.3 |
| 5 | Sodium only | clock | on | 3.7589 | 29.7189 | 298.0643 | 19.9984 | 3027.7 |
| 6 | Sodium only | clock | off | 2.8019 | 54.4640 | 15.0429 | 19.9985 | 4124.2 |
| 7 | Sodium only | dense | on | 2.3138 | 43.5656 | 12.9618 | 19.9977 | 2989.4 |
| 8 | Sodium only | dense | off | 2.3480 | 40.7170 | 16.9826 | 19.9977 | 4095.1 |
| 9 | Iris/Complementary | workshop | off | 4.5190 | 29.4342 | 24.0808 | 19.9989 | 3063.3 |
| 10 | Iris/Complementary | workshop | on | 4.3455 | 50.8251 | 25.2377 | 19.9974 | 5348.7 |
| 11 | Iris/Complementary | workshop | on | 5.6010 | 55.0593 | 137.9673 | 19.9983 | 4358.1 |
| 12 | Iris/Complementary | workshop | off | 5.2671 | 45.3955 | 109.7855 | 19.9981 | 3605.9 |
| 13 | Iris/Complementary | clock | on | 5.5964 | 48.8335 | 9.6778 | 19.9985 | 4024.3 |
| 14 | Iris/Complementary | clock | off | 5.6106 | 48.8146 | 7.9313 | 19.9980 | 4722.1 |
| 15 | Iris/Complementary | dense | on | 5.6575 | 53.7295 | 18.7852 | 19.9970 | 3544.4 |
| 16 | Iris/Complementary | dense | off | 6.4245 | 51.3841 | 20.6731 | 19.9979 | 3419.7 |

## Findings

Mechanics passed24,000emission checks over1,200redstone transitions. All post-window settling checkpoints had no pending colour sections, no RGB error, no rapid-update pause remaining and no shader fallback. Dense scenes verify4096actual lamps. The automatic colour-layer pause triggered during rapid changes and recovered afterwards.

**Performance remains PARTIAL:** Sodium window1 (RGB off) had286.2039ms maximum render interval and25total intervals over50ms across the8Sodium windows. The RGB-on clock window reached298.0643ms for a tick. Shader windows had6intervals over50ms; worst55.0593ms, with tick spikes in both RGB states (137.9673ms on,109.7855ms off). Causal attribution requires profiling; do not call the pack lag-free or claim a proven FPS improvement.

TPS windows19.9965–19.9989. The engine can recover its tick count after a long tick, so averageTPS does not erase an observed spike.

GC delta over Sodium windows:55young collections/839ms reported time,38concurrent/419ms,0old. Shader windows:34young/407ms,22concurrent/390ms,0old. Full-session gap work is excluded from these per-mode deltas. Collection times are MXBean counters, not measured STW pause histograms. Heap end snapshots2866–5349MiB, oscillating; no forced fullGC/retained-heap proof.

## Português brasileiro

As16janelas somam8min de medição. Passaram24mil conferências de emissão e as filas zeraram após estabilizar, inclusive depois de redstone rápida. Sodium e Iris/Complementary foram testados com RGB ligado/desligado, oficina e4096luminárias.

Ainda há engasgos a investigar: quadro de286ms com RGB desligado, tick de298ms com RGB ligado, e picos de tick com shader nos dois estados. Não atribuímos a causa automaticamente ao mod nem consideramos resolvido. Média de20TPS não elimina esses picos. As medições fora da tela não equivalem ao FPS do monitor.

Evidence: windows.json, summary.json, dense-off.json, dense-original.json, restoration.json and four current in-game screenshots.
