package com.curidev.ayni.feature_product.data.remote

import com.curidev.ayni.core_network.RetrofitFactory

class ProductServiceFactory private constructor() {
    companion object {
        fun getProductService(): ProductService {
            return RetrofitFactory.getRetrofit().create(ProductService::class.java)
        }
    }
}