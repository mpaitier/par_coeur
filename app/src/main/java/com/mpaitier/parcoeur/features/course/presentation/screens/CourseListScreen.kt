package com.mpaitier.parcoeur.features.course.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mpaitier.parcoeur.core.theme.ParCoeurTheme

// Écran temporaire, juste pour vérifier que le projet tourne
@Composable
fun CourseListScreen() {
    Scaffold { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("Par cœur")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CourseListScreenPreview() {
    ParCoeurTheme { CourseListScreen() }
}
