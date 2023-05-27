package fr.epf.gestionclient.movietech.ui.home

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.camera.core.Camera
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import fr.epf.gestionclient.movietech.Movie
import fr.epf.gestionclient.movietech.R
import fr.epf.gestionclient.movietech.TMDBService
import fr.epf.gestionclient.movietech.databinding.FragmentHomeBinding
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import androidx.camera.view.PreviewView



class HomeFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var movieAdapter: MovieAdapter
    private lateinit var camera: Camera
    private lateinit var cameraSelector: CameraSelector
    private lateinit var preview: Preview
    private lateinit var imageCapture: ImageCapture


    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val binding = _binding ?: return null
        val root: View = binding.root
        recyclerView = binding.HomeMoviesRecycler
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        // Instantiate MovieAdapter with empty list of movies
        movieAdapter = MovieAdapter(emptyList(), requireContext())
        recyclerView.adapter = movieAdapter


        val radioGroup = binding.HomeMoviesRadioGroup
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.Home_movies_popular -> {
                    // Logique à exécuter lorsque "Popular" est sélectionné
                    synchro()
                }
                R.id.Home_movies_top_rated -> {
                    // Logique à exécuter lorsque "Top Rated" est sélectionné
                    synchro()
                }
                R.id.Home_movies_upcoming -> {
                    // Logique à exécuter lorsque "Upcoming" est sélectionné
                    synchro()
                }
                R.id.Home_movies_nowPlaying -> {
                    // Logique à exécuter lorsque "Now Playing" est sélectionné
                    synchro()
                }
            }
        }


        val search = binding.HomeSearchView
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Logique à exécuter lorsque l'utilisateur valide sa recherche
                val bundle = Bundle()
                bundle.putString("query", query)
                findNavController().navigate(R.id.action_search, bundle)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Logique à exécuter lorsque l'utilisateur modifie le texte de sa recherche
                return false
            }
        })


        val qrCodeButton = binding.HomeQrcodeButton
        qrCodeButton.setOnClickListener {
            findNavController().navigate(R.id.action_qrcode)
        }
        synchro()
        homeViewModel.text.observe(viewLifecycleOwner) {
            // Handle the observed text change here
        }

        return root
    }


    private fun updateMovieList(movies: List<Movie>) {
        movieAdapter.updateMovies(movies)
    }


    private fun synchro() {
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

        runBlocking {
            try {
                val popularButton = binding.HomeMoviesPopular
                val topRatedButton = binding.HomeMoviesTopRated
                val upcomingButton = binding.HomeMoviesUpcoming
                val nowPlayingButton = binding.HomeMoviesNowPlaying
                var response = TMDBService.getPopularMovies(apiKey, "en-US", 1)
                if (popularButton.isChecked) {
                    response = TMDBService.getPopularMovies(apiKey, "en-US", 1)
                }
                if (topRatedButton.isChecked) {
                    response = TMDBService.getTopRatedMovies(apiKey, "en-US", 1)
                }
                if (upcomingButton.isChecked) {
                    response = TMDBService.getUpcomingMovies(apiKey, "en-US", 1)
                }
                if (nowPlayingButton.isChecked) {
                    response = TMDBService.getNowPlayingMovies(apiKey, "en-US", 1)
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



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
