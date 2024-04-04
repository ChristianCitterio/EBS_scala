package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json._
import scala.collection.mutable
import models.Product
import models.NewProduct

@Singleton
class ProductController @Inject() (
    val controllerComponents: ControllerComponents
) extends BaseController {
  var productsList = new mutable.ListBuffer[Product]()
  implicit val productsListJson = Json.format[Product]
  implicit val newProductItem = Json.format[NewProduct]

  def getAll(): Action[AnyContent] = Action {
    if (productsList.isEmpty) NoContent else Ok(Json.toJson(productsList))
  }

  def getProduct(productId: Int): Action[AnyContent] = Action {
    val itemId = productsList.find(_.id == productId)
    itemId match {
      case Some(value) => Ok(Json.toJson(value))
      case None        => NotFound
    }
  }

  def deleteProduct(productId: Int): Action[AnyContent] = Action {
    productsList = productsList.filter(_.id != productId)
    NoContent
  }

  def modifyProduct(productId: Int): Action[JsValue] = Action(parse.json) {
    implicit request =>
      request.body
        .validate[NewProduct]
        .asOpt
        .fold {
          BadRequest(
            "Problems with validation, please check the new values and try again!"
          )
        } { response =>
          val (same, different)
              : (mutable.ListBuffer[Product], mutable.ListBuffer[Product]) =
            productsList.partition(_.id == productId)
          if (same.isEmpty) {
            NotFound
          }
          productsList = productsList.filter(_.id != productId)
          val newProduct =
            Product(productId, response.name, response.price, response.category)
          productsList += newProduct
          Ok(Json.toJson(newProduct))
        }
  }

  def addProduct(): Action[JsValue] = Action(parse.json) { implicit request =>
    request.body
      .validate[NewProduct]
      .asOpt
      .fold {
        BadRequest(
          "Problems with validation, please check and try again!"
        )
      } { response =>
        val nextId = productsList.map(_.id).max + 1
        val newItemAdded =
          Product(nextId, response.name, response.price, response.category)
        productsList += newItemAdded
        Ok(Json.toJson(productsList))
      }
  }
}
