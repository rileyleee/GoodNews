import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.saveurlife.goodnews.main.MainAroundListFragment
import com.saveurlife.goodnews.main.MainFamilyAroundListFragment

class MainAdapter(fm: FragmentManager, private val numOfTabs: Int) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> MainAroundListFragment()
            1 -> MainFamilyAroundListFragment()
            else -> throw IllegalArgumentException("Invalid tab position: $position")
        }
    }

    override fun getCount(): Int {
        return numOfTabs
    }
}
