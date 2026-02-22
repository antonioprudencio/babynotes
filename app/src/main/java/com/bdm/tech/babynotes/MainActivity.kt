package com.bdm.tech.babynotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bdm.tech.babynotes.data.BabyNotesDatabase
import com.bdm.tech.babynotes.data.BabyNotesRepository
import com.bdm.tech.babynotes.ui.BabyNotesScreen
import com.bdm.tech.babynotes.ui.BabyNotesViewModel
import com.bdm.tech.babynotes.ui.BabyNotesViewModelFactory
import com.bdm.tech.babynotes.ui.theme.BabyNotesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BabyNotesTheme {
                val context = LocalContext.current
                val database = remember { BabyNotesDatabase.getInstance(context) }
                val repository = remember(database) {
                    BabyNotesRepository(
                        database.babyDao(),
                        database.feedingDao(),
                        database.medicineDao(),
                        database.hygieneDao()
                    )
                }
                val viewModel: BabyNotesViewModel = viewModel(
                    factory = BabyNotesViewModelFactory(repository)
                )
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BabyNotesScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
