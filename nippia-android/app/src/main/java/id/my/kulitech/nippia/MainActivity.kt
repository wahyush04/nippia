package id.my.kulitech.nippia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import id.my.kulitech.nippia.ui.navigation.NippiaNavGraph
import id.my.kulitech.nippia.ui.theme.NippiaTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NippiaTheme {
                NippiaNavGraph()
            }
        }
    }
}
