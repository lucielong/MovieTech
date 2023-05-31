package fr.epf.gestionclient.movietech.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import fr.epf.gestionclient.movietech.Movie
import fr.epf.gestionclient.movietech.R

class MovieViewHolder(val view: View) : RecyclerView.ViewHolder(view)

class MovieAdapter(var movies: List<Movie>, val context: Context) : RecyclerView.Adapter<MovieViewHolder>() {



    fun updateMovies(newMovies: List<Movie>) {
        movies = newMovies
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.movie_view, parent, false)
        return MovieViewHolder(view)
    }

    override fun getItemCount() = movies.size

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie : Movie = movies[position]
        val view = holder.itemView
        val textViewTitle = view.findViewById<TextView>(R.id.movie_view_title)
        textViewTitle.text = movie.title
        val imageView = view.findViewById<ImageView>(R.id.movie_view_imageview)
        if (movie.poster_path != null){
        val imageUrl = "https://image.tmdb.org/t/p/original" + movie.poster_path
        Picasso.get().load(imageUrl).into(imageView)}
        else{
            imageView.setImageResource(R.drawable.baseline_local_movies_24)
        }
        val ratingBar = view.findViewById<RatingBar>(R.id.movie_view_ratingbar)
        ratingBar.rating = movie.vote_average.toFloat()/2
        val textViewDate = view.findViewById<TextView>(R.id.movie_view_datetext)
        if (movie.release_date.length > 4){
        textViewDate.text = movie.release_date.subSequence(0,4)}
        else{
            textViewDate.text = "N/A"
        }

        //Details button to navigate to DetailsFragment
        val buttonDetails = view.findViewById<Button>(R.id.movie_view_details_button)
        buttonDetails.setOnClickListener {
            val navController = Navigation.findNavController(view)

            // get the movie id
            val movieId = movie.id

            val bundle = bundleOf("movieId" to movieId)
            //view.findNavController().navigate(R.id.action_get_details, bundle)
            navController.navigate(R.id.action_get_details, bundle)

        }



    }
}