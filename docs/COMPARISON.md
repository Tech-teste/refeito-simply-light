# Comparison with Simply Light / Comparação com Simply Light

English first; português abaixo. Reference: Simply Light 1.5.3 for Minecraft 1.21.1, by Flanks255. This is a functional comparison of our own implementation, not an official port or a world-migration promise.

| Original feature | Refeito implementation |
|---|---|
| Normal and inverted redstone full blocks | Both in 16 colors; shapeless behavior conversion |
| Half slabs and quarter panels | Both, rotatable, in 16 colors |
| Small fixtures and bulbs | Own models with housing/socket/diffuser; different mounting faces |
| Floor and ceiling edge strips | Both, with a compact four-direction editor |
| Three-block lamp post | Own post; safe dismantling and one-item drop |
| Rod light | Own rotatable rod in 16 colors |
| Small waterloggable lights | Supported |
| Sixteen colors | Supported across all ten families |
| Connected-texture options using CTM | Separate frameless items and wrench conversion; no CTM dependency. This is not the original CTM implementation |
| Wearable lights and playful post weapon | Preserved |
| Environmental RGB | Own optional experimental client layer, off by default; specific renderer/shader tests documented separately |
| Existing simplylight worlds | No automatic ID migration |

The reference jar has 160 block IDs; all 160 have a mapped functional counterpart. Refeito adds 64 frameless block IDs and a reusable finishing tool, for 224 blocks / 225 items. We checked mappings against the original jar locally, without redistributing it.

Recipes use our own glass/glowstone/metal approach and vanilla materials. We retain practical one-for-one behavior conversions and slab/panel splits. Models, textures, icon, tool artwork, code and screenshots in this repository are ours; original artwork and shader distributions are not included.

Original sources and MIT license: [Flanks255/simplylight](https://github.com/Flanks255/simplylight), [CurseForge project](https://www.curseforge.com/minecraft/mc-mods/simply-light).

## Português brasileiro

Os 160 IDs antigos têm equivalentes funcionais nas dez famílias e 16 cores. Há mais 64 variantes sem moldura e uma ferramenta reutilizável. As conversões práticas de normal/invertida, fita inferior/superior e laje/painel foram preservadas, com crafts próprios feitos de materiais vanilla.

Não copiamos código nem arte do Simply Light e não sugerimos afiliação. As superfícies sem moldura usam itens próprios, não o código CTM original. O RGB é uma função experimental independente e começa desligado. Mundos com IDs simplylight não são convertidos automaticamente. Crédito da referência: Flanks255; licença original MIT.
