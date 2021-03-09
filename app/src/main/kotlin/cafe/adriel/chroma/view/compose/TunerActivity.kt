package cafe.adriel.chroma.view.compose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import cafe.adriel.chroma.model.TuningDeviationPrecision
import org.koin.androidx.scope.activityScope

class TunerActivity : AppCompatActivity() {

    private val screen by activityScope().inject<TunerScreen>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { screen.Content() }
    }
}
