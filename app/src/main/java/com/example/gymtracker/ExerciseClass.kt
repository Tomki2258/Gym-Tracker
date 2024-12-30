import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableIntState
import com.example.gymtracker.MeasurementClass
import java.io.Serializable
class ExerciseClass(
    val name: String,
    val category: String,
    val photoString: String = name.replace(" ", "_").lowercase(),
    val measurementsList: MutableList<MeasurementClass> = mutableListOf<MeasurementClass>()
) : Serializable
{
    fun getPhotoResourceId(context: Context): Int {
        return context.resources.getIdentifier(photoString, "drawable", context.packageName)
    }

    fun printList() {
        for (measurement in measurementsList) {
            Log.d("Reps: ${measurement.reps}", "Weight: ${measurement.weight}")
        }
    }
}
