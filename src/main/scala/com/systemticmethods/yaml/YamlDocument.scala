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
  private val mappings: Map[String, Object] = datum match {
    case fields: mutable.Map[String, Object] => fields.toMap
    case fields: util.LinkedHashMap[String, Object] => fields.asScala.toMap
    case _ => Map[String, Object]()
  }

  private val sequences: List[Object] = datum match {
    case fields: mutable.Buffer[Object] => fields.toList
    case fields: util.ArrayList[Object] => fields.asScala.toList
    case _ => List[Object]()
  }

  val iterator: Iterable[Any] = datum match {
    case fields: mutable.Map[String, Object] => fields
    case fields: util.LinkedHashMap[String, Object] => fields.asScala
    case fields: mutable.Buffer[Object] => fields
    case fields: util.ArrayList[Object] => fields.asScala
    case any => mutable.Buffer[Object](any)
  }

  def size:Int = {
    datum match {
      case fields: mutable.Map[String, Object] => fields.size
      case fields: util.LinkedHashMap[String, Object] => fields.size()
      case fields: mutable.Buffer[Object] => fields.size
      case fields: util.ArrayList[Object] => fields.size()
      case any => 1
      case null => 0
    }
  }

  def isMapping: Boolean = mappings.nonEmpty

  def isSequence: Boolean = sequences.nonEmpty

  def isScalar: Boolean = sequences.isEmpty && mappings.isEmpty

  def get(field: String): Option[YamlDocument] = {
    mappings.get(field).map(obj => YamlDocument(obj))
  }

  def mapping[A](field: String): Option[A] = {
    get(field).flatMap(a => a.get[A])
  }

  def sequence[A](ix: Int): Option[A] = {
    lift(ix).flatMap(a => a.get[A])
  }

  def lift(ix: Int): Option[YamlDocument] = {
    sequences.lift(ix).map(item => YamlDocument(item))
  }

  def get[A]: Option[A] = {
    val any: Any = datum
    any match {
      case value: A => Option(value)
      case any => None
    }
  }

}




