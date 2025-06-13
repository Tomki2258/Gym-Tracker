
import com.tamusdev.gymtracker.data.ExerciseClass
import java.io.Serializable

class DayTrainingPlan(val day: String) : Serializable {
    val exercises = mutableListOf<ExerciseClass>()
}