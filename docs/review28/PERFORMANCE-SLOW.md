# Alpha 27 — RGB active during slow redstone, Sodium and Complementary

Eight 30-second windows after the actual SCC cache-loaded message and 60seconds additional warmup, then 30seconds per rendering mode. RGB OFF/ON/ON/OFF in each mode. Each mode: Sodium without shaders, then Iris/Complementary. Ordinary Alpha 27 binary unchanged. JFR profile running/checkpointed throughout. Auxiliary only, muted, no desktop input.

48,000 emission checks and 240 redstone transitions passed. At 20ticks between transitions, all four RGB-ON windows completed 29bakes each:116total, zero new protective pauses. All settling queues reached zero, RGB error empty, shader fallback false. This closes the specific gap from the rapidclock run, which intentionally invoked protection.

| Rendering | RGB | Frame p 99 range (ms) | Max frame (ms) | Max tick (ms) |
|---|---|---:|---:|---:|
| Sodium, no shaders | OFF | 2.80–3.06 | 48.79 | 9.35 |
| Sodium, no shaders | ON | 2.80–2.99 | 54.34 | 48.09 |
| Complementary | OFF | 4.39–4.98 | 49.84 | 298.42 |
| Complementary | ON | 4.98–6.29 | 62.74 | 44.37 |

## Captured stalls

- Tick 298.4182ms, RGB OFF with Complementary:11of 12Server-thread samples include world-save paths, notably FTB Quests chapter/quest JSON serialization and writes under onWorldSaved/saveNow. No sampled own production method in that span. This identifies a contributor to this event, not every prior 298ms event or proof of zero RGB cost.
- Frame 54.3435ms, RGB ON without shaders: overlaps 45.7505msGCpause.
- Frames 52.4672ms and 51.8615ms, RGB ON with Complementary: overlap 41.9070/41.4517msGCpauses.
- Frame 62.7405ms, RGB ON with Complementary: no samples and no overlappingGC; unresolved. Do not assign a cause from absence of samples.

RGB reported work was approximately 3.96–4.38ms per elapsed second in ON windows. That counter covers its cooperative client work, not all mesh rebuild/GPU/allocation cost, so it is not total RGB overhead. Frame distributions are reported as observations; two windows per combination and offscreen rendering cannot establish a universal FPS loss. Memory pauses occurred but this trace alone does not attribute the allocating component.

No production optimization was applied without identifying a production bottleneck. No autosave, pack mod, safety setting or shader feature was disabled. Earlier spikes remain in reports. A wider RGB density/movement comparison with shaders remains separate scope. Existing dense 4096runs are not replaced by this 10-lamp redstone test.

Auxiliary closed normally; options restored except master mute, RGB config and Iris properties byte-identical. Raw JFR remains private. First attempt aborted before measurement because cache completion was unsupported; corrected repeat uses real cache completion, not fabricated markers. See completed-index-evidence.txt, protocol sources, windows.json, jfr-summary.json and restoration.json.

## Português brasileiro

Repetição com mudança de redstone a cada segundo:48mil verificações/240transições passaram. O RGB concluiu 116recálculos sem pausar a proteção, com e sem Complementary. Filas zeradas, sem erro RGB ou fallback do shader.

O tick de 298ms apareceu com RGB desligado e 11das 12amostras apontam salvamento do mundo, incluindo serialização do FTB Quests. Três quadros longos sobrepuseram pausas de memória; um quadro de 62,74ms continua sem causa identificada. Não há promessa de zero lag nem medição de FPS visível na tela. Nenhum mod do pack ou salvamento foi desativado para melhorar o resultado.
