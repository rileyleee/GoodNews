import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.saveurlife.goodnews.main.MainAroundListFragment
import com.saveurlife.goodnews.main.MainAroundAdvertiseFragment
import com.saveurlife.goodnews.main.MainFamilyAroundListFragment

class MainAdapter(fragment: Fragment) :FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MainAroundAdvertiseFragment()
            1 -> MainAroundListFragment()
            2 -> MainFamilyAroundListFragment()
            else -> throw IllegalStateException("Invalid position $position")
        }
    }
}
