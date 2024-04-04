package models

import models.Product

final case class ShopCart(id: Int, products: List[Product])
