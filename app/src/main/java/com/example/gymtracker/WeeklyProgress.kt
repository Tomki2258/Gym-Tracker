import android.util.Log
import com.example.gymtracker.Measurement

class WeeklyProgress(
    val measurements: List<Measurement>,
    val weekNumber: Int,
    val lastWeek: WeeklyProgress? = null
) {
    val year: Int
    val avgWeight: Float = setAvgWeight()
    val avgWeightDifference: Float = calculateAvgWeightDifference()
    val firstDate: Long = measurements[0].date.time

    init {
        this.year = measurements[0].date.year + 1900
        Log.d("Week number: $weekNumber", measurements.size.toString())
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
        return if (lastWeek != null) {
            this.avgWeight - lastWeek.avgWeight
        } else {
            0.0f
        }
    }
}