# Alpha28 review candidate — not a published binary

The full review remains open. This branch contains the candidate source; the existing GitHub binary release and main are older. CurseForge Alpha26 was submitted earlier; no Alpha28 binary has been uploaded.

Alpha28 widens the invisible strip-direction hitboxes so the Portuguese wall label “Esquerda” is fully clickable. The visual panel is unchanged. Only the editor class and version metadata differ from Alpha27; RGB runtime, placed models, registered IDs, recipes and tags are byte-identical. Clean-source and tested builds matched.

Verified in Alpha28: 12 editor layouts (EN/PT, scales 1/2/3, floor/wall), all 14 guide pages fitting the native book, actual names/tooltips for 432 lamps in both languages, 28 white-family/tool left-hand nighttime captures, 1,081 engine recipe results, 432 item placements, 432 nondefault states after restart/reload, and 416 water-filled states after restart/reload. The development dedicated server passed 195,851 assertions with release-identical production classes, including 306 real Crafter dispense conversions. Those conversions invoked the vanilla dispense method directly; they are not a timed hopper/redstone throughput benchmark.

Alpha27 profiling remains versioned. A warmed slow-redstone comparison with Sodium and Iris/Complementary completed 116 RGB bakes without protective pauses. Captured stalls include world-save/FTB Quests work and GC pauses; one 62.74 ms frame remains unresolved. Offscreen intervals are not visible-monitor FPS. No zero-lag claim.

Remaining review includes wider visual/JEI coverage, selected manual bucket/support scenarios, current network client/server verification, final presentation and publication consistency. Raw logs/JFR, helper classes and worlds are not published here.

## Português brasileiro

Alpha28 em revisão, sem binário publicado. Corrige a área clicável de “Esquerda” sem mudar a aparência. Mantém o RGB, blocos, receitas e IDs da Alpha27. O guia, os nomes dos itens, os layouts e novos testes funcionais foram conferidos na candidata atual. Os relatórios de desempenho continuam identificados como Alpha27 e não prometem ausência de lag.

As verificações do Crafter usam o método real de fabricação, mas não um pulso cronometrado/hopper. A persistência de água cobre 416 peças menores em compartimentos de vidro; os postes tiveram testes separados de quebra/restauração. A missão completa ainda tem pendências. Consulte os relatórios desta pasta para o escopo exato.
