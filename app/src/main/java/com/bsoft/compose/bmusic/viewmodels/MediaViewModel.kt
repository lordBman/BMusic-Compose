package com.bsoft.compose.bmusic.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bsoft.compose.bmusic.data.entities.FavouriteEntity
import com.bsoft.compose.bmusic.data.models.RequestState
import com.bsoft.compose.bmusic.data.repositories.FavouriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(private val favouriteRepository: FavouriteRepository) : ViewModel() {
    private val _favorites = MutableStateFlow<RequestState<List<FavouriteEntity>>>(RequestState.Idle)
    val favorites: StateFlow<RequestState<List<FavouriteEntity>>> = _favorites.asStateFlow()

    init {
        viewModelScope.launch {
            favouriteRepository.getAllFavorites()
                .onStart { _favorites.value = RequestState.Loading }
                .onCompletion {  }
                .collect{ favoritesList->
                    _favorites.value = RequestState.Success(favoritesList)
                }
        }
    }
}