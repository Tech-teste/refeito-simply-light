# Alpha 28 — Crafter regression and water persistence

## Dedicated development regression

The actual dedicated localhost development server exited successfully after 195,851runtime assertions. All production class files used in that run were byte-identical to the release Alpha 28 jar; only test fixtures were added to the development classpath. See smoke/verification.json and smoke-result.txt. This is not an external client/server network session.

Coverage includes cached selection/collision shapes, all-state wall-strip fluid/rotation/mirror checks, equipment attributes, full-lamp internal-face hiding, redstone/editor state, permissions/cancellation/invalid-menu requests, named finishing-key remainders and actual vanilla Crafter output handling.

306Crafter conversions:240small-light upgrades/inversions across 16colours and 5families,64controlled-strip height conversions,2finishing-key frame conversions. Crafter dispenseFrom was invoked directly on the real vanilla block entity, with output collected in a chest. This proves output/consumption/remainder paths; it is not a timed redstone pulse or hopper throughput test. Named key returned exactly once with its custom name preserved.

Post tests cover water restoration when dismantling and survival/creative breaking of every segment, exact drop counts and orphan removal. These current tests confirm columns support water, though the earlier 416variant bucket round excluded their separate implementation. No claim that Create contraptions work is inferred from Create tags.

## Actual release-client water persistence

416architectural variants placed in isolated glass compartments and filled using vanilla BucketItem.emptyContents. Nondefault state plus waterlogged=true recorded in the manifest. Forced save; process closed; new process launched with the same Alpha 28 release jar. All 416exact states and water fluid matched. Actual server reloadResources future then completed; all 416matched again. Fixture compartments removed and saved.16columns excluded from this persistence fixture.

Not covered: raycast/manual bucket click, flowing water escape, every support-removal scenario, column-water restart, external multiplayer and hopper-clock throughput. Raw development log remains private. No production code changed. Auxiliary processes closed normally, original options restored except master mute and RGB config byte-identical.

## Português brasileiro

O servidor de desenvolvimento passou 195.851verificações, incluindo 306conversões no Crafter vanilla e devolução da chave nomeada. O acionamento usou o método real de fabricação do Crafter; não é um ensaio de pulso/hopper.

No cliente com o JAR de distribuição,416peças preenchidas pela rotina vanilla do balde mantiveram estado e água após reinício completo e recarga de dados. Compartimentos de vidro isolaram o teste de persistência de qualquer escoamento. Postes aceitam água e foram cobertos na regressão de quebra/restauração, mas não nesse conjunto de 416peças persistidas.
