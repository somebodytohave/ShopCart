package com.mecm.shopcart.adpater

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.CheckBox
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.mecm.shopcart.R
import com.mecm.shopcart.entity.CartProduct
import com.mecm.shopcart.entity.CartShop

//  购物车列表
class CartListAdapter(dataList: ArrayList<CartShop>) :
    BaseQuickAdapter<CartShop, BaseViewHolder>(R.layout.item_cart_list, dataList) {

    // 商城 或 产品 更新 需要执行的操作(重新计算价格等。)
    interface AddShopCheckedListener {
        fun updateShop(item: CartShop)
        fun updateProduct(cartProduct: CartProduct, cartShop: CartShop)
    }

    var addShopCheckedListener: AddShopCheckedListener? = null

    override fun convert(helper: BaseViewHolder, item: CartShop) {
        helper.setIsRecyclable(false) // 为了条目不复用
        helper.setText(R.id.item_cart_shop_name, item.name)

        // 设置 商城中 产品的列表
        val recyclerView = helper.getView<RecyclerView>(R.id.cart_product_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(mContext)
        val cartProductAdapter = CartProductAdapter(item.cartProduct)
        recyclerView.adapter = cartProductAdapter
        // end

        // 根据产品 更改事件 改变商城的状态
        cartProductAdapter.addShopProductCheckedListener = object : CartProductAdapter.AddShopProductCheckedListener {
            override fun shopProductChecked(cartProduct: CartProduct) {
                item.selected = shopIsChecked(item)

                if (addShopCheckedListener != null) {
                    addShopCheckedListener!!.updateProduct(cartProduct, item)
                }
                notifyDataSetChanged()
            }
        }
        // end

        // 设置商城 更改事件
        val shopCb = helper.getView<CheckBox>(R.id.item_cart_shop_cb)
        shopCb.isChecked = shopIsChecked(item)
        shopCb.setOnCheckedChangeListener { _, isChecked ->
                setIsCheckedAll(item, isChecked)
                if (addShopCheckedListener != null) {
                    addShopCheckedListener!!.updateShop(item)
                }
            notifyDataSetChanged()
        }
        // end

    }

    // 设置产品 或者商城 全选 或者 全不选择
    private fun setIsCheckedAll(item: CartShop, isChecked: Boolean) {
        item.selected = isChecked
        item.cartProduct.forEach {
            it.selected = isChecked
        }
    }

    // 根据是否产品全部选中，设置商城选中状态
    private fun shopIsChecked(item: CartShop): Boolean {
        item.cartProduct.forEach {
            if (!it.selected) {
                return false
            }
        }
        return true
    }
}