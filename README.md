# SimpleJavaCalculator

A JavaFX calculator with an AMOLED-style interface and a reusable `CalculatorEngine`.

## Download for Windows

Download the latest **FXCalculator-windows-x64.zip** from the [GitHub Releases page](https://github.com/AmjedBS/SimpleJavaCalculator/releases/latest), extract it, then run `FXCalculator\FXCalculator.exe`.

The ZIP contains the application and its private Java/JavaFX runtime. You do not need to install Java. Keep the extracted `FXCalculator` folder intact when moving or running the app.

## Build from source

Requirements:

- Windows x64
- JDK 21 (with `javac`, `jar`, and `jpackage`)
- The JavaFX 21.0.12 SDK JARs in `lib`
- The JavaFX 21.0.12 Windows x64 JMODs in `lib\javafx-jmods-21.0.12`

Run `build_final.cmd` from any terminal, or use the VS Code task **Build FXCalculator standalone app**. The executable and bundled runtime are created in `dist\FXCalculator`.

To publish a downloadable ZIP release, push a version tag such as `v1.0.0`. GitHub Actions builds the standalone app and attaches `FXCalculator-windows-x64.zip` to that release automatically.

## Project files

- `FXCalculator.java` — JavaFX user interface and application entry point
- `CalculatorEngine.java` — calculator expression engine used by the UI
- `EngineTest.java` — console-based engine diagnostics
- `build_final.cmd` — builds the standalone Windows application image
