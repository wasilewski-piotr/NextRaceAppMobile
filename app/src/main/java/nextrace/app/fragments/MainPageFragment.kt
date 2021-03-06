package nextrace.app.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import nextrace.app.R
import nextrace.app.adapters.RecyclerViewNextRaceAdapter
import nextrace.app.adapters.RecyclerViewPreviousRaceAdapter
import nextrace.app.adapters.RecyclerViewRaceAdapter
import nextrace.app.api.RaceApi
import nextrace.app.api.RaceApiClient
import nextrace.app.listeners.ClickListener
import nextrace.app.models.Event
import nextrace.app.models.Race
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

class MainPageFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setSubtitle()
        return inflater.inflate(R.layout.fragment_main_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setSubtitle()
        getData(view)
    }

    private fun getData(view: View) {
        val raceApi: RaceApi = RaceApiClient().buildService(RaceApi::class.java)
        val call: Call<List<Race>> = raceApi.getRaces()
        call.enqueue(object : Callback<List<Race>>, ClickListener {
            override fun onFailure(call: Call<List<Race>>, t: Throwable) {
                Log.d("TAG", "Response = $t")
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<List<Race>>, response: Response<List<Race>>) {
                val recyclerView = view.findViewById<RecyclerView>(R.id.main_page_item_list)
                val recyclerViewNextRace = view.findViewById<RecyclerView>(R.id.main_page_next_race_list)
                val recyclerViewPreviousRace = view.findViewById<RecyclerView>(R.id.main_page_previous_race_list)

                val layoutManager = LinearLayoutManager(view.context)
                val layoutManagerPrev = LinearLayoutManager(view.context)
                val layoutManagerNext = LinearLayoutManager(view.context)
                val raceList = response.body() as MutableList<Race>

                val finalRaceList: ArrayList<Race> = ArrayList()
                val finalPreviousRaceList: ArrayList<Race> = ArrayList()
                val finalComingUpRaceList: ArrayList<Race> = ArrayList()

                val comingUpRaceList: ArrayList<Race> = ArrayList()
                val previousRaceList: ArrayList<Race> = ArrayList()

                val textView: TextView = view.findViewById(R.id.main_page_error_message)

                raceList!!.forEach { race ->
                    val raceEvents: MutableList<Event> = race.eventList.events
                    raceEvents.forEach { event ->
                        val date = LocalDate.of(Integer.parseInt(event.date.substring(0,4)), Integer.parseInt(event.date.substring(5, 7)), Integer.parseInt(event.date.substring(8,10)))
                        val today = LocalDate.of(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH)+1, Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
                        if(date.isAfter(today)){
                            if(date.monthValue == Calendar.getInstance().get(Calendar.MONTH)+1){
                                if(event.type == "Race"){
                                    finalRaceList.add(race)
                                }
                            }else{
                                if(event.type == "Race"){
                                    finalComingUpRaceList.add(race)
                                }
                            }
                        }else{
                            if(event.type == "Race"){
                                finalPreviousRaceList.add(race)
                            }
                        }
                    }
                }

                if(finalRaceList.isNotEmpty()){
                    textView.text = context?.getString(R.string.null_string)
                }else{
                    textView.text = context?.getString(R.string.main_page_error)
                }

                if(finalPreviousRaceList.isNotEmpty()){
                    previousRaceList.add(finalPreviousRaceList.last())
                }

                if(finalRaceList.isEmpty()){
                    if (finalComingUpRaceList.isNotEmpty()) {
                        comingUpRaceList.add(finalComingUpRaceList.first())
                    }
                }else{
                    comingUpRaceList.add(finalRaceList.first())
                }

                val raceAdapter = RecyclerViewRaceAdapter(finalRaceList, this, context)
                recyclerView.layoutManager = layoutManager
                recyclerView.adapter = raceAdapter

                val raceAdapterPreviousRaces = RecyclerViewPreviousRaceAdapter(previousRaceList, this)
                recyclerViewPreviousRace.layoutManager = layoutManagerPrev
                recyclerViewPreviousRace.adapter = raceAdapterPreviousRaces

                val raceAdapterComingUpRaces= RecyclerViewNextRaceAdapter(comingUpRaceList, this)
                recyclerViewNextRace.layoutManager = layoutManagerNext
                recyclerViewNextRace.adapter = raceAdapterComingUpRaces
            }

            override fun onClick(pos: Int) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onResume() {
        super.onResume()
        setSubtitle()
    }

    private fun getDate(): String {
        val cal = Calendar.getInstance()
        val monthDate = SimpleDateFormat("LLLL", Locale.forLanguageTag("EN"))
        val month = monthDate.format(cal.time)
        return month.substring(0, 1).toUpperCase() + month.substring(1)
    }

    private fun setSubtitle() {
        (activity as AppCompatActivity).supportActionBar!!.subtitle = getDate()
    }

    private fun clearSubtitle() {
        val subtitle: String = resources.getString(R.string.null_string)
        (activity as AppCompatActivity).supportActionBar!!.subtitle = subtitle
    }

}
