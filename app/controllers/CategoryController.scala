package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json._

import scala.collection.mutable
import models.Category
import models.NewCategory

@Singleton
class CategoryController @Inject() (
    val controllerComponents: ControllerComponents
) extends BaseController {

  var categoriesList = new mutable.ListBuffer[Category]()
  implicit val categoriesListJson = Json.format[Category]
  implicit val newCategoryItem = Json.format[NewCategory]

  def getAll(): Action[AnyContent] = Action {
    if (categoriesList.isEmpty) NoContent else Ok(Json.toJson(categoriesList))
  }

  def getCategory(categoryId: Int): Action[AnyContent] = Action {
    val itemId = categoriesList.find(_.id == categoryId)
    itemId match {
      case Some(value) => Ok(Json.toJson(value))
      case None        => NotFound
    }
  }

  def deleteCategory(categoryId: Int): Action[AnyContent] = Action {
    categoriesList = categoriesList.filter(_.id != categoryId)
    NoContent
  }

  def modifyCategory(categoryId: Int): Action[JsValue] = Action(parse.json) {
    implicit request =>
      request.body
        .validate[NewCategory]
        .asOpt
        .fold {
          BadRequest(
            "Problem with validation, please check the new values and try again!"
          )
        } { response =>
          val (same, different)
              : (mutable.ListBuffer[Category], mutable.ListBuffer[Category]) =
            categoriesList.partition(_.id == categoryId)
          if (same.isEmpty) {
            NotFound
          }
          categoriesList = categoriesList.filter(_.id != categoryId)
          val newCategory =
            Category(categoryId, response.name)
          categoriesList += newCategory
          Ok(Json.toJson(newCategory))
        }
  }

  def addCategory(): Action[JsValue] = Action(parse.json) { implicit request =>
    request.body
      .validate[NewCategory]
      .asOpt
      .fold {
        BadRequest(
          "Problems with validation,  please check and try again!"
        )
      } { response =>
        val nextId = categoriesList.map(_.id).max + 1
        val newItemAdded = Category(nextId, response.name)
        categoriesList += newItemAdded
        Ok(Json.toJson(categoriesList))
      }
  }
}
