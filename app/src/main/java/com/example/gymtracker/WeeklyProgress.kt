import android.util.Log
import com.example.gymtracker.Measurement
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class WeeklyProgress(
    val measurements: List<Measurement>,
    val weekNumber: Int,
    val lastWeek: WeeklyProgress? = null
) {
    val year: Int
    val avgWeight: Float = setAvgWeight()
    val avgWeightDifference: Float = calculateAvgWeightDifference()
    val firstDate: Long = measurements[0].date.time
    val weekRange: String = calculateWeekRange()

    init {
        this.year = measurements[0].date.year + 1900
    }

    private fun setAvgWeight(): Float {
        var totalWeight = 0.0f
        var totalReps = 0
        for (measurement in measurements) {
            totalWeight += measurement.weight * measurement.reps
            totalReps += measurement.reps
        }
        return totalWeight / totalReps
    }

    private fun calculateAvgWeightDifference(): Float {
        Log.d("Current Week $weekNumber with value $avgWeight", "Last Week ${lastWeek?.weekNumber} with value ${lastWeek?.avgWeight}")
        if (lastWeek == null) {
            return 0.0f
        }
        return avgWeight - lastWeek.avgWeight
    }

    private fun calculateWeekRange(): String {
        val calendar = Calendar.getInstance()
        calendar.time = measurements[0].date
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek + 1)
        val startDate = calendar.time
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        val endDate = calendar.time
        val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
        return "${sdf.format(startDate)} - ${sdf.format(endDate)}"
    }
}