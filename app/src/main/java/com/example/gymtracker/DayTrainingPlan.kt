
import com.example.gymtracker.ExerciseClass
import java.io.Serializable

class DayTrainingPlan(val day: String) : Serializable {
    val exercises = mutableListOf<ExerciseClass>()
}