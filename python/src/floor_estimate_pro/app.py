from __future__ import annotations

import sys

from PySide6.QtWidgets import QApplication

from floor_estimate_pro.ui.main_frame import MainFrame


def main() -> None:
    app = QApplication(sys.argv)
    frame = MainFrame()
    frame.show()
    sys.exit(app.exec())


if __name__ == "__main__":
    main()
