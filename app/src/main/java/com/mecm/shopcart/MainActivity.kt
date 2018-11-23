package com.mecm.shopcart

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.mecm.shopcart.adpater.CartListAdapter
import com.mecm.shopcart.entity.CartProduct
import com.mecm.shopcart.entity.CartShop
import kotlinx.android.synthetic.main.activity_main.*
import java.text.DecimalFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    // 选中的价格和数量
    private var totalPrice: Double = 0.0
    private var totalNum: Int = 0
    // 选中的产品
    private var selectedProList: ArrayList<Int> = ArrayList()
    // 选中的商城
    var selectedShopList: ArrayList<Int> = ArrayList()
    // 是否更新为 全部选中或者 全部取消
    private var isUpdateAll: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        val carts = cartData()
        // 设置 recyclerView
        cart_shop_recyclerview.layoutManager = LinearLayoutManager(this)
        val cartListAdapter = CartListAdapter(carts)
        cart_shop_recyclerview.adapter = cartListAdapter
        // end

        // 增加监听 商城 或 产品 改变事件
        cartListAdapter.addShopCheckedListener = object : CartListAdapter.AddShopCheckedListener {
            // 更新 商城 全选状态
            override fun updateShop(item: CartShop) {
                updateAllCheckedState(carts)
                calPrice(carts)
                isAddShopId(item)
            }

            // 更新 产品 选中 状态
            override fun updateProduct(cartProduct: CartProduct, cartShop: CartShop) {
                updateAllCheckedState(carts)
                calPrice(carts)
                if (cartShop.selected) {
                    if (!selectedShopList.contains(cartShop.id)) {
                        selectedShopList.add(cartShop.id)
                    }
                } else {
                    if (selectedShopList.contains(cartShop.id)) {
                        selectedShopList.remove(cartShop.id)
                    }
                }
                isAddProductId(cartProduct)

            }

        }
        // end

        // 全选
        cart_all_checked.setOnCheckedChangeListener { _, isChecked ->
            // 通过点击全选按钮 触发的监听事件
            if (isUpdateAll) {
                checkedAll(carts, isChecked)
                if (!isChecked) {
                    totalPrice = 0.0
                    totalNum = 0
                }
                cart_price.text = DecimalFormat("0.00").format(totalPrice).toString()
                cart_num.text = totalNum.toString()
            }
            cartListAdapter.notifyDataSetChanged()
            isUpdateAll = true
        }
        // end

        // 删除
        cart_all_deleted.setOnClickListener {
            val copyShops = ArrayList<CartShop>()
            carts.forEach { shop ->
                val copyProducts = ArrayList<CartProduct>()
                // 不包含 商城id。留下来
                if (!selectedShopList.contains(shop.id)) {
                    shop.cartProduct.forEach { product ->
                        // 不包含 产品id。留下来
                        if (!selectedProList.contains(product.id)) {
                            copyProducts.add(product)
                        }
                    }
                    shop.cartProduct.clear()
                    shop.cartProduct.addAll(copyProducts)
                    copyShops.add(shop)
                }
            }
            // 更新购物车内状态
            carts.clear()
            carts.addAll(copyShops)
            cartListAdapter.notifyDataSetChanged()
            calPrice(carts)
            // 删除完成之后 更新 全选为 false
            if (cart_all_checked.isChecked) {
                cart_all_checked.isChecked = false
            }
        }
        // end

    }

    // 更新底部 全选的状态
    private fun updateAllCheckedState(cartShops: ArrayList<CartShop>) {
        var isAllChecked = false
        for (cart in cartShops) {
            if (!cart.selected) {
                isAllChecked = false
                break
            } else {
                isAllChecked = true
            }
        }
        // 更新全选的状态
        // cart_all_checked.isChecked 与 isAllChecked 一致将不会执行setOnCheckedChangeListener
        if (cart_all_checked.isChecked != isAllChecked) {
            isUpdateAll = false
        }
        cart_all_checked.isChecked = isAllChecked
        // end
    }


    // 是否添加产品 或删除
    private fun isAddProductId(cartProduct: CartProduct) {
        if (cartProduct.selected) {
            if (!selectedProList.contains(cartProduct.id)) {
                selectedProList.add(cartProduct.id)
            }
        } else {
            if (selectedProList.contains(cartProduct.id)) {
                selectedProList.remove(cartProduct.id)
            }
        }
    }

    // 是否添加商城 或删除
    private fun isAddShopId(shop: CartShop) {
        if (shop.selected) {
            if (!selectedShopList.contains(shop.id)) {
                selectedShopList.add(shop.id)
            }
        } else {
            if (selectedShopList.contains(shop.id)) {
                selectedShopList.remove(shop.id)
            }
        }
        // 全选 添加所有产品 或者 删除
        shop.cartProduct.forEach {
            if (!selectedProList.contains(it.id)) {
                selectedProList.add(it.id)
            } else {
                selectedProList.remove(it.id)
            }
        }
    }

    // 全选
    private fun checkedAll(cartShops: ArrayList<CartShop>, isChecked: Boolean) {
        totalNum = 0
        totalPrice = 0.0
        cartShops.forEach {
            it.selected = isChecked
            totalNum += it.cartProduct.size
            isAddShopId(it)
            // 产品
            it.cartProduct.forEach { product ->
                product.selected = isChecked
                totalPrice += product.price
                isAddProductId(product)
            }
            // end
        }

    }

    // 计算价格
    private fun calPrice(cartShops: ArrayList<CartShop>) {
        totalNum = 0
        totalPrice = 0.0
        cartShops.forEach {
            it.cartProduct.forEach { product ->
                if (product.selected) {
                    totalNum++
                    totalPrice += product.price
                }
            }
        }
        cart_price.text = DecimalFormat("0.00").format(totalPrice).toString()
        cart_num.text = totalNum.toString()
    }

    // 购物车数据
    private fun cartData(): ArrayList<CartShop> {
        val carts = ArrayList<CartShop>()
        var proId = 0
        var shopId = 0
        for (i in 1..10) {
            val cartName = "shop = $i"
            shopId++
            val products = ArrayList<CartProduct>()
            for (j in 1..3) {
                proId++
                products.add(CartProduct(proId, cartName + "cartProduct = $j", Random().nextDouble() * 10, false))
            }
            carts.add(CartShop(shopId, cartName, products))
        }
        return carts
    }
}
