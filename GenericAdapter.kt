package com.mindbyromanzanoni.genrics

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mindbyromanzanoni.R


/**
 * [GenericAdapter]
 * this class is design for create an abstraction layer over our recycler view Adapter.
 * Generics are the powerful features that allow us to define classes,
 * methods and properties which are accessible using different data types while keeping a check of the compile-time type safety.
 * <T> is used for Layout Type Binding
 * <M> is used for Data Class for specific type
 * Pass a <M> in DiffUtil Class
 * */

abstract class GenericAdapter<T, M>(var isShowAnimation: Boolean = true) : ListAdapter<M, RecyclerView.ViewHolder>(
    GenericDiffUtil<M>()
) {

    private lateinit var binding: ViewDataBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            getResourceLayoutId(),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        try {
            holder as ViewHolder
//            holder.binding.root.setAnimation()
            val dataClass = getItem(position)
            onBindHolder(holder.binding as T, dataClass, position)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    class ViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root)

    abstract fun getResourceLayoutId(): Int

    abstract fun onBindHolder(holder: T, dataClass: M, position: Int)


    /**
     * this function is user to perform [Animation] on item view
     * */

    private fun View.setAnimation() {
        if (isShowAnimation){
            val animation: Animation =
                AnimationUtils.loadAnimation(this.context, R.anim.recycler_animation)
            this.startAnimation(animation)
        }
    }
}