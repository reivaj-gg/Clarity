package com.reivaj.clarity.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reivaj.clarity.domain.model.AnalyticsSummary
import com.reivaj.clarity.domain.model.ProfileStats
import com.reivaj.clarity.domain.usecase.CalculateAnalyticsUseCase
import com.reivaj.clarity.domain.usecase.ExportDataUseCase
import com.reivaj.clarity.domain.usecase.GeneratePdfReportUseCase
import com.reivaj.clarity.domain.usecase.GetLast7DaysStatsUseCase
import com.reivaj.clarity.domain.usecase.GetProfileStatsUseCase
import com.reivaj.clarity.domain.util.DataSeeder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val dataSeeder: DataSeeder,
    private val getProfileStatsUseCase: GetProfileStatsUseCase,
    private val getLast7DaysStatsUseCase: GetLast7DaysStatsUseCase,
    private val exportDataUseCase: ExportDataUseCase,
    private val calculateAnalyticsUseCase: CalculateAnalyticsUseCase,
    private val generatePdfReportUseCase: GeneratePdfReportUseCase,
) : ViewModel() {

    private val _isSeeding = MutableStateFlow(false)
    val isSeeding = _isSeeding.asStateFlow()
    
    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    private val _profileStats = MutableStateFlow<ProfileStats?>(null)
    val profileStats = _profileStats.asStateFlow()

    private val _last7DaysData = MutableStateFlow<List<Pair<String, Int>>>(emptyList())
    val last7DaysData = _last7DaysData.asStateFlow()

    private val _analytics = MutableStateFlow<AnalyticsSummary?>(null)
    val analytics = _analytics.asStateFlow()

    private val _exportedData = MutableStateFlow<String?>(null)
    val exportedData = _exportedData.asStateFlow()

    private val _profilePictureUri = MutableStateFlow<String?>(null)
    val profilePictureUri = _profilePictureUri.asStateFlow()
    
    private val _pdfData = MutableStateFlow<ByteArray?>(null)
    val pdfData = _pdfData.asStateFlow()

    init {
        loadStats()
    }

    fun loadStats() {
        viewModelScope.launch {
            try {
                val stats = getProfileStatsUseCase()
                _profileStats.value = stats
                println("ProfileViewModel: Loaded stats - Sessions: ${stats.totalSessions}, EMAs: ${stats.totalEMAs}, Streak: ${stats.currentStreak}")
                
                val chartData = getLast7DaysStatsUseCase()
                _last7DaysData.value = chartData
                println("ProfileViewModel: Loaded chart data - ${chartData.size} days")
                
                // Load analytics
                val analyticsData = calculateAnalyticsUseCase()
                _analytics.value = analyticsData
                println("ProfileViewModel: Loaded analytics - ${analyticsData?.totalSessions ?: 0} sessions analyzed")
            } catch (e: Exception) {
                _message.value = "Error loading stats: ${e.message}"
                println("ProfileViewModel ERROR: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun seedData() {
        viewModelScope.launch {
            _isSeeding.value = true
            _message.value = "Generating synthetic history..."
            try {
                dataSeeder.seedData()
                _message.value = "Success! Generated 14 days of data."
                loadStats() // Refresh stats after seeding
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
            } finally {
                _isSeeding.value = false
            }
        }
    }

    fun exportData() {
        viewModelScope.launch {
            try {
                val jsonData = exportDataUseCase()
                _exportedData.value = jsonData
                _message.value = "Data exported! ${jsonData.length} characters"
            } catch (e: Exception) {
                _message.value = "Export failed: ${e.message}"
            }
        }
    }
    
    fun exportPdf() {
        viewModelScope.launch {
            try {
                _message.value = "Generating PDF report..."
                val pdf = generatePdfReportUseCase()
                _pdfData.value = pdf
                _message.value = "PDF generated! ${pdf.size} bytes. Ready to share."
            } catch (e: Exception) {
                _message.value = "PDF generation failed: ${e.message}"
                println("PDF generation error: ${e.message}")
                e.printStackTrace()
            }
        }
    }


    fun selectProfilePicture() {
        _message.value = "Profile picture selection - Coming soon!"
        // TODO: Implement platform-specific image picker
        // Android: Use ActivityResultContracts.PickVisualMedia()
        // iOS: Use PHPickerViewController
    }
    
    fun clearMessage() {
        _message.value = null
    }

    fun clearExportedData() {
        _exportedData.value = null
    }
}
