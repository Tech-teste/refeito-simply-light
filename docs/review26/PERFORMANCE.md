# Alpha26 — medição standalone densa

Quatro janelas de30s, mesma montagem4096luzes (blocos/emissão verificados), ordem RGBoff/on/on/off;1280×720 fora da tela, renderizador vanilla, sem Sodium/Iris/shader. PC compartilhado. Aquecimento12s após montagem e8s por estado. Medição por callbacks: a taxa não representa FPS visível no monitor e não deve virar anúncio de milhares deFPS. Nenhuma compilação ocorreu durante as janelas.

| Janela | TPS | quadro p99 ms | quadro máximo ms | MSPT p95 | MSPT máximo | quadros >50ms | ticks >50ms |
|---|---:|---:|---:|---:|---:|---:|---:|
| a26_dense_off1 | 19.998 | 1.3864 | 42.7287 | 1.3596 | 2.6226 | 0 | 0 |
| a26_dense_on1 | 19.999 | 0.8381 | 12.2564 | 1.1651 | 2.6212 | 0 | 0 |
| a26_dense_on2 | 19.997 | 0.8355 | 11.9588 | 1.1175 | 3.0467 | 0 | 0 |
| a26_dense_off2 | 19.999 | 0.8579 | 18.8851 | 1.0675 | 1.5101 | 0 | 0 |

Todos os intervalos tiveram throttleNONE e observadores removidos. A diferença entre janelas não prova ganho ou custo causal doRGB; controle inicial mais lento pode refletir aquecimento adicional/PC compartilhado. Não é benchmark doFTB, de shaders, de todas as geometrias nem teste prolongado de vazamento. Hardware documentado nesta revisão:i5-14600K/RTX5060. Resultados anteriores do pack com engasgos continuam válidos e não são anulados por esta amostra.

AuxiliarPID71904 encerrado normalmente; jar25, configuração e opções restaurados (mestre0). Próximo gate: candidata26noFTB/Sodium/Iris/Complementary e cenário redstone atual, empacotamento limpo e publicação autorizada.
