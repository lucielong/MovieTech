package fr.epf.gestionclient.movietech.ui.recommendations

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import fr.epf.gestionclient.movietech.Movie
import fr.epf.gestionclient.movietech.MovieRecommendations
import fr.epf.gestionclient.movietech.R
import fr.epf.gestionclient.movietech.ui.home.MovieViewHolder

class RecommendationsViewHolder(val view: View) : RecyclerView.ViewHolder(view)

class RecommendationsAdapter(var movieRecommendations : List<MovieRecommendations>, val context: Context) : RecyclerView.Adapter<RecommendationsViewHolder>(){

    fun updateMovies(newMovies: List<MovieRecommendations>) {
        movieRecommendations = newMovies
        notifyDataSetChanged()}


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendationsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.movie_view, parent, false)
        return RecommendationsViewHolder(view)
    }

    override fun getItemCount() = movieRecommendations.size

    override fun onBindViewHolder(holder: RecommendationsViewHolder, position: Int) {
        val movieRecommendations: MovieRecommendations = movieRecommendations[position]
        val view = holder.itemView
        val textViewTitle = view.findViewById<TextView>(R.id.movie_view_title)
        if (movieRecommendations.title.length > 40) {
            textViewTitle.text = "${movieRecommendations.title.subSequence(0, 40)} ..."
        } else {
            textViewTitle.text = movieRecommendations.title
        }
        val imageView = view.findViewById<ImageView>(R.id.movie_view_imageview)
        if (movieRecommendations.poster_path != null) {
            val imageUrl = "https://image.tmdb.org/t/p/original" + movieRecommendations.poster_path
            Picasso.get().load(imageUrl).into(imageView)
        } else {
            imageView.setImageResource(R.drawable.baseline_local_movies_24)
        }
        val ratingBar = view.findViewById<RatingBar>(R.id.movie_view_ratingbar)
        ratingBar.rating = movieRecommendations.vote_average.toFloat() / 2
        val textViewDate = view.findViewById<TextView>(R.id.movie_view_datetext)
        if (movieRecommendations.release_date.length > 4) {
            textViewDate.text = movieRecommendations.release_date.subSequence(0, 4)
        } else {
            textViewDate.text = "N/A"
        }
        imageView.setOnClickListener {
            val navController = Navigation.findNavController(view)

            // get the movie id
            val movieId = movieRecommendations.id

            val bundle = bundleOf("movieId" to movieId)
            //view.findNavController().navigate(R.id.action_get_details, bundle)
            navController.navigate(R.id.action_get_details, bundle)
        }


    }
}