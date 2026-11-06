# Alpha27 bounded RGB / GC / heap observation

Standalone vanilla renderer; Minecraft26.1.2 / NeoForge26.1.2.126 / Java25. i5-14600K, RTX5060. Shared user PC. Offscreen1280×720, throttleNONE throughout every measured window. No concurrent compilation.60s warmup,16×30s measured windows (480.03s), with setup/settling intervals excluded. No desktop input.

Eight dense windows with4096verified lamps, four windows on five small lamp families in normal/inverted pairs (period20ticks or2ticks), then four dense recovery windows. RGB off/on pairs retained.

| Window | Scene / RGB | Callback p99 ms | Callback max ms | Tick max ms | TPS | End heap MiB |
|---|---|---:|---:|---:|---:|---:|
| 1 | dense / off | 0.9713 | 8.2852 | 2.7010 | 19.9984 | 759.2 |
| 2 | dense / on | 0.9189 | 42.8112 | 3.1281 | 19.9988 | 460.2 |
| 3 | dense / on | 0.9497 | 10.3837 | 2.9314 | 19.9986 | 564.3 |
| 4 | dense / off | 0.9764 | 11.4051 | 1.5179 | 19.9988 | 936.6 |
| 5 | dense / off | 0.9045 | 12.3777 | 1.5511 | 19.9977 | 580.3 |
| 6 | dense / on | 0.9206 | 8.4531 | 1.4271 | 19.9990 | 891.0 |
| 7 | dense / on | 0.9271 | 47.6146 | 78.5133 | 19.9987 | 519.1 |
| 8 | dense / off | 0.9105 | 42.8893 | 1.6925 | 19.9986 | 939.5 |
| 9 | clock / on | 0.9133 | 6.2875 | 2.8456 | 19.9990 | 753.7 |
| 10 | clock / on | 0.9512 | 3.5457 | 1.6321 | 19.9990 | 821.3 |
| 11 | clock / off | 0.9844 | 32.5340 | 2.7974 | 19.9983 | 438.1 |
| 12 | clock / on | 0.9012 | 21.5979 | 2.1804 | 19.9990 | 637.7 |
| 13 | recovery_dense / off | 0.9290 | 36.6979 | 4.7405 | 19.9981 | 634.2 |
| 14 | recovery_dense / on | 0.9064 | 41.5096 | 2.4508 | 19.9987 | 761.4 |
| 15 | recovery_dense / on | 0.9131 | 32.9052 | 31.9596 | 19.9986 | 719.0 |
| 16 | recovery_dense / off | 0.9123 | 33.0572 | 1.6307 | 19.9986 | 905.6 |

The render callback intervals are not visible monitor FPS. TPS stayed19.9977–19.9990. One tick exceeded50ms:78.5133ms in dense RGB-on window7. Zero observed render intervals exceeded50ms (worst47.6146ms). Do not claim zero lag or an FPS gain.

24,000emission checks across660real redstone neighbor transitions passed. A rapid-update colour-layer pause occurred once; after each window the field settled, pending sections reached0 and error remained empty. Off mode was also measured.

End-window used heap ranged438.1–939.5MiB. Committed heap ranged1024–1052MiB at recorded start/end snapshots (max allowed6144MiB). GC:180young collections/322ms reported collection time;140concurrent collections/234ms;0old collections. These are MXBean counters, not STW pause measurements. No forced GC or retained-heap analysis. This bounded observation does not prove absence of a slow leak.

## Português brasileiro

Foram16janelas de30s, totalizando8min de medição. RGB ligado/desligado,4096luminárias estáticas, alternância lenta/rápida de redstone e recuperação.24mil conferências de emissão passaram; as filas zeraram ao estabilizar. Um tick de78,5ms foi registrado e não foi atribuído automaticamente ao RGB. A memória foi coletada por contadores reais da JVM; a observação limitada não prova ausência de vazamentos. As taxas da captura fora da tela não representam FPS visível no monitor.

Sodium/Iris/Complementary pack measurements remain explicitly Alpha26 evidence; no current Alpha27 shader or pack performance claim is added.

Evidence: windows.json, summary.json, dense-scene.json, restoration.json.
