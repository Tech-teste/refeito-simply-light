# Alpha27 — candidate review

Not published. Full goal remains active.

## Fixed and verified
- Actual ItemStack lamp names now use the block description prefix. All432lamp names match their translated block names in EN and PT; 1368runtime checks per language. Tool names also verified.
- First-person/ground presentation of thin lamps and posts:28white-family/tool samples captured and visually reviewed. Floor strips use their centered four-edge item geometry. Wall-strip position/front angle refined separately for3modes.
- Normal clean source build is byte-identical to tested final jar. No test helper bundled.
- Block models/blockstates, recipes, tags, loot and RGB runtime byte-identical to26. Item model changes add no ticking entities or block entities.

## Evidence limits
The first28captures were inconclusive due insufficient wait. A subsequent4-second probe showed actual held and dropped models. That exposed low/out-of-view thin-item presentation and the actual name defect.
The initial item-name test rejected only block.lumaworks keys, allowing item.lumaworks keys through. Alpha26 approval for actual item names is revoked; its translation-entry/tooltip checks remain separate evidence.
Current first-person review uses default FOV, right hand, daylight, white representative families. Every colour name is runtime checked, but every colour/hand/FOV/night/shader item presentation is not exhaustively approved.
Performance reports remain26evidence. No27FPS/TPS improvement or zero-lag claim.
Full requirement audit, pack-specific extended RGB queue/heap run, GUI scale checks, original-equivalence consolidation, final gallery cleanup and hosted publication/repository promotion remain open.

## Isolation
All auxiliary client processes used in these checks closed normally. Options restored except master volume stays muted; client configuration restored byte-for-byte. No global mouse/keyboard or main instance used.

## Bounded longer runtime observation completed
16×30s =480.03s actual standalone measurements after60s warmup. Dense4096lamp scene, on/off pairs,4redstone runs and dense recovery.24,000emission checks across660transitions passed. Field ready/error empty/pending0 at each settling checkpoint; one rapid-update pause recovered. Heap/GC measured with JVM MXBeans;1024–1052MiB committed snapshots. One78.5133ms tick retained; no zero-lag claim. Offscreen callback intervals are not monitor FPS. See longrun/PERFORMANCE.md and summary.json. Scope is bounded observation, not proof of no leak. Shader/pack performance evidence remains26.

## Current pack observation
FTB Evolution2 with Sodium and Iris/Complementary:16x30s measured,24,000emission checks passed and settling queues0/error empty/no shader fallback. Performance remains PARTIAL: observed286ms render interval (RGB off),298ms tick (RGB on), and shader tick spikes in both states. Causal profiling remains open. See pack-longrun/PERFORMANCE.md and summary.json. Auxiliary closed and options/RGB/Iris files restored.
