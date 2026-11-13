# Workflow: Implement Android Feature (Data + UI)
1. Baca `docs/API_CONTRACT.md`, `docs/RIVE_SPECS.md`, dan baca frame referensi di Figma via Figcraft MCP.
2. Layer Data (`data/`): Buat Retrofit DTO, Room Entity (`@Upsert`), DAO, dan Repository SSOT.
3. Layer Domain (`domain/`): Buat Domain Model dan UseCase murni Kotlin.
4. Layer Presentation (`presentation/`): Buat `UiState`, `ViewModel` (UDF), dan Composable UI dengan `TactileButton3D` serta `NippiaRiveAnimation`.