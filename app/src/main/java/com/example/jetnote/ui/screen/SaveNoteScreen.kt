import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview


import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetnote.R
import com.example.jetnote.domain.model.ColorModel
import com.example.jetnote.domain.model.NEW_NOTE_ID
import com.example.jetnote.domain.model.NoteModel
import com.example.jetnote.routing.JetNotesRouter
import com.example.jetnote.routing.Screen

import com.example.jetnote.util.fromHex


import com.example.jetnote.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import ui.components.ColorItem
import ui.components.NoteColor


@ExperimentalMaterialApi
@Composable
fun SaveNoteScreen(viewModel: MainViewModel) {
    val noteEntry: NoteModel by viewModel.noteEntry.observeAsState(NoteModel())
    val colors: List<ColorModel> by viewModel.colors.observeAsState(listOf())
    val bottomDrawerState: BottomDrawerState = rememberBottomDrawerState(BottomDrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val moveNoteToTrashDialogShowState: MutableState<Boolean> = rememberSaveable {
        mutableStateOf(false)
    }

    BackHandler(onBack = {
        if(bottomDrawerState.isOpen){
            coroutineScope.launch{bottomDrawerState.isClosed}
        }else{
            JetNotesRouter.navigateTo(Screen.Notes)
        }
    })
    Scaffold(
        topBar = {
            val isEditingMode: Boolean = noteEntry.id != NEW_NOTE_ID
            SaveNoteTopAppBar(
                isEditingMode = isEditingMode,
                onBackClick = { JetNotesRouter.navigateTo(Screen.Notes) },
                onSaveNoteClick = { viewModel.saveNote(noteEntry) },
                onOpenColorPicker = {
                    coroutineScope.launch { bottomDrawerState.open() }
                },
                onDeleteNoteClick = { moveNoteToTrashDialogShowState.value = true }
            )
        },
        content = {
            BottomDrawer(
                drawerState = bottomDrawerState,
                drawerContent = {
                    ColorPicker(
                        colors = colors,
                        onColorSelect = { color ->
                            val newNoteEntry = noteEntry.copy(color = color)
                            viewModel.onNoteEntryChange(newNoteEntry)
                        }
                    )
                },
                content = {
                    SaveNoteContent(
                        note = noteEntry,
                        onNoteChange = { updateNoteEntry ->
                            viewModel.onNoteEntryChange(updateNoteEntry)
                        }
                    )
                }
            )

            if (moveNoteToTrashDialogShowState.value) {
                AlertDialog(
                    onDismissRequest = {
                        moveNoteToTrashDialogShowState.value = false
                    },
                    title = {
                        Text(text = "Move note to the trash?")
                    },
                    text = {
                        Text(
                            text = "Are you sure you want to" +
                                    "move this note to the trash"
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = { viewModel.moveNoteToTrash(noteEntry) }) {
                            Text(text = "Confirm")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { moveNoteToTrashDialogShowState.value = false }) {
                            Text(text = "Dismiss")
                        }
                    }
                )
            }
        }
    )
}


@Composable
private fun SaveNoteContent(
    note: NoteModel,
    onNoteChange: (NoteModel) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        ContentTextField(
            label = "Title",
            text = note.title,
            onTextChange = { newTitle ->
                onNoteChange.invoke(note.copy(title = newTitle))
            }
        )

        ContentTextField(
            modifier = Modifier
                .heightIn(max = 240.dp)
                .padding(top = 16.dp),
            label = "Body",
            text = note.content,
            onTextChange = { newContent ->
                onNoteChange.invoke(note.copy(content = newContent))
            }
        )


        val canBeCheckedOff:Boolean = note.isCheckedOff !=null

        NoteCheckOption(
            isChecked = canBeCheckedOff ,
            onCheckedChange = { canBeCheckedOffNewValue ->
                val isCheckedOff: Boolean? = if (canBeCheckedOffNewValue) false else null

                onNoteChange.invoke(note.copy(isCheckedOff = isCheckedOff))
            }
        )
        PickedColor(color = note.color)
    }
}



@Composable
private fun SaveNoteTopAppBar(
    isEditingMode: Boolean,
    onBackClick: () -> Unit,
    onSaveNoteClick: () -> Unit,
    onOpenColorPicker: () -> Unit,
    onDeleteNoteClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(text = "Save Note")
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Save note button",
                    tint= MaterialTheme.colors.onPrimary
                )
            }
        },
        actions = {
            // Action save Note
            IconButton(onClick = onSaveNoteClick) {
                Icon(
                    Icons.Filled.Done,
                    contentDescription = "Save note",
                    tint = MaterialTheme.colors.onPrimary
                )
            }
            // Action select ColorPicker
            IconButton(onClick = onOpenColorPicker) {
                Icon(
                    painter = painterResource(
                        id = R.drawable.ic_baseline_color_lens_24
                    ),
                    contentDescription = "Text",
                    tint = MaterialTheme.colors.onPrimary
                )
            }
//            Action delete
            if(isEditingMode){
                IconButton(onClick = onDeleteNoteClick) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete Note Button",
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
            }
        },
        elevation = AppBarDefaults.TopAppBarElevation
    )
}


@Composable
fun ColorPicker(
    colors: List<ColorModel>,
    onColorSelect: (ColorModel) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Color Picker",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(colors.size) { itemIndex ->
                val color = colors[itemIndex]
                ColorItem(
                    color = color,
                    onColorSelect = onColorSelect
                )
            }
        }
    }
}


@Composable
private fun PickedColor(color: ColorModel)
{
    Row(
        Modifier
            .padding(8.dp)
            .padding(top = 16.dp)
    ){
        Text(
            text = "Picked color",
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        )
        NoteColor(
            color = Color.fromHex(color.hex),
            size = 40.dp,
            border = 1.dp,
            modifier = Modifier.padding(4.dp)
            )
    }
}



@Composable
private fun ContentTextField(
    modifier: Modifier = Modifier,
    label: String,
    text: String,
    onTextChange: (String) -> Unit
) {
    TextField(
        value = text,
        onValueChange = onTextChange,
        label = { Text(label) },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colors.surface
        )
    )
}

@Preview
@Composable
fun ContentTextFiekdPreview() {
    ContentTextField(
        label = "Title",
        text = "",
        onTextChange = {}
    )
}



@Composable
private fun NoteCheckOption(
    isChecked : Boolean,
    onCheckedChange :(Boolean) ->Unit
) {
    Row(
        Modifier
            .padding(8.dp)
            .padding(top = 16.dp)
    ) {
        Text(
            text = "Can not be checked off",
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
                .align(Alignment.CenterVertically)
        )
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .align(alignment = Alignment.CenterVertically)
        )
    }
}
