package nextrace.app.fragments.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

import nextrace.app.R
import nextrace.app.adapters.SectionsPageAdapter

class SearchFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val viewRoot = inflater.inflate(R.layout.fragment_search, container, false)
        val tabLayout: TabLayout = viewRoot.findViewById(R.id.tabs)
        val viewPager: ViewPager = viewRoot.findViewById(R.id.container)
        clearSubtitle()
        init(viewPager)
        tabLayout.setupWithViewPager(viewPager)
        return viewRoot
    }

    private fun clearSubtitle() {
        val subtitle: String = resources.getString(R.string.null_string)
        (activity as AppCompatActivity).supportActionBar!!.subtitle = subtitle
    }

    private fun init(viewPager: ViewPager){
        val adapter = SectionsPageAdapter(childFragmentManager)
        adapter.addFragment(SearchByCategoryFragment(), resources.getString(R.string.search_by_category))
        adapter.addFragment(SearchByCountryFragment(), resources.getString(R.string.search_by_country))
        adapter.addFragment(SearchByTrackFragment(), resources.getString(R.string.search_by_track))
        adapter.addFragment(SearchByDateFragment(), resources.getString(R.string.search_by_date))
        viewPager.adapter = adapter
    }
}
