# Reference comparison / Comparação com a referência

Reference: Simply Light1.5.3 for Minecraft1.21.1, by Flanks255, local source snapshot. Candidate: Alpha27, Minecraft26.1.2. The reference application was not launched in this comparison. Original files are read for analysis only; code/artwork are not redistributed. Per-file source hashes are in comparison-audit.json.

REFERENCE-160.csv maps every one of the160registered reference block IDs to a unique candidate family/colour counterpart, with the reference English name and our own EN/PT names. This mapping is not automatic world migration and does not claim exact artwork/CTM parity.

| Feature | Current scope and difference |
|---|---|
| Normal/inverted full lights | Same signal polarity and15/0emission logic,16colours; our runtime tests cover all current registered states. |
| Slabs/panels | Half/quarter thickness and six-face orientation. Both source implementations inherit an adjacent thin lamp orientation unless secondary use is active. Our geometry/art remains original. |
| Bulbs/fixtures/rods | Corresponding16-colour families; emission14for bulbs,15for fixtures/rods. Our small families add normal/inverted redstone variants. |
| Floor/ceiling edge strips | Both derive four edges from supporting side neighbours when placed and preserve the chosen shape afterwards. It is placement-time selection, not continuous automatic reshaping. Our four-bit editor adds real neighbour icons and server-side access checks. |
| Water | Current families support waterlogging, including full blocks in our implementation; the original full LampBlock has only its on/off property. Do not claim identical water behavior for every family. |
| Three-block posts | Corresponding three-part placement and top-only15emission. Own slimmer graphite geometry/drops implementation, independently tested. |
| Wearable lights/post weapon | Preserved using our own equipment/attribute implementation. |
| Connected textures | PARTIAL: the reference has CTM texture metadata for thin lights and an optional full-block CTM resource pack. We provide64separate frameless variants and a reusable wrench, with no CTM dependency. This does not dynamically retain a joined outer border. |
| Recipes | Own vanilla glass/glowstone-dust/iron approach; not identical ingredients, costs or outputs to the reference stone/glowstone recipes. Redstone mode and slab/panel conversions are independently implemented. |
| Names | Original source has English/Japanese resources and no pt_br. Our Portuguese naming is original; actual432item names checked EN/PT in27. |
| Additional content |64frameless +160small controlled +48manual wall-strip IDs, giving432total. Environmental RGB is our separate experimental client layer, default-on for new installs and locally switchable. |
| Existing worlds | lumaworks IDs preserved across our candidates. No automatic simplylight→lumaworks registry migration. |

Core runtime evidence comes from the candidate's independent tests and byte comparison: placed geometry, blockstates, recipes, tags, loot and RGB classes are unchanged from26;27changes actual item naming and held/dropped presentation. Dedicated195,851assertions, actual placements/crafts, water/orientation/redstone/drops and local networking reports retain their explicit version/scope. This table is not an approval of untested external multiplayer or Create moving structures.

## Português brasileiro

Os160IDs de referência têm um mapeamento explícito para as dez famílias e16cores, sem converter mundos automaticamente. As funções principais foram comparadas pelo código e pelas evidências de execução da nossa implementação; o original não foi executado nesta comparação.

Diferenças importantes: texturas conectadas CTM não são reproduzidas dinamicamente; itens sem moldura e chave são uma alternativa própria. Fitas horizontais escolhem bordas na colocação e mantêm a forma. Bordas de parede são uma adição manual. Água em blocos inteiros, receitas/custos, modelos, arte e RGB também diferem. As272adições são separadas dos160equivalentes na contagem.

Original: https://github.com/Flanks255/simplylight — MIT, copyright2018Flanks255. Candidate implementation/art: independent and unofficial, All Rights Reserved. No copied original assets are bundled.
