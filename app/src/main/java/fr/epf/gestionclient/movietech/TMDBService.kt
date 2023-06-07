package fr.epf.gestionclient.movietech

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface TMDBService {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("page") page: Int
    ): Response<MovieResponse>

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("page") page: Int
    ): Response<MovieResponse>

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("page") page: Int
    ): Response<MovieResponse>

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("page") page: Int
    ): Response<MovieResponse>

    @GET("search/movie")
    suspend fun getSearchMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("query") query: String
    ): Response<MovieResponse>

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("append_to_response") appendToResponse: String
    ): Response<MovieDetails>

    @POST("account/{accountId}/favorite")
    suspend fun addFavoriteMovies(
        @Path("accountId") accountId: Int,
        @Header("Authorization") authorization: String,
        @Body body: FavoriteBody
    ): Response<FavoriteResponse>

    @GET("account/{accountId}/favorite/movies")
    suspend fun getFavoriteMovies(
        @Path("accountId") accountId: Int,
        @Header("Authorization") authorization: String
    ): Response<MovieResponse>

    @GET("discover/movie")
    suspend fun getMoviesBy(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("with_genres") with_genres: String
    ): Response<MovieResponse>

    @GET("genre/movie/list")
    suspend fun getGenre(
        @Query("api_key") apiKey: String
    ): Response<GenreResponse>

    @GET("movie/{movie_id}/recommendations")
    suspend fun getRecommendations(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("page") page: Int
    ): Response<RecommendationsResponse>

}

data class RecommendationsResponse(
    val results: List<MovieRecommendations>,
)

data class MovieResponse(
    val results: List<Movie>
)
data class Movie(
    val adult: Boolean,
    val backdrop_path: String,
    val genre_ids: List<Int>,
    val id: Int,
    val original_language: String,
    val original_title: String,
    val overview: String,
    val popularity: Double,
    val poster_path: String,
    val release_date: String,
    val title: String,
    val video: Boolean,
    val vote_average: Double,
    val vote_count: Int
)

data class MovieDetails(
    val adult: Boolean,
    val backdrop_path: String,
    val belongs_to_collection : Any?,
    val budget: Int,
    val genre_ids: List<Genre>,
    val homepage : String,
    val id: Int,
    val imdb_id : String,
    val original_language: String,
    val original_title: String,
    val overview: String,
    val popularity: Double,
    val poster_path: String,
    val production_companies_ids : List<ProductionCompany>,
    val production_countries : List<ProductionCountry>,
    val release_date: String,
    val revenue : Int,
    val runtime : Int,
    val spoken_languages : List<SpokenLanguage>,
    val status : String,
    val tagline : String,
    val title: String,
    val video: Boolean,
    val vote_average: Double,
    val vote_count: Int
)

data class MovieRecommendations(
    val adult: Boolean,
    val backdrop_path: String,
    val id: Int,
    val title: String,
    val original_language: String,
    val original_title: String,
    val overview: String,
    val poster_path: String,
    val media_type: String,
    val genre_ids: List<Int>,
    val popularity: Double,
    val release_date: String,
    val video: Boolean,
    val vote_average: Double,
    val vote_count: Int
)

data class ProductionCompany(
    val id: Int,
    val logo_path: String?,
    val name: String,
    val origin_country: String
)

data class ProductionCountry(
    val iso_3166_1: String,
    val name: String
)

data class SpokenLanguage(
    val english_name: String,
    val iso_639_1: String,
    val name: String
)

data class TokenResponse(
    val success: Boolean,
    val expires_at: String,
    val request_token: String
)

data class SessionRequest(
    val request_token: String
)

data class SessionResponse(
    val success: Boolean,
    val session_id: String
)


data class FavoriteResponse(
    val status_code: Int,
    val status_message: String
)

data class Genre(
    val id: Int,
    val name: String
)

data class FavoriteBody(
    val media_type: String,
    val media_id: Int,
    val favorite: Boolean
)

data class GenreResponse(
    val genres: List<Genre>
)

