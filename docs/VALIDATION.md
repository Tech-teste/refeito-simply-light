# Validation / Validação

Candidate: **0.1.0-alpha.16**, Minecraft 26.1.2, NeoForge 26.1.2.126, Java 25.

## English

- Standalone client, only lumaworks/minecraft/neoforge: **224 real item placements, all 520 recipes assembled and 1,938 checks passed**. English and Brazilian Portuguese: 270 entries each, 724 client checks per language. Recipe photographs used real crafting-result clicks; the finishing tool returned to the grid.
- Dedicated development server: **34,633 assertions passed** for shapes, water, redstone, posts, strip directions, access validation and reusable crafting/Crafter behavior.
- Resource audit: **5,578 checks passed**, all 160 reference IDs mapped, all 225 items reachable from vanilla materials, localization parity.
- Full-pack candidate: client/JEI 464 checks, all 225 items and 11 information pages; server/recipes 541 checks; finishing recipe, owner/intruder claim permissions and 37 post-dismantling checks passed. Test claims/chunks were cleaned up.
- **642.198 seconds** of full-pack use completed without an in-game crash: redstone rooms for 180 seconds, 256 sparse lights for 120 seconds, 4,096 stress lights for 120 seconds and rooms with original Complementary for 180 seconds, with movement between areas. Sodium 0.9.2, Iris 1.11.4, ComplementaryUnbound r5.9.3. Four actual 30-second frame/tick measurement windows were taken during those phases; the entire 642 seconds was not a continuous frame trace.
- Client RGB starts off. Actual configuration on/off was checked; standalone file reload worked, whereas the full-pack test used the registered NeoForge configuration value. **Restart after manually editing the file.** Optional Sodium classes do not prevent standalone startup when Sodium is absent.
- 2,088 generated resources reproduced byte-for-byte, plus three static resources. The clean public source build on the same Windows machine produced the exact installed candidate jar SHA-256: `7e06c7676ce09c19237867477ce280272c59d9889950a4172789f2f35efa2cce`. This is not a cross-platform byte-reproducibility guarantee.

### Full-pack measurement windows

Offscreen 1280×720 measurements, without desktop input; other applications/another client shared the machine. FPS outside the screen is not a promise of visible-game FPS. Times below are milliseconds; lower is better. TPS close to 20 indicates the integrated server kept up in these windows.

| Scene / shader | TPS | Tick p95 ms | Frame p99 ms | Largest frame ms |
|---|---:|---:|---:|---:|
| rooms / off | 20.00 | 12.55 | 51.91 | 436.73 |
| sparse / off | 19.99 | 9.51 | 165.35 | 350.55 |
| stress / off | 19.99 | 9.04 | 38.19 | 88.92 |
| rooms / original | 20.02 | 9.97 | 45.35 | 199.82 |

**Frame-time spikes remain.** These measurements do not isolate every cause or prove zero overhead. An earlier prototype JFR recording is not presented as a profile of this candidate.

### Known startup and RGB limits

One first full-pack startup failed during registry bootstrap with `Some intrusive holders were not registered: []`. An unchanged retry loaded and passed all subsequent audits and the soak. **The cause has not been isolated.** No specific mod is blamed, and no unproven fix is claimed. This is one reason the release is Alpha.

RGB affects nearby block surfaces in a fixed 48×32×48 region. Entities, fluids, partial occlusion, fast movement and rapid continuous changes have limits. Shader results depend on vertex-color handling; compatibility with every shader is not claimed. There is no per-light block entity or ticking task. Normal Minecraft light and gameplay work with RGB disabled.

No world files, launcher profiles, account data, private raw logs or local control agents are included in the public source or distribution.

## Português brasileiro

A candidata passou na colocação real dos 224 blocos, nas 520 receitas, nos dois idiomas, no servidor dedicado, no JEI opcional e nas permissões do FTB. A chave voltou ao craft, inclusive no Crafter. Os recursos foram comparados byte a byte; a compilação das fontes públicas na mesma máquina gerou o mesmo jar testado.

O teste prolongado no pack durou **10 minutos e 42 segundos**, com redstone, 256/4.096 luzes, movimento e Complementary. Quatro janelas de 30 segundos mediram quadros/ticks. Os TPS ficaram próximos de 20, mas **ainda houve engasgos gráficos**. As medições foram fora da tela, em 1280×720, com outros aplicativos compartilhando o computador; não prometem FPS universal nem zero lag.

Uma abertura inicial do pack falhou no registro dos mods. Repetir sem mudar o jar funcionou e passou nas verificações seguintes. **A causa não foi isolada** e não foi atribuída a outro mod sem prova. A versão permanece Alpha.

RGB desligado por padrão. Reinicie ao editar o arquivo manualmente. Entidades, fluidos, oclusão parcial, movimento e mudanças rápidas ainda têm limites. A iluminação normal continua independente do RGB. Nenhum mundo, perfil de conta ou gravação privada acompanha as fontes.
