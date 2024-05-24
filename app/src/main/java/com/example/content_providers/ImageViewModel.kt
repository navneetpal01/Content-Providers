package com.example.content_providers

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope


class ImageViewModel : ViewModel(){

    var images by mutableStateOf(emptyList<Image>())
        private set



    fun updateImages(images: List<Image>){
        this.images = images
    }



}


