package com.plcoding.testingwithktor.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.testingwithktor.domain.Product
import com.plcoding.testingwithktor.domain.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class ProductsViewModel(
    private val repository: ProductRepository
): ViewModel() {

    private var hasLoadedProducts = false

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products
        .onStart {
            if(!hasLoadedProducts) {
                _products.value = repository.getProducts()
                hasLoadedProducts = true
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _products.value
        )

}