package com.example.wordsapp

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wordsapp.databinding.FragmentLetterListBinding

/**
 * A simple [Fragment] subclass.
 * Use the [LetterListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LetterListFragment : Fragment() {
    private var isLinearLayoutManager = true
    // implement view binding
    // FragmentLetterListBinding is nullable
    private var _binding: FragmentLetterListBinding? = null
    // If you're certain a value won't be null when you access it, you can append !! to its type name
    // get() means this property is "get-only": That means you can get the value,
    // but once assigned (as it is here), you can't assign it to something else.
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView


    // Remember that with fragments, the layout is inflated in onCreateView().
    // Implement onCreateView() by inflating the view,
    // setting the value of _binding, and returning the root view.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLetterListBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = binding.recyclerView
        chooseLayout()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // While the Activity class has a global property called menuInflater,
    // Fragment does not have this property.
    // The menu inflater is instead passed into onCreateOptionsMenu().
    // Also note that the onCreateOptionsMenu() method used with fragments
    // doesn't require a return statement. Implement the method as shown:
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.layout_menu, menu)

        val layoutButton = menu.findItem(R.id.action_switch_layout)
        setIcon(layoutButton)
    }

    // unlike an activity, a fragment is not a Context
    // You can't pass in this (referring to the fragment object) as the layout manager's context
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_switch_layout -> {
                // Sets isLinearLayoutManager (a Boolean) to the opposite value
                isLinearLayoutManager = !isLinearLayoutManager
                // Sets layout and icon
                chooseLayout()
                setIcon(item)

                return true
            }
            //  Otherwise, do nothing and use the core event handling

            // when clauses require that all possible paths be accounted for explicitly,
            //  for instance both the true and false cases if the value is a Boolean,
            //  or an else to catch all unhandled cases.
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun chooseLayout() {
        if (isLinearLayoutManager) {
            recyclerView.layoutManager = LinearLayoutManager(context)
        } else {
            recyclerView.layoutManager = GridLayoutManager(context, 4)
        }
        recyclerView.adapter = LetterAdapter()
    }
    // Toggle icon
    private fun setIcon(menuItem: MenuItem?) {
        if (menuItem == null)
            return
        // Set the drawable for the menu icon based on which LayoutManager is currently in use
        menuItem.icon =
            if (isLinearLayoutManager)
                ContextCompat.getDrawable(this.requireContext(), R.drawable.ic_grid_layout)
            else ContextCompat.getDrawable(this.requireContext(), R.drawable.ic_linear_layout)
    }

}