package com.example.noteapplicationwithoutauth.feature_note.presentation.notes

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.noteapplicationwithoutauth.feature_note.domain.util.NoteOrder
import com.example.noteapplicationwithoutauth.feature_note.presentation.notes.components.NoteItem
import com.example.noteapplicationwithoutauth.feature_note.presentation.notes.components.OrderSection
import kotlinx.coroutines.launch
import java.lang.reflect.Modifier

@Composable
fun NotesScreen(
    navController: NavController,
    viewModel: NotesViewModel = hiltViewModel()
){
    val state = viewModel.state.value
    val scaffoldState = rememberScaffoldState()//snacks bars
    val scope = rememberCoroutineScope()

    Scaffold(

        floatingActionButton = {
            FloatingActionButton(
                onClick = {

                },
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add note")
            }
        },
        scaffoldState = scaffoldState
    ){
        Column(
        modifier = androidx.compose.ui.Modifier
            .fillMaxSize()
            .padding(paddingValues = it), // use "it" parameter
       
    ) {
            Row(
                modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Notes",
                    style = MaterialTheme.typography.h4,
                )
                IconButton (
                    onClick = {
                        viewModel.onEvent(NotesEvent.ToggleOrderSection)
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Sort"
                    )
                }
            }
            AnimatedVisibility(visible = state.isOrderSectionVisible,
                enter = fadeIn()+ slideInVertically(),
                exit = fadeOut()+ slideOutVertically()
                ) {
                    OrderSection(
                        modifier = androidx.compose.ui.Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        noteOrder = state.noteOrder,
                        onOrderChange = {
                            viewModel.onEvent(NotesEvent.Order(it))
                        }
                    )
            }
            Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
            LazyColumn(modifier = androidx.compose.ui.Modifier.fillMaxSize()){
                items(state.notes){note->
                    NoteItem(
                        note = note,
                        modifier = androidx.compose.ui.Modifier
                            .fillMaxWidth()
                            .clickable {

                            },
                        onDeleteClick = {
                            viewModel.onEvent(NotesEvent.DeleteNote(note))
                            scope.launch {
                                val result = scaffoldState.snackbarHostState.showSnackbar(
                                    message = "Note Deleted",
                                    actionLabel = "Undo"
                                )
                                if (result==SnackbarResult.ActionPerformed){
                                    viewModel.onEvent(NotesEvent.RestoreNote)
                                }
                            }
                        }
                    )
                    Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))

                }
            }
        }
    }
}