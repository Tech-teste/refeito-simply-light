> Later correction: actual Alpha26 lamp-item names were NOT approved by a sufficient check. A stronger comparison found item.lumaworks.white_signal. Alpha27 fixes the block description prefix and passes actual432item names in EN/PT. See [countercheck](../review27/name-countercheck26.json) and [current review](../review27/STATUS.md). The older locale-entry and tooltip checks remain separately scoped evidence.

# Alpha26 review status / Situação da revisão

The whole review remains open. Results below have limited scopes. / A revisão geral continua aberta; cada resultado tem escopo limitado.

| Area / Área | Result / Resultado | Evidence and scope / Evidência e escopo |
|---|---|---|
| Runtime source / Fontes de execução | PASS / APROVADO | Fresh GitHub clone, Windows build, byte-identical jar. remote-build-check.json. Hosted Linux CI is separate. |
| Recipes and items / Receitas e itens | PASS / APROVADO | 432 light blocks,433 items,1,081 recipes; actual standalone item placements and crafts. |
| English and Portuguese / Inglês e português | PASS / APROVADO for tested screens | Native RGB widget both directions, names/tooltips and guide in tested standalone setup. Full-pack language reload exceeded the tool timeout and was not approved. |
| RGB controls / Controle RGB | PASS / APROVADO | Actual GUI save and NativeRgb reaction, commands and optional key; GUI-specific full-process restart persistence passed; see gui-restart-persistence.json. |
| Frame/tick measurements / Medições | PARTIAL / PARCIAL | Four dense standalone and eight furnished FTB windows; see PERFORMANCE*.md. Spikes remain. No exhaustive or long-duration guarantee. |
| Redstone / Redstone | PASS / APROVADO for sampled Alpha26 cases | Ten small normal/inverted lamps in both signal states, Sodium and Complementary. Earlier exhaustive checks belong to their stated versions. No Alpha26 rapid-clock performance claim. |
| Save/reload / Save e recarga | Earlier-version evidence / Evidência anterior | Alpha25 full-process432-state sample and reload/crafts; gameplay/state/RGB code preserved, but not a new exhaustive26 run. |
| Dedicated runtime / Servidor dedicado | PASS / APROVADO | Alpha26 development server on loopback:195,851 runtime assertions. dedicated-result.json and smoke-result.txt; normal shutdown, release rebuilt free of helpers. Not a network client connection. |
| Dedicated network / Conexão dedicada | PASS locally / APROVADO localmente | Separate client and dedicated server on127.0.0.1:25565: block sync, both redstone states and wrench round trip. See network-result.json. External multiplayer, cross-machine latency and networked editor were not tested. |
| Gallery/design / Galeria e design | PARTIAL / PARCIAL | Current26 materials and real GIFs reviewed; all-family closeups and final gallery replacement still pending. |
| CurseForge / CurseForge | SUBMITTED / ENVIADO | Alpha26file9468561 under review at last observation; previous16 retained. Moderation/public download not guaranteed. |
| GitHub presentation / Apresentação | IN PROGRESS / EM ANDAMENTO | Review branch contains current runtime; main/release promotion remains pending. |

No original Simply Light code/assets or AI-generated promotional images are included. Real GIFs use identical documented cropping of the pack HUD. / Sem código/recursos do Simply Light original ou imagens promocionais geradas por IA; GIFs reais com recorte igual e documentado do HUD.
