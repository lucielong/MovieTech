package fr.epf.gestionclient.movietech.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.epf.gestionclient.movietech.Movie
import fr.epf.gestionclient.movietech.TMDBService
import fr.epf.gestionclient.movietech.databinding.FragmentSearchBinding
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchFragment : Fragment(){
    lateinit var recyclerView: RecyclerView
    lateinit var movieAdapter: MovieAdapter
    private var _binding: FragmentSearchBinding? = null
    val apiKey = "6f195923c63346a8d0677974810d5255"

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("accept", "application/json")
                .build()
            chain.proceed(request)
        }
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val TMDBService = retrofit.create(TMDBService::class.java)

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val binding = _binding ?: return null
        val root: View = binding.root
        recyclerView = binding.HomeMoviesRecycler
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        // Instantiate MovieAdapter with empty list of movies
        movieAdapter = MovieAdapter(emptyList(), requireContext())
        recyclerView.adapter = movieAdapter
        arguments?.let {
            val valeur = it.getString("query")
            synchro(valeur!!)
        }
        return root
    }

    private fun updateMovieList(movies: List<Movie>) {
        movieAdapter.updateMovies(movies)
    }


    private fun synchro(valeur: String) {


        runBlocking {
            try {
                var response = TMDBService.getSearchMovies(apiKey, "en-US", valeur)
                if (response.isSuccessful) {
                    val movieResponse = response.body()
                    val movies = movieResponse?.results ?: emptyList()

                    updateMovieList(movies)
                } else {
                    Log.e("TAG", "Request failed: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("TAG", "Error: ${e.message}")
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
