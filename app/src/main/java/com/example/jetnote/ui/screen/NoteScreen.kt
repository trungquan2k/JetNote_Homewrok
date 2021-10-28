package com.example.jetnote.ui.screen



import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import com.example.jetnote.domain.model.NoteModel
import com.example.jetnote.routing.Screen
import com.example.jetnote.ui.components.Note
import com.example.jetnote.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import ui.components.*
@ExperimentalMaterialApi
@Composable
fun NotesScreen(viewModel: MainViewModel) {
    val notes: List<NoteModel> by viewModel
        .notesNotInTrash
        .observeAsState(listOf())
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                scaffoldState = scaffoldState,
                scope = coroutineScope
            )
        },
        drawerContent = {
            AppDrawer(currentScreen = Screen.Notes,
                closeDrawerAction = {
                    coroutineScope.launch {
                        scaffoldState.drawerState.close()
                    }
                }
            )
        },
        content = {
            if (notes.isNotEmpty()) {
                NotesList(
                    notes = notes,
                    onNoteCheckedChange = { viewModel.onNoteCheckedChange(it) },
                    onNoteClick = { viewModel.onNoteClick(it) }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.onCreateNewNoteClick()
                }
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add new data"
                )
            }
        },

        )
}
@ExperimentalMaterialApi
@Composable
private fun NotesList(
    notes: List<NoteModel>,
    onNoteCheckedChange: (NoteModel) -> Unit,
    onNoteClick: (NoteModel) -> Unit
) {
    LazyColumn{
        items(count = notes.size) { noteIndex ->
            val note = notes[noteIndex]
            Note(
                note = note,
                onNoteCheckedChange = onNoteCheckedChange,
                onNoteClick = onNoteClick,
                isSelected = false
            )

        }
    }
}

//@Composable
//private fun NoteList(
//    notes: List<NoteModel>,
//    onNoteCheckedChange: (NoteModel) -> Unit,
//    onNoteClick: (NoteModel) -> Unit,
//) {
//    LazyColumn {
////        items(notes) { item ->
////            Notes(item,onNoteClick = {
////                    onNoteClick
////                },
////                onNoteCheckedChange = {
////                    onNoteCheckedChange
////                }
////            )
////        }
//        items(count=notes.size) { itemIndex ->
//            val item = notes[itemIndex]
//            Notes(
//                item = item,
//                onNoteClick = onNoteClick,
//                onNoteCheckedChange = onNoteCheckedChange
//            )
//        }
//    }
//}

