package co.kr.whitewave.presentation.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun WhiteWaveScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    backgroundColor: Color = Color.White,
    contentColor: Color = contentColorFor(backgroundColor),
    snackbarHost: @Composable () -> Unit = {},
    // content
    content: @Composable ColumnScope.(PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .safeDrawingPadding(),
        bottomBar = bottomBar,
        containerColor = backgroundColor,
        contentColor = contentColor,
        snackbarHost = snackbarHost
    ) { paddingValues ->
        Column {
            topBar()
            content(paddingValues)
        }
    }
}
