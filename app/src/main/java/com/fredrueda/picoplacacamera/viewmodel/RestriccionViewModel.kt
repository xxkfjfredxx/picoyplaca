package com.fredrueda.picoplacacamera.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredrueda.picoplacacamera.model.Restriccion
import com.fredrueda.picoplacacamera.repository.RestriccionRepository
import kotlinx.coroutines.launch

class RestriccionViewModel : ViewModel() {

    private val repository = RestriccionRepository()

    private val _restriccion = MutableLiveData<Restriccion>()
    val restriccion: LiveData<Restriccion> get() = _restriccion

    fun verificarPlaca(ciudad: String, tipo: String, placa: String) {
        viewModelScope.launch {
            val resultado = repository.verificarRestriccion(ciudad, tipo, placa)
            _restriccion.postValue(resultado)
        }
    }
}