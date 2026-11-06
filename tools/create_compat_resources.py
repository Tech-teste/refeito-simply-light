"""Optional Create data contract: include our empty-collision lights in assembly.

No runtime dependency, collision changes, attachment override or moving-light promise.
See Create BlockMovementChecksImpl.isMovementNecessaryFallback.
"""
from pathlib import Path
import json

def generate(root):
 root=Path(root)
 families=('fixture','bulb','strip_low','strip_high','strip_wall')
 suffixes=('', '_redstone', '_redstone_inverted')
 states=root/'assets/lumaworks/blockstates'
 values=sorted('lumaworks:'+p.stem for p in states.glob('*.json')
               if any(p.stem.endswith('_'+family+suffix) for family in families for suffix in suffixes))
 assert len(values)==240, len(values)
 target=root/'data/create/tags/block/movable_empty_collider.json'
 target.parent.mkdir(parents=True,exist_ok=True)
 target.write_text(json.dumps({'replace':False,'values':values},indent=2)+'\n',encoding='utf-8')
