package com.bdm.tech.babynotes.ui

/*
 * Refeição: horário, bebê, tipo (enum Mama/Mamadeira) e volume. Medicamento: horário, bebê e descrição.
 */
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.bdm.tech.babynotes.data.Baby
import java.util.Calendar
import com.bdm.tech.babynotes.data.FeedingRecord
import com.bdm.tech.babynotes.data.FeedingType
import com.bdm.tech.babynotes.data.HygieneRecord
import com.bdm.tech.babynotes.data.MedicineRecord
import android.app.DatePickerDialog
import android.app.TimePickerDialog

@Composable
fun BabyNotesScreen(
    viewModel: BabyNotesViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val babies by viewModel.babies.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Bebês") },
                icon = { Icon(Icons.Default.ChildCare, contentDescription = null) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Refeições") },
                icon = { Icon(Icons.Default.Restaurant, contentDescription = null) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Medicamentos") },
                icon = { Icon(Icons.Default.Medication, contentDescription = null) }
            )
            Tab(
                selected = selectedTab == 3,
                onClick = { selectedTab = 3 },
                text = { Text("Higiene") },
                icon = { Icon(Icons.Default.Bathtub, contentDescription = null) }
            )
            Tab(
                selected = selectedTab == 4,
                onClick = { selectedTab = 4 },
                text = { Text("Estatísticas") },
                icon = { Icon(Icons.Default.BarChart, contentDescription = null) }
            )
        }
        when (selectedTab) {
            0 -> BabiesTab(viewModel = viewModel, babies = babies)
            1 -> FeedingsTab(viewModel = viewModel, babies = babies)
            2 -> MedicineTab(viewModel = viewModel, babies = babies)
            3 -> HygieneTab(viewModel = viewModel, babies = babies)
            4 -> StatisticsTab(viewModel = viewModel)
        }
    }
}

@Composable
private fun BabiesTab(viewModel: BabyNotesViewModel, babies: List<Baby>) {
    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (babies.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.ChildCare,
                    contentDescription = null,
                    modifier = Modifier.padding(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    "Nenhum bebê cadastrado",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Toque em + para adicionar um bebê",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(babies, key = { it.id }) { baby ->
                    BabyItem(
                        baby = baby,
                        onDelete = { viewModel.deleteBaby(baby.id) }
                    )
                }
            }
        }
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Adicionar bebê")
        }
    }
    if (showAddDialog) {
        AddBabyDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name ->
                viewModel.addBaby(name)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun BabyItem(baby: Baby, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerHigh),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.ChildCare,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.padding(horizontal = 12.dp))
            Text(
                baby.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Excluir")
            }
        }
    }
}

@Composable
private fun FeedingsTab(viewModel: BabyNotesViewModel, babies: List<Baby>) {
    val feedings by viewModel.filteredFeedings.collectAsState()
    val filterPeriod by viewModel.feedingsFilterPeriod.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (babies.isEmpty()) {
            EmptyBabiesMessage()
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterPeriod.entries.forEach { period ->
                        androidx.compose.material3.FilterChip(
                            selected = period == filterPeriod,
                            onClick = { viewModel.setFeedingsFilterPeriod(period) },
                            label = { Text(period.label, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
                if (feedings.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(32.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Restaurant,
                            contentDescription = null,
                            modifier = Modifier.padding(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "Nenhuma refeição no período",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Toque em + para registrar",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(feedings) { record ->
                            FeedingItem(
                                record = record,
                                // Nome: resolvido aqui a partir do babyId do registro
                                babyName = babies.find { it.id == record.babyId }?.name ?: "",
                                onDelete = { viewModel.deleteFeeding(record.id) }
                            )
                        }
                    }
                }
            }
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Registrar refeição")
            }
        }
    }
    if (showAddDialog && babies.isNotEmpty()) {
        AddFeedingDialog(
            babies = babies,
            onDismiss = { showAddDialog = false },
            onConfirm = { babyId: Long, feedingType: FeedingType, volume: Int, timestampMillis: Long ->
                viewModel.addFeeding(babyId, feedingType, volume, timestampMillis)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun MedicineTab(viewModel: BabyNotesViewModel, babies: List<Baby>) {
    val medicine by viewModel.filteredMedicine.collectAsState()
    val filterPeriod by viewModel.medicineFilterPeriod.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (babies.isEmpty()) {
            EmptyBabiesMessage()
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterPeriod.entries.forEach { period ->
                        androidx.compose.material3.FilterChip(
                            selected = period == filterPeriod,
                            onClick = { viewModel.setMedicineFilterPeriod(period) },
                            label = { Text(period.label, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
                if (medicine.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(32.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Medication,
                            contentDescription = null,
                            modifier = Modifier.padding(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "Nenhum medicamento no período",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Toque em + para registrar",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(medicine, key = { it.id }) { record ->
                            MedicineItem(
                                record = record,
                                // Nome: resolvido aqui a partir do babyId do registro
                                babyName = babies.find { it.id == record.babyId }?.name ?: "",
                                onDelete = { viewModel.deleteMedicine(record.id) }
                            )
                        }
                    }
                }
            }
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Registrar medicamento")
            }
        }
    }
    if (showAddDialog && babies.isNotEmpty()) {
        AddMedicineDialog(
            babies = babies,
            onDismiss = { showAddDialog = false },
            onConfirm = { babyId: Long, note: String, timestampMillis: Long ->
                viewModel.addMedicine(babyId, note, timestampMillis)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun HygieneTab(viewModel: BabyNotesViewModel, babies: List<Baby>) {
    val hygiene by viewModel.filteredHygiene.collectAsState()
    val filterPeriod by viewModel.hygieneFilterPeriod.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (babies.isEmpty()) {
            EmptyBabiesMessage()
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterPeriod.entries.forEach { period ->
                        androidx.compose.material3.FilterChip(
                            selected = period == filterPeriod,
                            onClick = { viewModel.setHygieneFilterPeriod(period) },
                            label = { Text(period.label, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
                if (hygiene.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(32.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Bathtub,
                            contentDescription = null,
                            modifier = Modifier.padding(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "Nenhum registro de higiene no período",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Toque em + para registrar",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(hygiene, key = { it.id }) { record ->
                            HygieneItem(
                                record = record,
                                babyName = babies.find { it.id == record.babyId }?.name ?: "",
                                onDelete = { viewModel.deleteHygiene(record.id) }
                            )
                        }
                    }
                }
            }
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Registrar higiene")
            }
        }
    }
    if (showAddDialog && babies.isNotEmpty()) {
        AddHygieneDialog(
            babies = babies,
            onDismiss = { showAddDialog = false },
            onConfirm = { babyId: Long, note: String, timestampMillis: Long ->
                viewModel.addHygiene(babyId, note, timestampMillis)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun StatisticsTab(viewModel: BabyNotesViewModel) {
    val stats by viewModel.consumptionStats.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterPeriod.entries.forEach { period ->
                val selected = period == selectedPeriod
                androidx.compose.material3.FilterChip(
                    selected = selected,
                    onClick = { viewModel.setPeriod(period) },
                    label = { Text(period.label) }
                )
            }
        }
        if (stats.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.BarChart,
                    contentDescription = null,
                    modifier = Modifier.padding(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    "Nenhum consumo registrado no período",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Registre refeições na aba Refeições",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(stats) { stat ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerHigh),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.ChildCare,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                                Text(
                                    stat.babyName,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Total: ${stat.totalVolumeMl} ml",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "${stat.feedingCount} refeições",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                            stat.averagePerDayMl?.let { avg ->
                                Text(
                                    "Média por dia: ${avg.toInt()} ml",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyBabiesMessage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.ChildCare,
            contentDescription = null,
            modifier = Modifier.padding(16.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            "Cadastre um bebê na aba Bebês para registrar notas",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FeedingItem(
    record: FeedingRecord,
    babyName: String,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerHigh),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    formatTime(record.timestampMillis),
                    style = MaterialTheme.typography.titleMedium
                )
                val description = buildList {
                    if (babyName.isNotBlank()) add(babyName)
                    add(record.feedingType.label)
                    add("${record.volume} ml")
                }.joinToString(" • ")
                Text(
                    description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Excluir")
            }
        }
    }
}

@Composable
private fun MedicineItem(
    record: MedicineRecord,
    babyName: String,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerHigh),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    formatTime(record.timestampMillis),
                    style = MaterialTheme.typography.titleMedium
                )
                val description = buildList {
                    if (babyName.isNotBlank()) add(babyName)
                    if (record.note.isNotBlank()) add(record.note)
                }.joinToString(" • ")
                if (description.isNotBlank()) {
                    Text(
                        description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Excluir")
            }
        }
    }
}

@Composable
private fun HygieneItem(
    record: HygieneRecord,
    babyName: String,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerHigh),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    formatTime(record.timestampMillis),
                    style = MaterialTheme.typography.titleMedium
                )
                val description = buildList {
                    if (babyName.isNotBlank()) add(babyName)
                    if (record.note.isNotBlank()) add(record.note)
                }.joinToString(" • ")
                if (description.isNotBlank()) {
                    Text(
                        description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Excluir")
            }
        }
    }
}

@Composable
private fun EditableDateTimeRow(
    selectedTimestamp: Long,
    onTimestampChange: (Long) -> Unit
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            formatDateTime(selectedTimestamp),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = {
            val cal = Calendar.getInstance().apply { timeInMillis = selectedTimestamp }
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, month)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    TimePickerDialog(
                        context,
                        { _, hourOfDay, minute ->
                            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            cal.set(Calendar.MINUTE, minute)
                            cal.set(Calendar.SECOND, 0)
                            cal.set(Calendar.MILLISECOND, 0)
                            onTimestampChange(cal.timeInMillis)
                        },
                        cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE),
                        true
                    ).show()
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }) {
            Text("Alterar")
        }
    }
}

@Composable
private fun AddBabyDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Novo bebê") },
        text = {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name.trim()) }) {
                Text("Adicionar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddFeedingDialog(
    babies: List<Baby>,
    onDismiss: () -> Unit,
    onConfirm: (babyId: Long, feedingType: FeedingType, volume: Int, timestampMillis: Long) -> Unit
) {
    var selectedBaby by remember { mutableStateOf(babies.firstOrNull()) }
    var selectedFeedingType by remember { mutableStateOf(FeedingType.MAMADEIRA) }
    var volumeStr by remember { mutableStateOf("") }
    var selectedTimestamp by remember { mutableStateOf(System.currentTimeMillis()) }
    var expandedBaby by remember { mutableStateOf(false) }
    var expandedType by remember { mutableStateOf(false) }
    val currentBaby = selectedBaby ?: babies.firstOrNull()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar refeição") },
        text = {
            Column {
                Text("Data e hora", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(4.dp))
                EditableDateTimeRow(
                    selectedTimestamp = selectedTimestamp,
                    onTimestampChange = { selectedTimestamp = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
                ExposedDropdownMenuBox(
                    expanded = expandedBaby,
                    onExpandedChange = { expandedBaby = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = currentBaby?.name ?: "Selecione o bebê",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Bebê") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBaby) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedBaby,
                        onDismissRequest = { expandedBaby = false }
                    ) {
                        babies.forEach { baby ->
                            DropdownMenuItem(
                                text = { Text(baby.name) },
                                onClick = {
                                    selectedBaby = baby
                                    expandedBaby = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                ExposedDropdownMenuBox(
                    expanded = expandedType,
                    onExpandedChange = { expandedType = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedFeedingType.label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedType,
                        onDismissRequest = { expandedType = false }
                    ) {
                        FeedingType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.label) },
                                onClick = {
                                    selectedFeedingType = type
                                    expandedType = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                TextField(
                    value = volumeStr,
                    onValueChange = { newValue -> if (newValue.isEmpty() || newValue.all { c -> c.isDigit() }) volumeStr = newValue },
                    label = { Text("Volume") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    currentBaby?.let {
                        val volume = volumeStr.toIntOrNull() ?: 0
                        onConfirm(it.id, selectedFeedingType, volume, selectedTimestamp)
                    }
                }
            ) {
                Text("Registrar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddMedicineDialog(
    babies: List<Baby>,
    onDismiss: () -> Unit,
    onConfirm: (babyId: Long, note: String, timestampMillis: Long) -> Unit
) {
    var selectedBaby by remember { mutableStateOf(babies.firstOrNull()) }
    var note by remember { mutableStateOf("") }
    var selectedTimestamp by remember { mutableStateOf(System.currentTimeMillis()) }
    var expanded by remember { mutableStateOf(false) }
    val currentBaby = selectedBaby ?: babies.firstOrNull()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar medicamento") },
        text = {
            Column {
                Text("Data e hora", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(4.dp))
                EditableDateTimeRow(
                    selectedTimestamp = selectedTimestamp,
                    onTimestampChange = { selectedTimestamp = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = currentBaby?.name ?: "Selecione o bebê",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Bebê") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        babies.forEach { baby ->
                            DropdownMenuItem(
                                text = { Text(baby.name) },
                                onClick = {
                                    selectedBaby = baby
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                TextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Descrição (ex: nome do medicamento)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    currentBaby?.let { onConfirm(it.id, note.trim(), selectedTimestamp) }
                }
            ) {
                Text("Registrar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddHygieneDialog(
    babies: List<Baby>,
    onDismiss: () -> Unit,
    onConfirm: (babyId: Long, note: String, timestampMillis: Long) -> Unit
) {
    var selectedBaby by remember { mutableStateOf(babies.firstOrNull()) }
    var note by remember { mutableStateOf("") }
    var selectedTimestamp by remember { mutableStateOf(System.currentTimeMillis()) }
    var expanded by remember { mutableStateOf(false) }
    val currentBaby = selectedBaby ?: babies.firstOrNull()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar higiene") },
        text = {
            Column {
                Text("Data e hora", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(4.dp))
                EditableDateTimeRow(
                    selectedTimestamp = selectedTimestamp,
                    onTimestampChange = { selectedTimestamp = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = currentBaby?.name ?: "Selecione o bebê",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Bebê") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        babies.forEach { baby ->
                            DropdownMenuItem(
                                text = { Text(baby.name) },
                                onClick = {
                                    selectedBaby = baby
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                TextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Descrição (ex: banho, troca de fralda)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    currentBaby?.let { onConfirm(it.id, note.trim(), selectedTimestamp) }
                }
            ) {
                Text("Registrar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
