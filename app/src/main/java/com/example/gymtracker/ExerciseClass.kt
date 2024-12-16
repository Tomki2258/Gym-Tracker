import android.content.Context
import java.io.Serializable

class ExerciseClass(
    val name: String,
    val category: String,
    val photoString: String = name.replace(" ", "_").lowercase()
) : Serializable
{
    fun getPhotoResourceId(context: Context): Int {
        return context.resources.getIdentifier(photoString, "drawable", context.packageName)
    }
}
