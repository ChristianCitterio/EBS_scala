package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json._

import scala.collection.mutable
import models.Product
import models.ShopCart
import models.NewShopCart

@Singleton
class ShopCartController @Inject() (
    val controllerComponents: ControllerComponents
) extends BaseController {
  var shopCartsList = new mutable.ListBuffer[ShopCart]()
  implicit val productsListJson = Json.format[Product]
  implicit val shopCartsListJson = Json.format[ShopCart]
  implicit val newShopCartItem = Json.format[NewShopCart]

  def getAll(): Action[AnyContent] = Action {
    if (shopCartsList.isEmpty) NoContent else Ok(Json.toJson(shopCartsList))
  }

  def getShopCart(shopCartId: Int): Action[AnyContent] = Action {
    val itemId = shopCartsList.find(_.id == shopCartId)
    itemId match {
      case Some(value) => Ok(Json.toJson(value))
      case None        => NotFound
    }
  }

  def deleteShopCart(shopCartId: Int): Action[AnyContent] = Action {
    shopCartsList = shopCartsList.filter(_.id != shopCartId)
    NoContent
  }

  def modifyShopCart(shopCartId: Int): Action[JsValue] = Action(parse.json) {
    implicit request =>
      request.body
        .validate[NewShopCart]
        .asOpt
        .fold {
          BadRequest(
            "Problems with validation, please check the new values and try again!"
          )
        } { response =>
          val (same, different)
              : (mutable.ListBuffer[ShopCart], mutable.ListBuffer[ShopCart]) =
            shopCartsList.partition(_.id == shopCartId)
          if (same.isEmpty) {
            NotFound
          }
          shopCartsList = shopCartsList.filter(_.id != shopCartId)
          val newShopCart =
            ShopCart(
              shopCartId,
              response.products
            )
          shopCartsList += newShopCart
          Ok(Json.toJson(newShopCart))
        }
  }

  def addShopCart(): Action[JsValue] = Action(parse.json) { implicit request =>
    request.body
      .validate[NewShopCart]
      .asOpt
      .fold {
        BadRequest(
          "Problems with validation, please check and try again!"
        )
      } { response =>
        val nextId = shopCartsList.map(_.id).max + 1
        val newItemAdded =
          ShopCart(nextId, response.products)
        shopCartsList += newItemAdded
        Ok(Json.toJson(shopCartsList))
      }
  }
}
