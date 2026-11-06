# Simply Light: Reforged RGB — Alpha27 candidate

Current Alpha27 review: actual lamp-item names corrected in EN/PT; thin lamps/posts fit better in first-person/ground views. The GIFs below are real Alpha27 captures, manually toggling surface RGB off/on in the auxiliary FTB pack; they do not demonstrate automatic colour cycling.

[Alpha27 review status](docs/review27/STATUS.md) · [Standalone heap/GC run](docs/review27/PERFORMANCE-STANDALONE.md) · [Current FTB/Sodium/Iris measurements](docs/review27/PERFORMANCE-PACK.md). Performance remains under investigation: pack spikes include286ms render interval with RGB off and298ms tick with RGB on; no zero-lag or FPS-gain claim. Alpha27 binary is not published.

Revisão atual Alpha27: nomes reais dos itens corrigidos em inglês/português e peças finas/postes mais visíveis na mão/no chão. GIFs reais desta versão alternam RGB desligado/ligado no FTB auxiliar, sem ciclo automático de cores. Testes do pack registraram picos de286ms em quadro com RGB desligado e298ms em tick com RGB ligado; a causa segue em investigação. Binário Alpha27 ainda não publicado.

**Version status:** Alpha26 was submitted to CurseForge and is under review. This branch contains the reviewed source; main and the existing GitHub binary release are older. English first / Português abaixo.

![RGB off/on without shaders / RGB desligado/ligado sem shaders](docs/images/review27/native-rgb-sodium27.gif)

![RGB off/on with Complementary / RGB desligado/ligado com Complementary](docs/images/review27/rgb-iris-complementary27.gif)

Real Alpha26 in-game captures; same lower crop removes pack HUD. / Capturas reais Alpha26; mesmo recorte inferior para retirar o HUD.

Independent, unofficial architectural lighting inspired by Simply Light by Flanks255. 432 light blocks, one Finishing Wrench, 16 colours, 1,081 recipes. Minecraft 26.1.2, NeoForge 26.1.2.126, Java 25. Own code, models and textures. All Rights Reserved; Gradle retains its bundled licence. No original Simply Light code/assets are bundled.

Install the same lumaworks jar on client and server. No lighting addon, JEI, Sodium or shader is required. Experimental surface RGB is enabled for new installations; existing preferences remain unchanged. Open Mods → Simply Light: Reforged RGB → Config to change the client preference without commands. /lumargb on/off/toggle/status/guide and an optional unbound shortcut are also available. RGB affects nearby block surfaces; entities, fluids, partial occlusion and shader-dependent appearance have limitations. Fast continuous changes can temporarily pause the colour layer. No zero-lag guarantee.

Normal lamps light with redstone; inverted lamps turn off. Craft a controlled lamp alone to switch mode. Small families have controlled variants. Sneak and empty-hand right-click opens strip direction controls. Wall strips use manually selected edges. Wrench changes compatible frames in-world or crafting/Crafter and returns without wear. Dye recolours matching lights. Lamps can be waterlogged where supported; three-block posts dismantle to one item. Lights can be worn; posts retain their melee function. Existing lumaworks IDs are preserved; simplylight worlds are not automatically converted. Create assembly integration is not verified on this target version.

Build: Python 3.13, Pillow 13.0.0 and Java 25. Install the asset dependency with python -m pip install Pillow==13.0.0. Run `gradlew.bat build` (Windows) or `./gradlew build`; assets and recipe checks run automatically. Sodium/JEI APIs are compile-only, never bundled. Development smoke tests are optional and excluded from ordinary builds. [Current review status and measurement limits](docs/review27/STATUS.md). This candidate is still under full pack/release review; do not interpret inclusion of reports as complete validation.

## Português brasileiro

Recriação independente e não oficial inspirada no Simply Light de Flanks255. 432 luminárias, uma Chave de Acabamento, 16 cores e 1.081 receitas. Minecraft 26.1.2/NeoForge 26.1.2.126/Java 25. Código, modelos e texturas próprios, sem materiais do original. Licença All Rights Reserved.

Instale o mesmo jar lumaworks no cliente/servidor. RGB experimental de superfícies ligado em instalações novas, respeitando preferências existentes. Mods → Simply Light: Reforged RGB → Configurar permite mudar a preferência local sem comando. Também há /lumargb on/off/toggle/status/guide e atalho opcional. Não exige JEI/Sodium/shaders. Entidades, fluidos, oclusão parcial e shaders têm limitações; mudanças contínuas podem pausar temporariamente a cor. Não promete zero lag.

Luzes normais acendem com redstone; invertidas apagam. Uma peça controlável sozinha na bancada alterna o modo. Famílias pequenas têm variantes controláveis. Agachar e clicar com mão vazia abre o editor das fitas; bordas de parede são manuais. Chave muda molduras no mundo/bancada/Crafter, retorna sem desgaste. Recoloração usa corante. Água onde suportada, postes de três blocos entregam um item, luzes podem ir à cabeça e postes mantêm função de arma. Preserva IDs lumaworks; não converte mundos simplylight. Integração com estruturas móveis Create não foi comprovada nesta versão.

Compilar: Python 3.13, Pillow 13.0.0 e Java 25; instale python -m pip install Pillow==13.0.0, gere/verifique recursos e execute gradlew.bat build. Evidências atuais em docs/review27; candidata ainda em revisão do pack/publicação.

Original: https://www.curseforge.com/minecraft/mc-mods/simply-light
Project: https://github.com/Tech-teste/refeito-simply-light
Credit: ChatGPTinho (user-selected nickname), developed with assistance from OpenAI Codex/ChatGPT; no OpenAI/Flanks255/FTB/Mojang affiliation.


[Historical Alpha26 remote-source build proof / Compilação remota histórica Alpha26](docs/review26/remote-build-check.json).

[Historical Alpha26 standalone measurements / Medições históricas sem pack](docs/review26/PERFORMANCE.md) · [Historical Alpha26 FTB measurements / Medições históricas no FTB](docs/review26/PERFORMANCE-PACK.md).

[Historical Alpha26 review / Revisão histórica Alpha26](docs/review26/REVIEW-STATUS.md).
