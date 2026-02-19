# Documentação das telas – Baby Notes

Este documento descreve todas as telas, composables e o fluxo de dados relacionados à interface do app Baby Notes.

---

## 1. Visão geral

O app possui **uma Activity** e **uma tela principal** em Compose, organizada em **três abas**:

| Aba           | Objetivo                          | Arquivo principal   |
|---------------|-----------------------------------|---------------------|
| **Bebês**     | Listar e cadastrar bebês          | `BabyNotesScreen.kt`|
| **Refeições** | Listar e registrar refeições      | `BabyNotesScreen.kt`|
| **Medicamentos** | Listar e registrar medicamentos | `BabyNotesScreen.kt`|

- **Navegação:** `TabRow` com três `Tab`s; não há navegação por stack (telas separadas).
- **Estado:** `BabyNotesViewModel` expõe `StateFlow`s; a UI usa `collectAsState()`.
- **Persistência:** Room; repositório e DAOs em `data/`.

---

## 2. Ponto de entrada: MainActivity

**Arquivo:** `app/src/main/java/com/bdm/tech/babynotes/MainActivity.kt`

### Responsabilidades

- Inflar o conteúdo com Compose (`setContent`).
- Aplicar `BabyNotesTheme` e edge-to-edge.
- Criar (uma vez) `BabyNotesDatabase`, `BabyNotesRepository` e `BabyNotesViewModel` (via `BabyNotesViewModelFactory`).
- Envolver o conteúdo em um `Scaffold` e exibir `BabyNotesScreen` com o padding do scaffold.

### Fluxo de criação

```
MainActivity.onCreate()
  → setContent { BabyNotesTheme { ... } }
  → remember { BabyNotesDatabase.getInstance(context) }
  → remember(database) { BabyNotesRepository(babyDao, feedingDao, medicineDao) }
  → viewModel(factory = BabyNotesViewModelFactory(repository))
  → Scaffold { BabyNotesScreen(viewModel, modifier) }
```

Não há navegação entre Activities; toda a UI está dentro de `BabyNotesScreen`.

---

## 3. Tela principal: BabyNotesScreen

**Arquivo:** `app/src/main/java/com/bdm/tech/babynotes/ui/BabyNotesScreen.kt`

### Assinatura

```kotlin
@Composable
fun BabyNotesScreen(
    viewModel: BabyNotesViewModel,
    modifier: Modifier = Modifier
)
```

### Estrutura

- **Estado local:** `selectedTab` (0 = Bebês, 1 = Refeições, 2 = Medicamentos).
- **Estado do ViewModel:** `viewModel.babies.collectAsState()` (usado nas três abas).
- **Layout:** `Column` com:
  1. **TabRow** com três `Tab`s (Bebês, Refeições, Medicamentos), cada um com ícone e texto.
  2. Conteúdo da aba selecionada via `when (selectedTab)`.

### Composables chamados

| Tab index | Composable     | Parâmetros                          |
|-----------|----------------|-------------------------------------|
| 0         | `BabiesTab`    | `viewModel`, `babies`               |
| 1         | `FeedingsTab`  | `viewModel`, `babies`               |
| 2         | `MedicineTab`  | `viewModel`, `babies`               |

---

## 4. Aba Bebês (BabiesTab)

### Objetivo

- Listar todos os bebês cadastrados.
- Permitir adicionar novo bebê (FAB e diálogo).
- Permitir excluir um bebê (ícone na linha).

### Estados da tela

| Condição        | O que é exibido |
|-----------------|------------------|
| `babies.isEmpty()` | Estado vazio: ícone, “Nenhum bebê cadastrado”, “Toque em + para adicionar um bebê”. |
| `babies` não vazio | `LazyColumn` de `BabyItem` para cada bebê. |

### Elementos de UI

- **FAB (+):** abre o diálogo de novo bebê.
- **Diálogo:** `AddBabyDialog` (título “Novo bebê”, campo “Nome”, botões Adicionar / Cancelar).

### Composables

- **`BabiesTab(viewModel, babies)`**  
  Controla estado `showAddDialog`, decide entre estado vazio ou lista, exibe FAB e `AddBabyDialog`.

- **`BabyItem(baby, onDelete)`**  
  Card com ícone `ChildCare`, nome do bebê e `IconButton` de delete.  
  - **Parâmetros:** `Baby`, `onDelete: () -> Unit`.

- **`AddBabyDialog(onDismiss, onConfirm)`**  
  - **Estado local:** `name` (texto do campo).  
  - **Ações:** `onConfirm(name.trim())` ao confirmar; `onDismiss` ao cancelar.  
  - **Parâmetros:** `onDismiss: () -> Unit`, `onConfirm: (String) -> Unit`.

### Fluxo de dados (Bebês)

- Lista: `viewModel.babies` (já coletado no `BabyNotesScreen` e passado para `BabiesTab`).
- Adicionar: `viewModel.addBaby(name)` → repositório → Room.
- Excluir: `viewModel.deleteBaby(baby.id)` → repositório → Room.

---

## 5. Aba Refeições (FeedingsTab)

### Objetivo

- Listar **todas** as refeições (de todos os bebês), ordenadas por data/hora (mais recente primeiro).
- Permitir registrar nova refeição (FAB e diálogo com seleção de bebê e descrição).

### Estados da tela

| Condição              | O que é exibido |
|-----------------------|------------------|
| `babies.isEmpty()`    | `EmptyBabiesMessage`: “Cadastre um bebê na aba Bebês para registrar notas”. |
| Bebês existem e `feedings.isEmpty()` | Estado vazio: ícone, “Nenhuma refeição registrada”, “Toque em + para registrar”. |
| Bebês existem e há refeições | `LazyColumn` de `FeedingItem`. |

### Elementos de UI

- **FAB (+):** abre o diálogo de registrar refeição (só se `babies.isNotEmpty()`).
- **Diálogo:** `AddFeedingDialog` (horário atual, dropdown do bebê, campo descrição, Registrar / Cancelar).

### Composables

- **`FeedingsTab(viewModel, babies)`**  
  Usa `viewModel.feedings.collectAsState()` e `showAddDialog`. Decide entre `EmptyBabiesMessage`, estado vazio de refeições ou lista de `FeedingItem`. Exibe FAB e, se houver bebês, `AddFeedingDialog`.

- **`FeedingItem(record, babyName, onDelete)`**  
  Card com: horário (via `formatTime`), nome do bebê (se não vazio), descrição/nota (se não vazio), botão excluir.  
  - **Parâmetros:** `FeedingRecord`, `babyName: String`, `onDelete: () -> Unit`.

- **`AddFeedingDialog(babies, onDismiss, onConfirm)`**  
  - **Estado local:** `selectedBaby`, `note`, `expanded` (dropdown).  
  - **Conteúdo:** texto com horário atual, `ExposedDropdownMenuBox` para escolher o bebê, `TextField` para descrição (ex.: mama, mamadeira).  
  - **Ação:** `onConfirm(babyId, note.trim())` ao registrar.  
  - **Parâmetros:** `babies: List<Baby>`, `onDismiss`, `onConfirm: (babyId: Long, note: String) -> Unit`.

### Fluxo de dados (Refeições)

- Lista: `viewModel.feedings` → repositório → `FeedingDao.getAllFlow()`.
- Nome do bebê no item: `babies.find { it.id == record.babyId }?.name`.
- Registrar: `viewModel.addFeeding(babyId, note)` → repositório → Room (`FeedingRecord` com `babyId`, `timestampMillis`, `note`).
- Excluir: `viewModel.deleteFeeding(record.id)` → repositório → Room.

---

## 6. Aba Medicamentos (MedicineTab)

### Objetivo

- Listar **todos** os medicamentos (de todos os bebês), ordenados por data/hora (mais recente primeiro).
- Permitir registrar novo medicamento (FAB e diálogo com seleção de bebê e descrição).

### Estados da tela

| Condição              | O que é exibido |
|-----------------------|------------------|
| `babies.isEmpty()`    | `EmptyBabiesMessage` (mesmo texto da aba Refeições). |
| Bebês existem e `medicine.isEmpty()` | Estado vazio: ícone, “Nenhum medicamento registrado”, “Toque em + para registrar”. |
| Bebês existem e há registros | `LazyColumn` de `MedicineItem`. |

### Elementos de UI

- **FAB (+):** abre o diálogo de registrar medicamento (só se `babies.isNotEmpty()`).
- **Diálogo:** `AddMedicineDialog` (horário atual, dropdown do bebê, campo descrição, Registrar / Cancelar).

### Composables

- **`MedicineTab(viewModel, babies)`**  
  Usa `viewModel.medicine.collectAsState()` e `showAddDialog`. Mesma lógica de estados que `FeedingsTab`, com `MedicineItem` e `AddMedicineDialog`.

- **`MedicineItem(record, babyName, onDelete)`**  
  Estrutura igual a `FeedingItem`: horário, nome do bebê, descrição/nota, botão excluir.  
  - **Parâmetros:** `MedicineRecord`, `babyName: String`, `onDelete: () -> Unit`.

- **`AddMedicineDialog(babies, onDismiss, onConfirm)`**  
  Análogo a `AddFeedingDialog`: horário, dropdown do bebê, campo descrição (ex.: nome do medicamento).  
  - **Parâmetros:** `babies: List<Baby>`, `onDismiss`, `onConfirm: (babyId: Long, note: String) -> Unit`.

### Fluxo de dados (Medicamentos)

- Lista: `viewModel.medicine` → repositório → `MedicineDao.getAllFlow()`.
- Nome do bebê: `babies.find { it.id == record.babyId }?.name`.
- Registrar: `viewModel.addMedicine(babyId, note)` → repositório → Room.
- Excluir: `viewModel.deleteMedicine(record.id)` → repositório → Room.

---

## 7. Composables compartilhados

### EmptyBabiesMessage()

Exibido nas abas **Refeições** e **Medicamentos** quando não há bebês cadastrados.

- Ícone `ChildCare`.
- Texto: “Cadastre um bebê na aba Bebês para registrar notas”.
- Sem parâmetros; apenas layout centralizado.

---

## 8. ViewModel: BabyNotesViewModel

**Arquivo:** `app/src/main/java/com/bdm/tech/babynotes/ui/BabyNotesViewModel.kt`

### Estado exposto (StateFlow)

| Propriedade  | Tipo                         | Origem                    | Uso nas telas        |
|-------------|------------------------------|---------------------------|----------------------|
| `babies`    | `StateFlow<List<Baby>>`      | `repository.allBabies`    | Lista e diálogos     |
| `feedings`  | `StateFlow<List<FeedingRecord>>` | `repository.allFeedings`  | Lista na aba Refeições |
| `medicine`  | `StateFlow<List<MedicineRecord>>` | `repository.allMedicine`  | Lista na aba Medicamentos |

### Ações (funções)

| Função              | Efeito                                      |
|----------------------|---------------------------------------------|
| `addBaby(name)`      | Insere bebê no repositório (Room).          |
| `deleteBaby(id)`     | Remove bebê por id.                         |
| `addFeeding(babyId, note)` | Insere refeição com horário atual.    |
| `addMedicine(babyId, note)` | Insere medicamento com horário atual. |
| `deleteFeeding(id)`  | Remove refeição por id.                     |
| `deleteMedicine(id)` | Remove medicamento por id.                  |

Todas as escritas são feitas em `viewModelScope.launch` para não bloquear a UI.

### Factory

`BabyNotesViewModelFactory(repository)` é usada em `MainActivity` para criar o `BabyNotesViewModel` com o `BabyNotesRepository` injetado.

---

## 9. Formatação de data/hora: TimeFormat.kt

**Arquivo:** `app/src/main/java/com/bdm/tech/babynotes/ui/TimeFormat.kt`

Funções usadas nos itens de lista e nos diálogos:

- **`formatTime(timestampMillis: Long): String`**  
  - Se for “hoje”: só hora (`HH:mm`).  
  - Caso contrário: data curta + hora (ex.: “Jan 15 14:30”).  
  Usado em `FeedingItem` e `MedicineItem`.

- **`formatDateTime(timestampMillis: Long): String`**  
  Formato completo (ex.: “Jan 15, 14:30”).  
  Usado nos diálogos de registrar refeição/medicamento para mostrar o horário do registro.

Locale: `Locale.getDefault()`.

---

## 10. Resumo dos arquivos por responsabilidade

| Arquivo            | Responsabilidade |
|--------------------|------------------|
| `MainActivity.kt`  | Entrada do app, criação de DB/Repository/ViewModel, Scaffold e `BabyNotesScreen`. |
| `BabyNotesScreen.kt` | Tela principal com TabRow, três abas e todos os composables de lista, itens e diálogos. |
| `BabyNotesViewModel.kt` | Estado (babies, feedings, medicine) e ações de CRUD; factory do ViewModel. |
| `TimeFormat.kt`    | Formatação de data/hora para exibição. |
| `ui/theme/*`       | Tema, cores e tipografia (BabyNotesTheme). |

Não existem outras telas ou Activities; toda a navegação é por abas dentro de `BabyNotesScreen`.

---

## 11. Fluxo resumido (navegação e dados)

```
MainActivity
  └── BabyNotesTheme
        └── Scaffold
              └── BabyNotesScreen(viewModel)
                    ├── TabRow [ Bebês | Refeições | Medicamentos ]
                    └── Conteúdo da aba:
                          ├── BabiesTab   → lista BabyItem + AddBabyDialog
                          ├── FeedingsTab → lista FeedingItem + AddFeedingDialog (ou EmptyBabiesMessage / vazio)
                          └── MedicineTab → lista MedicineItem + AddMedicineDialog (ou EmptyBabiesMessage / vazio)

Dados: ViewModel (StateFlow) ← Repository ← Room (BabyDao, FeedingDao, MedicineDao)
```

Esta documentação cobre tudo relacionado às telas do app: estrutura, estados, composables, ViewModel e formatação de tempo.
