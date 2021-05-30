/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.cupcake

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.cupcake.databinding.FragmentSummaryBinding
import com.example.cupcake.model.OrderViewModel


/**
 * [SummaryFragment] contains a summary of the order details with a button to share the order
 * via another app.
 */
class SummaryFragment : Fragment() {
    private val sharedViewModel: OrderViewModel by activityViewModels()

    // Binding object instance corresponding to the fragment_summary.xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment.
    private var binding: FragmentSummaryBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentSummaryBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            summaryFragment = this@SummaryFragment
        }
    }

    /**
     * Submit the order by sharing out the order details to another app via an implicit intent.
     */
    fun sendOrder() {
        val numberOfCupcakes = sharedViewModel.quantity.value ?: 0

        //Toast.makeText(activity, "Send Order", Toast.LENGTH_SHORT).show()
        // getQuantityString(R.string.cupcakes, 1, 1) returns the string 1 cupcake
        // getQuantityString(R.string.cupcakes, 6, 6) returns the string 6 cupcakes
        // The first quantity parameter is used to select the correct plural string.
        // The second quantity parameter is used in the %d placeholder of the actual string resource.
        val orderSummary = getString(
                R.string.order_details,
                resources.getQuantityString(R.plurals.cupcakes, numberOfCupcakes, numberOfCupcakes),
                listFlavor(),
                sharedViewModel.date.value.toString(),
                sharedViewModel.price.value.toString()
        )

        val intent = Intent(Intent.ACTION_SEND)
                .setType("text/plain")
                .putExtra(Intent.EXTRA_SUBJECT, getString(R.string.new_cupcake_order))
                .putExtra(Intent.EXTRA_TEXT, orderSummary)
                .putExtra(Intent.EXTRA_EMAIL, arrayOf("dien.tranxuan110594@gmail.com"))

        if (activity?.packageManager?.resolveActivity(intent, 0) != null) {
            startActivity(intent)
        }
    }

    fun cancelOrder() {
        sharedViewModel.resetOrder()
        findNavController().navigate(R.id.action_summaryFragment_to_startFragment)
    }

    fun listFlavor() : String {
        val formatedFlavors = StringBuilder()
        val listFlavor = sharedViewModel.flavors.value?: mapOf<String, Int>()
        for ((key, value) in listFlavor) {
            if (!value.equals("0")) {
                formatedFlavors.append(key)
                        .append(" x")
                        .append(value)
                        .append(" || ")
            }
        }
        if ((sharedViewModel.qualityRemain.value?:0) > 0){
            formatedFlavors.append("Your choice")
                    .append(" x")
                    .append((sharedViewModel.qualityRemain.value?:0).toString())
                    .append(" || ")
        }
        return formatedFlavors.toString()
    }

    /**
     * This fragment lifecycle method is called when the view hierarchy associated with the fragment
     * is being removed. As a result, clear out the binding object.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}