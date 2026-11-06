# Alpha26 — FTB Evolution2, Sodium/Iris/Complementary

Rodada real na cópia auxiliar, oito intervalos30s no mesmo cenário de oficina, RGBoff/on/on/off para Sodium semshader e comComplementary. Resolução1280x720fora da tela; PC compartilhado i5-14600K/RTX5060. Taxa de callbacks não representa FPS visível. Não comparar diretamente com standalone4096: este cenário contém oficina/luminárias diferentes. Não executar compilação durante medições. Aquecimento60s após mundo pronto e15s por mudança de shader;8s por estadoRGB.

|Janela|TPS|quadro p99 ms|quadro máximo ms|MSPT p95|MSPT máximo|quadros >50ms|ticks >50ms|
|---|---:|---:|---:|---:|---:|---:|---:|
|a26pack_off_off1|19.999|4.1703|204.7186|4.5033|8.7291|21|0|
|a26pack_off_off2|19.999|2.9079|48.6045|5.5565|25.7699|0|0|
|a26pack_off_on1|19.999|3.0532|160.7667|5.0901|28.4841|1|0|
|a26pack_off_on2|19.999|2.6452|22.6460|4.2687|11.1432|0|0|
|a26pack_original_off1|19.998|6.1058|48.8050|4.8019|23.2607|0|0|
|a26pack_original_off2|19.998|4.0876|46.6549|3.1437|8.3978|0|0|
|a26pack_original_on1|19.998|4.2735|24.8150|3.6116|23.1375|0|0|
|a26pack_original_on2|19.999|4.2576|53.1356|3.4028|7.0959|1|0|

Todos os intervalos tiveram frame_comparison_valid=true, throttleNONE e observadores removidos. Picos continuam em controles e RGBativo; não há promessa de zero lag nem atribuição causal/ganho percentual. SCC havia carregado análise do cache; não foi observado marcador de análise nova concluída neste recorte. Hardware/PC compartilhado e tempos de aquecimento não isolam todas fontes de ruído.

GUI de configuração real no idioma atual do pack: botão nos dois sentidos, save e reação NativeRgb aprovados. EN/PT já testados standalone; a tentativa de recarregar idioma completo no pack excedeu o prazo da ferramenta e foi registrada como incompleta, não aprovada.10pequenas luzes normal/invertida responderam aos dois estados de redstone em cada modo de shader. Isso não é um clock rápido/performance de redstone nesta rodada, nem teste longo de vazamento.

Processo78688 encerrado normalmente. Jar25anterior, opções (mestre0), RGB e iris.properties restaurados. Capturas da oficina têm HUDresidual e são evidência técnica; não selecionadas para publicação sem captura limpa ou recorte documentado. Nada desta rodada foi publicado.
