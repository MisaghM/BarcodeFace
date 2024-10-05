package com.misana.barcodeface.presentation.drawer

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DrawerViewModel : ViewModel() {
    var drawerState: DrawerState by mutableStateOf(DrawerState(DrawerValue.Closed))
        private set

    var drawerSelected: DrawerItem.Index by mutableStateOf(DrawerItem.Index.HOME)
        private set

    private var _exitRequest = Channel<Boolean>()
    val exitRequest = _exitRequest.receiveAsFlow()

    fun sendExitRequest() {
        viewModelScope.launch {
            _exitRequest.send(true)
        }
    }

    fun switchDrawer(uiScope: CoroutineScope) {
        viewModelScope.launch {
            withContext(uiScope.coroutineContext) {
                drawerState.apply {
                    if (isClosed) open() else close()
                }
            }
        }
    }

    fun setDrawerSelect(index: DrawerItem.Index) {
        drawerSelected = index
    }

    fun reset() {
        drawerState = DrawerState(DrawerValue.Closed)
        drawerSelected = DrawerItem.Index.HOME
    }
}
