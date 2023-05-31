package fr.epf.gestionclient.movietech.ui.details

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import fr.epf.gestionclient.movietech.Movie
import fr.epf.gestionclient.movietech.MovieDetails
import fr.epf.gestionclient.movietech.R
import fr.epf.gestionclient.movietech.TMDBService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MovieDetailsFragment : Fragment() {

    // private lateinit
    private lateinit var textViewTitle: TextView
    private lateinit var textViewGenre: TextView
    private lateinit var textViewRuntime: TextView
    private lateinit var textViewReleaseDate: TextView
    private lateinit var textViewAverageRating: TextView
    private lateinit var textViewOverview: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var affiche: ImageView



    fun fetchMovieDetails(movieID: Int) {

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
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        val TMDBService = retrofit.create(TMDBService::class.java)

        //val movieID = requireArguments().getInt("movieId")

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    TMDBService.getMovieDetails(movieID, apiKey, "en-US", "credits")
                }
                if (response.isSuccessful) {
                    val movieDetails = response.body()
                    textViewTitle.text = movieDetails?.title
                    textViewRuntime.text = "${movieDetails?.runtime} minutes"
                    textViewOverview.text = movieDetails?.overview
                    textViewReleaseDate?.text = movieDetails?.release_date
                    ratingBar.rating = movieDetails?.vote_average?.toFloat()?.div(2) ?: 0f
                    //affiche.setImageResource(R.drawable.ic_launcher_background)
                } else {
                    Log.e("TAG", "Request failed: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("TAG", "Error: ${e.message}")
            }
        }
    }

        /*runBlocking {
            try {
                val response = TMDBService.getMovieDetails(movieID, apiKey, "en-US", "credits")
                if (response.isSuccessful) {
                    val movieDetails = response.body()


                    //if (movieDetails != null) {
                    textViewTitle?.text = movieDetails?.title
                    //textViewGenre?.text = movieDetails.genres.joinToString { it.name }
                    textViewRuntime?.text = "${movieDetails?.runtime} minutes"
                    textViewOverview?.text = movieDetails?.overview
                    textViewReleaseDate?.text = movieDetails?.release_date
                    //ratingBar?.rating = movieDetails?.vote_average?.toFloat() / 2
                    ratingBar?.rating = movieDetails?.vote_average?.toFloat()?.div(2) ?: 0f
                    affiche?.setImageResource(R.drawable.ic_launcher_background)
                    //}
                } else {
                    Log.e("TAG", "Request failed: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("TAG", "Error: ${e.message}")
            }

        }

    }*/

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            // Inflater la vue à partir du fichier XML du fragment
            val view = inflater.inflate(R.layout.fragment_movie_details, container, false)

            // Initiate the views
            textViewTitle = view.findViewById<TextView>(R.id.details_movie_title)

            textViewGenre = view.findViewById<TextView>(R.id.details_genre)
            textViewRuntime = view.findViewById<TextView>(R.id.details_runtime)
            textViewReleaseDate = view.findViewById<TextView>(R.id.details_release_date)
            textViewAverageRating = view.findViewById<TextView>(R.id.details_average_rating)
            textViewOverview = view.findViewById<TextView>(R.id.details_overview_body)
            ratingBar = view.findViewById<RatingBar>(R.id.details_ratingbar)
            affiche = view.findViewById<ImageView>(R.id.details_imageview)

            val movieID = requireArguments().getInt("movieId")

            fetchMovieDetails(movieID)

            return view

        }
}


