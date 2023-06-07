package fr.epf.gestionclient.movietech.ui.details

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.squareup.picasso.Picasso
import fr.epf.gestionclient.movietech.FavoriteBody
import fr.epf.gestionclient.movietech.MovieDetails
import fr.epf.gestionclient.movietech.MovieResponse
import fr.epf.gestionclient.movietech.R
import fr.epf.gestionclient.movietech.TMDBService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
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
    private lateinit var favbutton: ImageButton
    private lateinit var favoritebody: FavoriteBody

    val apiKey = "6f195923c63346a8d0677974810d5255"
    private val accountId = 19649775
    private var accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI2ZjE5NTkyM2M2MzM0NmE4ZDA2Nzc5NzQ4MTBkNTI1NSIsInN1YiI6IjY0NmYyYzljODk0ZWQ2MDBiZjc3OTE2ZSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.gSdV8s24QLV2NYm4o_9_cvo6SfGCApmkXBIPT-Ui6Xw"


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
        favbutton = view.findViewById<ImageButton>(R.id.details_fav_button)

        val movieID = requireArguments().getInt("movieId")
        lifecycleScope.launch { synchro(movieID) }

        val buttonRecommendations = view.findViewById<Button>(R.id.recommendations_button)
        buttonRecommendations.setOnClickListener {
            val navController = Navigation.findNavController(view)

            val bundle = bundleOf("movieId" to movieID)
            navController.navigate(R.id.action_get_recommendations, bundle)

        }

        return view

    }

    private suspend fun getMovieDetails(movieID: Int): MovieDetails? {
        return withContext(Dispatchers.IO) {
            val response = withContext(Dispatchers.IO) {
                TMDBService.getMovieDetails(movieID, apiKey, "en-US", "credits")
            }
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("TAG", "Request failed: ${response.code()} - ${response.message()}")
                null
            }
        }
    }


    private fun synchro(movieID: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            val movieDetails = getMovieDetails(movieID)
            textViewTitle.text = movieDetails?.title
            textViewRuntime.text = "${movieDetails?.runtime} min"
            textViewOverview.text = movieDetails?.overview
            textViewReleaseDate.text = movieDetails?.release_date
            ratingBar.rating = movieDetails?.vote_average?.toFloat()?.div(2) ?: 0f
            val posterUrl = "https://image.tmdb.org/t/p/original" + movieDetails?.poster_path
            Picasso.get()
                .load(posterUrl)
                .into(affiche)
            checkFavorites(movieID)
            favbutton.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch {
                    addOrRemoveFavorite(movieID)
                }
            }
        }
    }

    private suspend fun addOrRemoveFavorite(movieID: Int){
        val favoriteMovies = getFavoriteMovies(movieID)
        val movie = favoriteMovies?.results?.find { it.id == movieID }
        if(movie != null){
            favoritebody = FavoriteBody("movie", movieID, false)
            addFavoriteMovies(favoritebody)
            favbutton.setBackgroundResource(R.drawable.baseline_favorite_border_24)
        } else{
            favoritebody = FavoriteBody("movie", movieID, true)
            addFavoriteMovies(favoritebody)
            favbutton.setBackgroundResource(R.drawable.baseline_favorite_24)
        }
    }

    private suspend fun checkFavorites(movieID: Int){
        val favoriteMovies = getFavoriteMovies(movieID)
        val movie = favoriteMovies?.results?.find { it.id == movieID }
        if(movie != null){ favbutton.setBackgroundResource(R.drawable.baseline_favorite_24) }
        else{favbutton.setBackgroundResource(R.drawable.baseline_favorite_border_24)}
    }


    private fun addFavoriteMovies(favoritebody : FavoriteBody) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                withContext(Dispatchers.IO) {
                    TMDBService.addFavoriteMovies(accountId ,"Bearer $accessToken", favoritebody)
                }
            } catch (e: Exception) {
                Log.e("TAG", "Error: ${e.message}")
            }
        }
    }

    private suspend fun getFavoriteMovies(movieID: Int) : MovieResponse? {
        return withContext(Dispatchers.IO) {
            val response = TMDBService.getFavoriteMovies(accountId, "Bearer $accessToken")
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("TAG", "Request failed: ${response.code()} - ${response.message()}")
                null
            }
        }
    }


}

