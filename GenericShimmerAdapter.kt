package com.mindbyromanzanoni.genrics

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView


/**
 * [GenericShimmerAdapter]
 * this class is design for create an abstraction layer over our recycler view Adapter.
 * Generics are the powerful features that allow us to define classes,
 * methods and properties which are accessible using different data types while keeping a check of the compile-time type safety.
 * <T> is used for Layout Type Binding
 * <M> is used for Data Class for specific type
 * Pass a <M> in DiffUtil Class
 * */

abstract class GenericShimmerAdapter<T, M>() : ListAdapter<M, RecyclerView.ViewHolder>(
    GenericDiffUtil<M>()
) {

    private lateinit var binding: ViewDataBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), getResourceLayoutId(), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        try {
            holder as ViewHolder
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    class ViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root)

    abstract fun getResourceLayoutId(): Int

}