package com.ntg.movieapiapp.data.model

//data class Item()
interface ListItem {
    enum class Type(value: Int) {TypeA(0), TypeB(1) }
    fun getListItemType(): Int
}

data class ItemA(val textA: String): ListItem {
    override fun getListItemType(): Int {
        return ListItem.Type.TypeA.ordinal
    }
}