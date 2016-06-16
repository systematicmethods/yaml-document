package com.systemticmethods.yaml

import org.junit.runner.RunWith
import org.scalatest.{BeforeAndAfter, FlatSpec}
import org.scalatest.junit.JUnitRunner

/**
  * see http://doc.scalatest.org/2.2.6/#org.scalatest.FlatSpec
  */

@RunWith(classOf[JUnitRunner])
abstract class UnitTest extends FlatSpec with BeforeAndAfter {

}
