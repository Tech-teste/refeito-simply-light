# Alpha28 — reproducible packaged bytes

The earlier Windows candidate (SHA256190d26625cbbdfdc0f6f47c8cdc26ee93fcca657599182be94e765e2c9995556) and GitHub Linux artifact differed in4,353JSON files solely because of CRLF/LF line endings. Every production class and image was identical, and all decoded JSON values matched. The previous candidate is preserved locally for traceability.

The build now normalizes packaged JSON to LF through processResources, with an explicit task input so the normalization invalidates old cached output. No runtime source, model geometry, texture pixels, recipe content, IDs or RGB logic changed.

The resulting Windows JAR is byte-for-byte identical to the actual GitHub artifact from run42217198073, commit61c8d12c64685d8655edd92b2d12396660ffa8ea. That earlier Linux artifact already used LF. SHA256:

4b6190c415d139536f6b691e03baa8fe7ddff11b32b6b828bdc68f1dfbf33f72

The normalized JAR was opened in the standalone auxiliary client. Actual names/translations/tooltips passed in English/Portuguese;1,081engine recipes and432item placements passed again. Auxiliary closed and options/config restored, except master mute retained. Other Alpha28 reports retain their original hashes and scopes; content equivalence connects those results but is not a new performance run.

No new binary was uploaded to CurseForge or GitHub Releases. Full review remains open.

## Português brasileiro

A diferença entre os JARs Windows/Linux era apenas quebra de linha em4.353JSONs. A compilação passou a usar LF na saída e agora gera o mesmo arquivo binário nos dois ambientes. Classes, imagens e dados decodificados são idênticos. O JAR normalizado abriu e passou nos testes de nomes, receitas e colocação; não é uma nova medição de desempenho. A versão anterior foi preservada.
