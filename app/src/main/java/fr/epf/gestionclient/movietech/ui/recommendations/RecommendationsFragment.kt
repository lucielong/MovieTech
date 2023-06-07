package fr.epf.gestionclient.movietech.ui.recommendations

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.epf.gestionclient.movietech.Movie
import fr.epf.gestionclient.movietech.MovieRecommendations
import fr.epf.gestionclient.movietech.R
import fr.epf.gestionclient.movietech.RecommendationsResponse
import fr.epf.gestionclient.movietech.TMDBService
import fr.epf.gestionclient.movietech.databinding.FragmentHomeBinding
import fr.epf.gestionclient.movietech.databinding.FragmentRecommendationsBinding
import fr.epf.gestionclient.movietech.ui.home.MovieAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RecommendationsFragment : Fragment() {

    private var _binding: FragmentRecommendationsBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var recommendationsAdapter: RecommendationsAdapter
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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //return inflater.inflate(R.layout.fragment_recommendations, container, false)
        _binding = FragmentRecommendationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerView = binding.RecommendationsMoviesRecycler
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        recommendationsAdapter = RecommendationsAdapter(emptyList(), requireContext())
        recyclerView.adapter = recommendationsAdapter

        synchro()
        return root
    }

    private suspend fun getRecommendations(): Response<RecommendationsResponse> {
        val movieID = requireArguments().getInt("movieId")
        return tmdbService.getRecommendations(movieID, apiKey, "en-US", 1)
    }

    private fun synchro() {
        val pageNumber = 1
        CoroutineScope(Dispatchers.Main).launch {
            val response = getRecommendations()
            if (response.isSuccessful) {
                val movieRecommendations = response.body()?.results ?: emptyList()
                updateMovieList(movieRecommendations)
                //val movieRecommendations = getRecommendations()

                //updateMovieList(movieRecommendations)
            }
        }
    }

        private fun updateMovieList(movieRecommendations: List<MovieRecommendations>) {
            recommendationsAdapter.updateMovies(movieRecommendations)
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }

}