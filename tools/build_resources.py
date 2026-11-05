from pathlib import Path
import sys
import generate_resources as resources
resources.ROOT = Path(sys.argv[1]).resolve()
resources.generate()
