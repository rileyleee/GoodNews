import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.saveurlife.goodnews.main.MainAroundListFragment
import com.saveurlife.goodnews.main.MainFamilyAroundListFragment

class MainAdapter(fragment: Fragment) :FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MainAroundListFragment()
            1 -> MainFamilyAroundListFragment()
            else -> throw IllegalStateException("Invalid position $position")
        }
    }
}
