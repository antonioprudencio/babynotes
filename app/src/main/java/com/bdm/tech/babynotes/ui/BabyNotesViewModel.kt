package com.bdm.tech.babynotes.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bdm.tech.babynotes.data.Baby
import com.bdm.tech.babynotes.data.BabyNotesRepository
import com.bdm.tech.babynotes.data.FeedingRecord
import com.bdm.tech.babynotes.data.FeedingType
import com.bdm.tech.babynotes.data.HygieneRecord
import com.bdm.tech.babynotes.data.MedicineRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
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

    val hygiene: StateFlow<List<HygieneRecord>> = repository.allHygiene
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _feedingsFilterPeriod = MutableStateFlow(FilterPeriod.TODAY)
    val feedingsFilterPeriod: StateFlow<FilterPeriod> = _feedingsFilterPeriod
    val filteredFeedings: StateFlow<List<FeedingRecord>> = combine(feedings, _feedingsFilterPeriod) { list, period ->
        val (start, end) = StatisticsHelper.periodRange(period)
        list.filter { it.timestampMillis in start..end }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _medicineFilterPeriod = MutableStateFlow(FilterPeriod.TODAY)
    val medicineFilterPeriod: StateFlow<FilterPeriod> = _medicineFilterPeriod
    val filteredMedicine: StateFlow<List<MedicineRecord>> = combine(medicine, _medicineFilterPeriod) { list, period ->
        val (start, end) = StatisticsHelper.periodRange(period)
        list.filter { it.timestampMillis in start..end }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _hygieneFilterPeriod = MutableStateFlow(FilterPeriod.TODAY)
    val hygieneFilterPeriod: StateFlow<FilterPeriod> = _hygieneFilterPeriod
    val filteredHygiene: StateFlow<List<HygieneRecord>> = combine(hygiene, _hygieneFilterPeriod) { list, period ->
        val (start, end) = StatisticsHelper.periodRange(period)
        list.filter { it.timestampMillis in start..end }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedPeriod = MutableStateFlow(FilterPeriod.TODAY)
    val selectedPeriod: StateFlow<FilterPeriod> = _selectedPeriod

    val consumptionStats: StateFlow<List<BabyConsumptionStats>> = combine(
        feedings,
        babies,
        _selectedPeriod
    ) { feedingsList, babiesList, period ->
        val (start, end) = StatisticsHelper.periodRange(period)
        val inRange = feedingsList.filter { it.timestampMillis in start..end }
        val byBaby = inRange.groupBy { it.babyId }
        val lastFeedingByBaby = feedingsList
            .groupBy { it.babyId }
            .mapValues { (_, list) -> list.maxOfOrNull { it.timestampMillis } }
        val days = StatisticsHelper.daysInPeriod(period)
        babiesList.map { baby ->
            val babyFeedings = byBaby[baby.id].orEmpty()
            val total = babyFeedings.sumOf { it.volume }
            val avg = if (days > 0 && period != FilterPeriod.TODAY) total.toFloat() / days else null
            BabyConsumptionStats(
                babyId = baby.id,
                babyName = baby.name,
                totalVolumeMl = total,
                feedingCount = babyFeedings.size,
                averagePerDayMl = avg,
                lastFeedingTimestampMillis = lastFeedingByBaby[baby.id]
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setPeriod(period: FilterPeriod) {
        _selectedPeriod.value = period
    }

    fun setFeedingsFilterPeriod(period: FilterPeriod) {
        _feedingsFilterPeriod.value = period
    }

    fun setMedicineFilterPeriod(period: FilterPeriod) {
        _medicineFilterPeriod.value = period
    }

    fun setHygieneFilterPeriod(period: FilterPeriod) {
        _hygieneFilterPeriod.value = period
    }

    fun addBaby(name: String) {
        viewModelScope.launch { repository.addBaby(name) }
    }

    fun updateBaby(baby: Baby) {
        viewModelScope.launch { repository.updateBaby(baby) }
    }

    fun deleteBaby(id: Long) {
        viewModelScope.launch { repository.deleteBaby(id) }
    }

    fun addFeeding(babyId: Long, feedingType: FeedingType, volume: Int = 0, timestampMillis: Long = System.currentTimeMillis()) {
        viewModelScope.launch { repository.addFeeding(babyId, feedingType, volume, timestampMillis) }
    }

    fun addMedicine(babyId: Long, note: String = "", timestampMillis: Long = System.currentTimeMillis()) {
        viewModelScope.launch { repository.addMedicine(babyId, note, timestampMillis) }
    }

    fun addHygiene(babyId: Long, note: String = "", timestampMillis: Long = System.currentTimeMillis()) {
        viewModelScope.launch { repository.addHygiene(babyId, note, timestampMillis) }
    }

    fun updateFeeding(record: FeedingRecord) {
        viewModelScope.launch { repository.updateFeeding(record) }
    }

    fun updateMedicine(record: MedicineRecord) {
        viewModelScope.launch { repository.updateMedicine(record) }
    }

    fun updateHygiene(record: HygieneRecord) {
        viewModelScope.launch { repository.updateHygiene(record) }
    }

    fun deleteFeeding(id: Long) {
        viewModelScope.launch { repository.deleteFeeding(id) }
    }

    fun deleteMedicine(id: Long) {
        viewModelScope.launch { repository.deleteMedicine(id) }
    }

    fun deleteHygiene(id: Long) {
        viewModelScope.launch { repository.deleteHygiene(id) }
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
