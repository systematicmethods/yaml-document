package com.systemticmethods.yaml

import java.util

import org.yaml.snakeyaml.Yaml

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.util.{Failure, Success, Try}

object YamlDocument {
  /**
    * Parse ONE document only
    *
    * @param yamlstr: String
    * @return
    */
  def parseOne(yamlstr: String): Either[String, YamlDocument] = {
    val yaml = new Yaml()
    Try (yaml.load(yamlstr)) match {
      case Success(fields) =>
        fields match {
          case fields: util.LinkedHashMap[String, Object] => Right(YamlDocument(fields.asScala))
          case fields: util.ArrayList[Object] => Right(YamlDocument(fields.asScala))
          case field: String => Right(YamlDocument(field))
          case unknown: Any => Left(s"Can't recoginise yaml type ${unknown}")
        }
      case Failure(ex) => {
        Left(s"Exception ${ex.getLocalizedMessage} while parsing ${yamlstr}")
      }
    }
  }

  /**
    * Parse ONE or More documents
    *
    * @param yamlstr: String
    * @return
    */
  def parseAll(yamlstr: String): List[Either[String, YamlDocument]] = {
    val yaml = new Yaml()
    Try (yaml.loadAll(yamlstr).asScala.toList) match {
      case Success(yamldocs) => {
        yamldocs.map (doc => doc match {
          case fields: util.LinkedHashMap[String, Object] => Right(YamlDocument(fields.asScala))
          case fields: util.ArrayList[Object] => Right(YamlDocument(fields.asScala))
          case unknown: Any => Left(s"Can't recoginise yaml type ${unknown} while parsing ${yamlstr}")
        })
      }
      case Failure(ex) => {
        List(Left(s"Exception ${ex.getLocalizedMessage} while parsing ${yamlstr}"))
      }
    }
  }
}

case class YamlDocument(datum: Object) {
  val mapping: Option[mutable.Map[String, Object]] = datum match {
    case fields: mutable.Map[String, Object] => Option(fields)
    case fields: util.LinkedHashMap[String, Object] => Option(fields.asScala)
    case _ => None
  }

  val sequence: Option[mutable.Buffer[Object]] = datum match {
    case fields: mutable.Buffer[Object] => Option(fields)
    case fields: util.ArrayList[Object] => Option(fields.asScala)
    case _ => None
  }

  val iterator: Option[Iterable[Any]] = datum match {
    case fields: mutable.Map[String, Object] => Option(fields)
    case fields: util.LinkedHashMap[String, Object] => Option(fields.asScala)
    case fields: mutable.Buffer[Object] => Option(fields)
    case fields: util.ArrayList[Object] => Option(fields.asScala)
    case _ => None
  }

  def isMapping: Boolean = mapping.isDefined

  def isSequence: Boolean = sequence.isDefined

  def isScalar: Boolean = sequence.isEmpty && mapping.isEmpty

  def getMapping(field: String): Option[YamlDocument] = {
    mapping.flatMap(fld => fld.get(field).map(obj => YamlDocument(obj)))
  }

  def sequence(ix: Int): Option[YamlDocument] = {
    sequence.map(fld => YamlDocument(fld(ix)))
  }

  def get[A]: Option[A] = {
    val any: Any = datum
    any match {
      case value: A => Option(value)
      case any => None
    }
  }

//  def getLong: Option[Long] = {
//    val any: Any = datum
//    any match {
//      case value: Int => Option(value)
//      case value: Long => Option(value)
//      case any => None
//    }
//  }
//
//  def getFloat: Option[Float] = {
//    val any: Any = datum
//    any match {
//      case value: Float => Option(value)
//      case any => None
//    }
//  }
//
//  def getDouble: Option[Double] = {
//    val any: Any = datum
//    any match {
//      case value: Float => Option(value)
//      case value: Double => Option(value)
//      case any => None
//    }
//  }
//
//  def getString: Option[String] = {
//    val any: Any = datum
//    any match {
//      case value: String => Option(value)
//      case any => None
//    }
//  }

}


