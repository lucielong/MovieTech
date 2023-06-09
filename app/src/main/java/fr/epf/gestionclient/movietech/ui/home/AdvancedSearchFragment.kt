package fr.epf.gestionclient.movietech.ui.home

import android.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.slider.RangeSlider
import fr.epf.gestionclient.movietech.databinding.FragmentAdvancedSearchBinding


class AdvancedSearchFragment : Fragment(){
    private var _binding: FragmentAdvancedSearchBinding? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdvancedSearchBinding.inflate(inflater, container, false)
        val binding = _binding ?: return null
        val root: View = binding.root
        val spinner = binding.spinnerSortby
        val items = listOf("popularity.asc", "popularity.desc", "release_date.asc", "release_date.desc", "revenue.asc", "revenue.desc", "primary_release_date.asc", "primary_release_date.desc", "original_title.asc", "original_title.desc", "vote_average.asc", "vote_average.desc", "vote_count.asc", "vote_count.desc")
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, items)
        spinner.adapter = adapter
        val checkboxAction = binding.checkboxAction
        val checkboxAdventure = binding.checkboxAdventure
        val checkboxAnimation = binding.checkboxAnimation
        val checkboxComedy = binding.checkboxComedy
        val checkboxCrime = binding.checkboxCrime
        val checkboxDocumentary = binding.checkboxDocumentary
        val checkboxDrama = binding.checkboxDrama
        val checkboxFamily = binding.checkboxFamily
        val checkboxFantasy = binding.checkboxFantasy
        val checkboxHistory = binding.checkboxHistory
        val checkboxHorror = binding.checkboxHorror
        val checkboxMusic = binding.checkboxMusic
        val checkboxMystery = binding.checkboxMystery
        val checkboxRomance = binding.checkboxRomance
        val checkboxScienceFiction = binding.checkboxSciencefiction
        val checkboxTVMovie = binding.checkboxTvmovie
        val checkboxThriller = binding.checkboxThriller
        val checkboxWar = binding.checkboxWar
        val checkboxWestern = binding.checkboxWestern
        val switchadult = binding.switchAdult

        val slider: RangeSlider = binding.rangeSlider
        slider.setValues(0f, 10.0f)

        val apply = binding.buttonApply

        apply.setOnClickListener() {
            var genres = ""
            if (checkboxAction.isChecked) {
                genres += "28 OR"
            }
            if (checkboxAdventure.isChecked) {
                genres += "12 OR"
            }
            if (checkboxAnimation.isChecked) {
                genres += "16 OR"
            }
            if (checkboxComedy.isChecked) {
                genres += "35 OR"
            }
            if (checkboxCrime.isChecked) {
                genres += "80 OR"
            }
            if (checkboxDocumentary.isChecked) {
                genres += "99 OR"
            }
            if (checkboxDrama.isChecked) {
                genres += "18 OR"
            }
            if (checkboxFamily.isChecked) {
                genres += "10751 OR"
            }
            if (checkboxFantasy.isChecked) {
                genres += "14 OR"
            }
            if (checkboxHistory.isChecked) {
                genres += "36 OR"
            }
            if (checkboxHorror.isChecked) {
                genres += "27 OR"
            }
            if (checkboxMusic.isChecked) {
                genres += "10402 OR"
            }
            if (checkboxMystery.isChecked) {
                genres += "9648 OR"
            }
            if (checkboxRomance.isChecked) {
                genres += "10749 OR"
            }
            if (checkboxScienceFiction.isChecked) {
                genres += "878 OR"
            }
            if (checkboxTVMovie.isChecked) {
                genres += "10770 OR"
            }
            if (checkboxThriller.isChecked) {
                genres += "53 OR"
            }
            if (checkboxWar.isChecked) {
                genres += "10752 OR"
            }
            if (checkboxWestern.isChecked) {
                genres += "37 OR"
            }
            if (genres != "") {
                genres = genres.substring(0, genres.length - 3)
            }
            if (genres == "") {
                genres = "28 OR 12 OR 16 OR 35 OR 80 OR 99 OR 18 OR 10751 OR 14 OR 36 OR 27 OR 10402 OR 9648 OR 10749 OR 878 OR 10770 OR 53 OR 10752 OR 37"
            }
            val min = slider.values[0]
            val max = slider.values[1]
            Log.d("min", min.toString())
            Log.d("max", max.toString())

            val sortby = spinner.selectedItem.toString()
            var adult = false
            if (switchadult.isChecked) {
                adult = true
            }

            val bundle = Bundle()
            bundle.putString("genres",genres)
            bundle.putString("sortby", sortby)
            bundle.putFloat("min", min)
            bundle.putFloat("max", max)
            bundle.putBoolean("adult", adult)

            findNavController().navigate(fr.epf.gestionclient.movietech.R.id.action_advanced_search_results, bundle)
        }


        return root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
