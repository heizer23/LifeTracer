package com.example.lifetracer.views

interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int)
    // Add other methods if you need, like onItemDismiss for swipe actions
}