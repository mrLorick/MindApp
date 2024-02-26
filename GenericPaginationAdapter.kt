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
 * [GenericPaginationAdapter]
 * this class is design for create an abstraction layer over our recycler view Adapter.
 * Generics are the powerful features that allow us to define classes,
 * methods and properties which are accessible using different data types while keeping a check of the compile-time type safety.
 * <T> is used for Layout Type Binding
 * <M> is used for Data Class for specific type
 * Pass a <M> in DiffUtil Class
 * */

abstract class GenericPaginationAdapter<T, M>(var isShowAnimation: Boolean = true) : ListAdapter<M, RecyclerView.ViewHolder>(
    GenericDiffUtil<M>()
) {

    private lateinit var binding: ViewDataBinding

    var isLoading = false

    private val viewTypeLoading = 0

    private val viewTypeInvitationList = 1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == viewTypeLoading) {
            binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.shimmer_item_comments,
                parent,
                false
            )
            return ViewHolder(binding)
        } else {
            binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                getResourceLayoutId(),
                parent,
                false
            )
            return ViewHolder(binding)
        }
    }

    override fun getItemViewType(position: Int): Int {
       return if (isLoading) {
            if (position == currentList.size - 1) {
                viewTypeLoading
            } else {
                viewTypeInvitationList
            }
        } else {
            viewTypeInvitationList
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        try {
            holder as ViewHolder
            holder.binding.root.setAnimation()
            val dataClass = getItem(position)
            onBindHolder(holder.binding as T, dataClass, position)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    class ViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root)


    abstract fun getResourceLayoutId(): Int

    abstract fun onBindHolder(holder: T, dataClass: M, position: Int)



    private fun View.setAnimation() {
        if (isShowAnimation) {
            val animation: Animation =
                AnimationUtils.loadAnimation(this.context, R.anim.recycler_animation)
            this.startAnimation(animation)
        }
    }
}
