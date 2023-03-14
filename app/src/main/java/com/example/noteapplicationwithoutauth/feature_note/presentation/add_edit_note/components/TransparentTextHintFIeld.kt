package com.example.noteapplicationwithoutauth.feature_note.presentation.add_edit_note.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.text.TextStyle

@Composable
fun TransparentTextHintFIeld(
    text : String,
    hint : String,
    modifier: Modifier = Modifier,
    isHintVisible: Boolean = true,
    onValueChange:(String) -> Unit,
    textStyle: TextStyle = TextStyle(),
    singleLine : Boolean = false,
    onFocusChange: (FocusState)-> Unit
){
    Box(modifier = Modifier) {
        BasicTextField(
            value = text,
            onValueChange = onValueChange,
            singleLine = singleLine,
            textStyle = textStyle
        )
    }

}