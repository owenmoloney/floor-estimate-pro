# Floor Estimate Pro (Python)

Restore of the original Python desktop app on the `python` branch. Matches the Java app behavior.

```bash
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
pip install -e .
pytest
python -m floor_estimate_pro.app
```

Company packaging (fat JAR / DMG / MSI) stays on `main` (Java).
