package fr.epf.gestionclient.movietech.ui.home

import android.R
import android.view.Gravity
import android.widget.SearchView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class HomeViewModel : ViewModel() {


    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"

    }

    val text: LiveData<String> = _text
}