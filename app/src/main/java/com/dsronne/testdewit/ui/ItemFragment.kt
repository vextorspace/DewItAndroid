package com.dsronne.testdewit.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.dsronne.testdewit.R
import com.dsronne.testdewit.datamodel.ListItem

class ItemFragment : Fragment() {

    private var label: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        label = arguments?.getString(ARG_LABEL)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.text_label).text = label
    }

    companion object {
        private const val ARG_LABEL = "arg_label"

        fun newInstance(item: ListItem): ItemFragment {
            val fragment = ItemFragment()
            val args = Bundle()
            args.putString(ARG_LABEL, item.label())
            fragment.arguments = args
            return fragment
        }
    }
}
