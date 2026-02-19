package com.bdm.tech.babynotes.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bdm.tech.babynotes.data.Baby
import com.bdm.tech.babynotes.data.BabyNotesRepository
import com.bdm.tech.babynotes.data.FeedingRecord
import com.bdm.tech.babynotes.data.FeedingType
import com.bdm.tech.babynotes.data.MedicineRecord
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BabyNotesViewModel(
    private val repository: BabyNotesRepository
) : ViewModel() {

    val babies: StateFlow<List<Baby>> = repository.allBabies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val feedings: StateFlow<List<FeedingRecord>> = repository.allFeedings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val medicine: StateFlow<List<MedicineRecord>> = repository.allMedicine
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addBaby(name: String) {
        viewModelScope.launch { repository.addBaby(name) }
    }

    fun deleteBaby(id: Long) {
        viewModelScope.launch { repository.deleteBaby(id) }
    }

    fun addFeeding(babyId: Long, feedingType: FeedingType, volume: Int = 0) {
        viewModelScope.launch { repository.addFeeding(babyId, feedingType, volume) }
    }

    fun addMedicine(babyId: Long, note: String = "") {
        viewModelScope.launch { repository.addMedicine(babyId, note) }
    }

    fun deleteFeeding(id: Long) {
        viewModelScope.launch { repository.deleteFeeding(id) }
    }

    fun deleteMedicine(id: Long) {
        viewModelScope.launch { repository.deleteMedicine(id) }
    }
}

class BabyNotesViewModelFactory(
    private val repository: BabyNotesRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BabyNotesViewModel::class.java)) {
            return BabyNotesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
