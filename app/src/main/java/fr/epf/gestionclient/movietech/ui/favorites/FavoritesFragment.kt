package fr.epf.gestionclient.movietech.ui.favorites
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.epf.gestionclient.movietech.Movie
import fr.epf.gestionclient.movietech.R
import fr.epf.gestionclient.movietech.SessionRequest
import fr.epf.gestionclient.movietech.TMDBService
import fr.epf.gestionclient.movietech.databinding.FragmentFavoritesBinding
import fr.epf.gestionclient.movietech.ui.home.MovieAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var movieAdapter: MovieAdapter
    private val apiKey = "6f195923c63346a8d0677974810d5255"
    private val accountId = 19649775
    private var accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI2ZjE5NTkyM2M2MzM0NmE4ZDA2Nzc5NzQ4MTBkNTI1NSIsInN1YiI6IjY0NmYyYzljODk0ZWQ2MDBiZjc3OTE2ZSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.gSdV8s24QLV2NYm4o_9_cvo6SfGCApmkXBIPT-Ui6Xw"

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
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        recyclerView = binding.RecyclerViewFavorites
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        movieAdapter = MovieAdapter(emptyList(), requireContext())
        recyclerView.adapter = movieAdapter
        getFavoriteMovies()
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    private fun getFavoriteMovies() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = withContext(Dispatchers.IO) {
                    tmdbService.getFavoriteMovies(accountId ,"Bearer $accessToken")
                }
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

    private fun updateMovieList(movies: List<Movie>) {
        movieAdapter.updateMovies(movies)
    }



}
