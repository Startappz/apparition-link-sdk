package link.apparition.android.sample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    content: String,
    appContent: @Composable () -> Unit,
) {
    var openBottomSheet by rememberSaveable(content) { mutableStateOf(content.isNotEmpty()) }
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState()
    val toggleSheet = {
        openBottomSheet = !openBottomSheet
    }

    Box(modifier = modifier) {
        appContent()

        // Sheet content
        if (openBottomSheet) {
            BottomSheet(
                bottomSheetState = bottomSheetState,
                onDismiss = { toggleSheet() },
                content = content,
            )
        }
    }

}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun BottomSheet(
    modifier: Modifier = Modifier,
    bottomSheetState: SheetState,
    content: String,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = bottomSheetState,
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = content,
            )
        }
    }
}
