> Historical design notes; later test outcomes are in REVIEW-STATUS.md and PERFORMANCE-PACK.md. / Notas iniciais de design; resultados posteriores em REVIEW-STATUS.md e PERFORMANCE-PACK.md.

# Alpha26 — revisão visual e acesso ao RGB

Candidata em validação; ainda não publicada. IDs, receitas, estados, colisões e código RGB preservados da Alpha25 (artifact-diff.json). Não transfere automaticamente a validação de desempenho antiga para a nova versão.

Materiais próprios em32px: juntas e marcas discretas no grafite, difusor em agrupamentos de pixels sem gradiente artificial. Base do poste com encaixe/collar ajustado; contagem de elementos permanece5/1/11 nas três partes e17 no item. Bulbo passa de4para5 elementos por um anel no soquete; arandela permanece3. Não há animação de textura nem tarefa adicional por lâmpada.

Configuração nativa do NeoForge registrada na lista de mods. Alternância pelo widget real, save e reação do RGB aprovados nos dois sentidos emEN/PT. Preferência de cada cliente; não envia pacote ao servidor. Persistência após reiniciar especificamente pelo fluxo GUI ainda não foi verificada nesta rodada. Há comando e atalho opcional existentes.

Capturas finais emstandalone-verified: helpercorrigido para recalcular layoutGUI na resolução de captura1280x720. Primeiras capturasfinal omitiram rodapé por tamanhoGUI desatualizado; não usar na galeria. A correção é somente no teste.

Pendências: analisar as quatro medições densas atuais; testar candidata noFTB/Sodium/Iris; revisar todas famílias em uso e ícones; candidato limpo/hash/empacotamento; atualizaçãoGitHub/CurseForge e galeria enxuta. Nenhuma promessa de zero lag.
