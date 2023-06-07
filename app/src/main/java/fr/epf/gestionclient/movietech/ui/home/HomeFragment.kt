package fr.epf.gestionclient.movietech.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.epf.gestionclient.movietech.Movie
import fr.epf.gestionclient.movietech.MovieResponse
import fr.epf.gestionclient.movietech.R
import fr.epf.gestionclient.movietech.TMDBService
import fr.epf.gestionclient.movietech.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var movieAdapter: MovieAdapter
    private val apiKey = "6f195923c63346a8d0677974810d5255"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("accept", "application/json")
                .build()
            chain.proceed(request)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val tmdbService = retrofit.create(TMDBService::class.java)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerView = binding.HomeMoviesRecycler
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        movieAdapter = MovieAdapter(emptyList(), requireContext())
        recyclerView.adapter = movieAdapter

        binding.HomeMoviesRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            synchro()
        }

        binding.HomeSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val bundle = Bundle()
                bundle.putString("query", query)
                findNavController().navigate(R.id.action_search, bundle)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        binding.HomeQrcodeButton.setOnClickListener {
            findNavController().navigate(R.id.action_qrcode)
        }

        synchro()
        return root
    }

    private fun updateMovieList(movies: List<Movie>) {
        movieAdapter.updateMovies(movies)
    }

    private suspend fun getMoviesByCategory(category: suspend () -> Response<MovieResponse>): List<Movie> {
        return withContext(Dispatchers.IO) {
            try {
                val response = category.invoke()
                if (response.isSuccessful) {
                    val movieResponse = response.body()
                    movieResponse?.results ?: emptyList()
                } else {
                    Log.e("TAG", "Request failed: ${response.code()} - ${response.message()}")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("TAG", "Error: ${e.message}")
                emptyList()
            }
        }
    }



    private suspend fun getPopularMovies(): List<Movie> {
        return getMoviesByCategory { tmdbService.getPopularMovies(apiKey, "en-US", 1) }
    }

    private suspend fun getTopRatedMovies(): List<Movie> {
        return getMoviesByCategory { tmdbService.getTopRatedMovies(apiKey, "en-US", 1) }
    }

    private suspend fun getUpcomingMovies(): List<Movie> {
        return getMoviesByCategory { tmdbService.getUpcomingMovies(apiKey, "en-US", 1) }
    }

    private suspend fun getNowPlayingMovies(): List<Movie> {
        return getMoviesByCategory { tmdbService.getNowPlayingMovies(apiKey, "en-US", 1) }
    }

    private fun synchro() {
        val pageNumber = 1
        CoroutineScope(Dispatchers.Main).launch {
            val movies = when (binding.HomeMoviesRadioGroup.checkedRadioButtonId) {
                R.id.Home_movies_popular -> getPopularMovies()
                R.id.Home_movies_top_rated -> getTopRatedMovies()
                R.id.Home_movies_upcoming -> getUpcomingMovies()
                R.id.Home_movies_nowPlaying -> getNowPlayingMovies()
                else -> emptyList()
            }
            updateMovieList(movies)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
