package com.mecm.shopcart.entity

data class CartShop(
    var id: Int,
    var name: String,
    var cartProduct: ArrayList<CartProduct>,
    var selected: Boolean = false
)

data class CartProduct(
    var id: Int,
    var name: String,
    var price: Double,
    var selected: Boolean = false
)