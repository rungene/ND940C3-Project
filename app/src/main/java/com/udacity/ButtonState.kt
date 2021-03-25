package com.udacity


sealed class ButtonState(var customTextButton: Int) {
    object Clicked : ButtonState(R.string.button_download)
    object Loading : ButtonState(R.string.button_loading)
    object Completed : ButtonState(R.string.button_complete)
}