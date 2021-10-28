package com.example.jetnote

import SaveNoteScreen
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import com.example.jetnote.routing.JetNotesRouter
import com.example.jetnote.routing.Screen
import com.example.jetnote.theme.JetNotesTheme
import com.example.jetnote.ui.screen.NotesScreen
import com.example.jetnote.ui.screen.TrashScreen

import com.example.jetnote.viewmodel.MainViewModel
import com.example.jetnote.viewmodel.MainViewModelFactory



/**
 * Main activity for the app.
 */

class MainActivity : AppCompatActivity() {

  private val viewModel: MainViewModel by viewModels(factoryProducer = {
    MainViewModelFactory(
      this,
      (application as JetNotesApplication).dependencyInjector.repository
    )
  })
  @ExperimentalMaterialApi
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent{
      JetNotesTheme {
        MainActivityScreen(viewModel=viewModel)
      }
    }
  }
}


@Composable
@ExperimentalMaterialApi
private fun MainActivityScreen(viewModel: MainViewModel){
  Surface{
    when(JetNotesRouter.currentScreen){
      is Screen.Notes -> NotesScreen(viewModel)
      is Screen.SaveNote -> SaveNoteScreen(viewModel)
      is Screen.Trash -> TrashScreen(viewModel)
    }
  }
}