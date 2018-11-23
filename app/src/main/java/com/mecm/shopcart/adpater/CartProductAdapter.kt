package com.mecm.shopcart.adpater

import android.widget.CheckBox
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.mecm.shopcart.R
import com.mecm.shopcart.entity.CartProduct
import java.text.DecimalFormat

// 购物车 商城产品列表
class CartProductAdapter(dataList: ArrayList<CartProduct>) : BaseQuickAdapter<CartProduct, BaseViewHolder>(R.layout.item_cart_product, dataList) {

    // 产品 更改事件
    interface AddShopProductCheckedListener {
        fun shopProductChecked(cartProduct: CartProduct)
    }

    var addShopProductCheckedListener: AddShopProductCheckedListener? = null

    override fun convert(helper: BaseViewHolder, item: CartProduct) {
        helper.setText(R.id.item_order_list_pro_name, item.name)
        helper.setText(R.id.item_order_list_pro_price, DecimalFormat("0.00").format(item.price).toString())

        // 设置产品的选中事件
        val productCb = helper.getView<CheckBox>(R.id.item_cart_product_cb)
        productCb.isChecked = item.selected
        productCb.setOnCheckedChangeListener { _, isChecked ->
            if (addShopProductCheckedListener != null) {
                item.selected = isChecked
                addShopProductCheckedListener!!.shopProductChecked(item)
            }
            notifyDataSetChanged()
        }
        // end
    }

}