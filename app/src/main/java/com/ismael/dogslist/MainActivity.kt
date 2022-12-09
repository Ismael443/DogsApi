package com.ismael.dogslist

import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ismael.dogslist.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity(), androidx.appcompat.widget.SearchView.OnQueryTextListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: DogAdapter
    private val dogImages = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.searchViewDogs.setOnQueryTextListener(this)


        initRecyclerview()
    }

    private fun initRecyclerview() {
        adapter = DogAdapter(dogImages)
        binding.RecyclerViewDogs.layoutManager = LinearLayoutManager(this)
        binding.RecyclerViewDogs.adapter = adapter


    }

    //Creamos el Retrofit
    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://dog.ceo/api/breed/")
            .addConverterFactory(GsonConverterFactory.create()) //Libreria para la conversión del JSON
            .build()
    }

    private fun searchByName(raza: String){
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(ApiService::class.java).getDogsByBreeds("$raza/images")
            val puppies = call.body() //Aqui es donde esta la respuesta

            runOnUiThread {

                if(call.isSuccessful){ //Comprobamos si ha funcionado la llamada
                    //Mostramos las imagenes en el RecyclerView
                    val images = puppies?.images ?: emptyList() //Si devuelve null, le digo que devuelva una lista vacia
                    dogImages.clear()//Limpiamos todo
                    dogImages.addAll(images) //Añadimos las imagenes
                    adapter.notifyDataSetChanged() //Avisamos al adapter
                }else{
                    //Mostramos un error
                    showError()
                }

                closeKey()
            }


        }

    }

    //Funcion para ocultar el teclado del buscador cuando pulsemos enter
    private fun closeKey() {
        val inm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inm.hideSoftInputFromWindow(binding.viewRoot.windowToken, 0)
    }

    private fun showError() {
        Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show()
    }

    override fun onQueryTextSubmit(raza: String?): Boolean { //Cuando le demos en buscar, llamamos a este metodo
        if(!raza.isNullOrEmpty()){ //Si el texto que se escribe no esta vacio ni es nulo
            searchByName(raza.lowercase())
        }

        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }
}