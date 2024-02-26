package com.mindbyromanzanoni.genrics


/**
 * GenericChatAdapter
 * this class is design for create an abstraction layer over our recycler view Adapter.
 * Generics are the powerful features that allow us to define classes,
 * methods and properties which are accessible using different data types while keeping a check of the compile-time type safety.
 * <T> is used for Layout Type Binding
 * <M> is used for Data Class for specific type
 * Pass a <M> in DiffUtil Class
 * */
/*

abstract class GenericChatAdapter(
    private var isShowAnimation: Boolean = true,
    var sharedPrefs: SharedPrefs
) : ListAdapter<ChatListResponseModal.ChatModel, ChatViewHolder>(GenericDiffUtil<ChatListResponseModal.ChatModel>()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return when (viewType) {
            R.layout.chat_item_left -> {
                ChatViewHolder.ViewHolderLeftChat(
                    ChatItemLeftBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            R.layout.chat_item_right -> {
                ChatViewHolder.ViewHolderRightChat(
                    ChatItemRightBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            R.layout.chat_time_layout -> {
                ChatViewHolder.ViewHolderTime(
                    ChatTimeLayoutBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> throw IllegalArgumentException("Invalid view type ")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (currentList[position].type != 0) {
            R.layout.chat_time_layout
        } else {
            when (currentList[position].senderId) {
                sharedPrefs.getUserData(AppConstant.SHARED_PREFS_USER_DATA)?.id.toString() -> {
                    getResourceLayoutIdRight()
                }
                else -> {
                    getResourceLayoutIdLeft()
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        try {
            holder.itemView.setAnimation()
            val dataClass = getItem(position)
            onBindHolder(holder, dataClass, position)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    abstract fun getResourceLayoutIdLeft(): Int

    abstract fun getResourceLayoutIdRight(): Int

    abstract fun onBindHolder(
        holder: ChatViewHolder,
        dataClass: ChatListResponseModal.ChatModel,
        position: Int
    )


    */
/**
     * this function is user to perform Animation on item view
     * *//*


    private fun View.setAnimation() {
        if (isShowAnimation) {
            val animation: Animation = AnimationUtils.loadAnimation(this.context, R.anim.recycler_animation)
            this.startAnimation(animation)
        }
    }
}*/
