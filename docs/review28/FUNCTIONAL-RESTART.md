# Alpha 28 — standalone recipes, bucket paths and save/restart

Actual minimal client: Minecraft 26.1.2, NeoForge 26.1.2.126 and Simply Light: Reforged RGB Alpha 28 only. Private helpers attach only to the named auxiliary instance; no global desktop input. Production jar unchanged.

## Results

- PASS:1081recipes resolved via the actual server RecipeManager and assembled; item and count match each recipe definition. This is engine-level matching/assembly, not 1081manual crafting-screen clicks or Crafter pulses.
- PASS:432light blocks placed through ItemStack.useOn; expected blocks appeared. Removal checked three vertical positions for orphan blocks. It does not cover every orientation/drop scenario.
- PASS:416ArchitecturalLight variants filled using vanilla BucketItem.emptyContents and drained using the block pickup path. Each produced one water bucket and returned to the exact dry BlockState.16columns explicitly excluded. Contained fixture cleared. This is not a raycast/manual bucket-click test, all orientations, fluid spreading or persisted water test.
- PASS:432IDs/nondefault states written, forced disk save, auxiliary process closed, new process opened and all 432states compared exactly. Actual server reloadResources future completed, then all 432compared again. Artificial static fixture deliberately excludes waterlogged/powered properties from cycling; not a survival-placement proof. Manifest retained.
- Fixture cleanup executed and saved; original options restored except master mute; RGB config byte-identical. Both auxiliary processes closed normally.

## Remaining scope

Dedicated network/external clients, actual Crafter pulses on 28, waterlogged-state restart, support removal and full orientation/drop matrix remain outside this run. Older evidence is retained with its own version and does not silently become a new 28test. No FPS/TPS performance claim comes from this functional run.

## Português brasileiro

No cliente mínimo da Alpha 28:1081receitas resolveram corretamente,432blocos foram colocados pelo item e removidos sem órfãos nos pontos conferidos,416peças aceitaram/retornaram água pelas rotinas vanilla.432estados não padrão sobreviveram ao reinício completo e à recarga de dados. Os 16postes não foram considerados aprovados para água. Isso não substitui teste manual de baldes, automação ou todos os cenários de sobrevivência.
