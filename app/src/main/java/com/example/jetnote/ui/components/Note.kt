package com.example.jetnote.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetnote.domain.model.NoteModel
import com.example.jetnote.util.fromHex
import ui.components.NoteColor


//@Composable
//fun Note(
//    note: NoteModel,
//    onNoteClick: (NoteModel) ->Unit={},
//    onNoteCheckedChange: (NoteModel)->Unit={}
//){
//
//    val backgroundShape: Shape = RoundedCornerShape(4.dp)
//    Row(
//        modifier= Modifier
//            .padding(8.dp)
//            .shadow(1.dp, backgroundShape)
//            .fillMaxWidth()
//            .heightIn(min = 64.dp)
//            .background(Color.White, backgroundShape)
//            .clickable(onClick = { onNoteClick(note) }),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        NoteColor(
//            modifier= Modifier
//                .align(Alignment.CenterVertically)
//                .padding(4.dp),
//            color = Color.fromHex(note.color.hex),
//            size = 40.dp,
//            border = 1.dp
//        )
//        Column(
//            modifier= Modifier
//                .weight(1f)
//                .align(Alignment.CenterVertically)
//        ) {
//
//            Text( text=note.title, maxLines = 1)
//            Text(text=note.content, maxLines = 1)
//        }
//
//        if (note.isCheckedOff !=null){
//            Checkbox(
//                checked = note.isCheckedOff,
//                onCheckedChange = { isChecked ->
//                    var newNote = note.copy(isCheckedOff = isChecked)
//                    onNoteCheckedChange(newNote)
//                },
//                modifier = Modifier
//                    .padding(start = 16.dp)
//                    .align(Alignment.CenterVertically)
//            )
//        }
//    }
//}
//@Preview(showBackground = true)
//@Composable
//fun NotePreview(){
//    Note(note= NoteModel(1, "Note 1", "Content 1", null))
//}

@ExperimentalMaterialApi
@Composable
fun Note(
    modifier: Modifier = Modifier,
    note: NoteModel,
    onNoteClick: (NoteModel) -> Unit = {},
    onNoteCheckedChange: (NoteModel) -> Unit = {},
    isSelected: Boolean
) {
    val background = if (isSelected)
        Color.LightGray
    else
        MaterialTheme.colors.surface

    Card(
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        backgroundColor = background
    ) {
        ListItem(
            text = { Text(text = note.title, maxLines = 1) },
            secondaryText = {
                Text(text = note.content, maxLines = 1)
            },
            icon = {
                NoteColor(
                    color = Color.fromHex(note.color.hex),
                    size = 40.dp,
                    border = 1.dp
                )
            },
            trailing = {
                if (note.isCheckedOff != null) {
                    Checkbox(
                        checked = note.isCheckedOff,
                        onCheckedChange = { isChecked ->
                            var newNote = note.copy(isCheckedOff = isChecked)
                            onNoteCheckedChange(newNote)
                        },
                        modifier = Modifier
                            .padding(start = 16.dp)
                    )
                }
            },
            modifier = Modifier.clickable {
                onNoteClick.invoke(note)
            }
        )
    }
}