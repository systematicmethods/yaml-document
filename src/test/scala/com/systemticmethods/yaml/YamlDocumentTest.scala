package com.systemticmethods.yaml

class YamlDocumentTest extends UnitTest {

  behavior of "Yaml Document"

  it should "read Example 2.1. Sequence of Scalars" in {
    val yamlstr = """- Mark McGwire
                    |- Sammy Sosa
                    |- Ken Griffey
                  """.stripMargin
    val doc = YamlDocument.parseOne(yamlstr)
    assert(doc != null)
    doc match {
      case Left(error) => fail(error)
      case Right(adoc) =>
        assert(adoc.isSequence)
        adoc.sequence match {
          case Some(arr) => {
            assertResult(arr(0)) {
              "Mark McGwire"
            }
            assertResult(arr(1)) {
              "Sammy Sosa"
            }
            assertResult(arr(2)) {
              "Ken Griffey"
            }
          }
          case None => fail("Should not be None")
        }
        val str = adoc.iterator.get.mkString(",")
        assert(str == "Mark McGwire,Sammy Sosa,Ken Griffey")
    }
  }

  it should "read Example 2.2.  Mapping Scalars to Scalars" in {
    val yamlstr = """hr:  65    # Home runs
                    |avg: 0.278 # Batting average
                    |rbi: 147   # Runs Batted In""".stripMargin
    val doc = YamlDocument.parseOne(yamlstr)
    assert(doc != null)
    doc match {
      case Left(error) => fail(error)
      case Right(doc) =>
        assert(doc.isMapping)
        doc.mapping.map(achunk => achunk("hr") == 65)
        doc.mapping.map(achunk => achunk("avg") == 0.278)
        doc.mapping.map(achunk => achunk("rbi") == 147)
    }
  }

  it should "read Example 2.2.  Mapping Scalars to Scalars as an iterator" in {
    val yamlstr = """hr:  65    # Home runs
                    |avg: 0.278 # Batting average
                    |rbi: 147   # Runs Batted In""".stripMargin
    val doc = YamlDocument.parseOne(yamlstr)
    assert(doc != null)
    doc match {
      case Left(error) => fail(error)
      case Right(adoc) =>
        assert(adoc.isMapping)
        val str = adoc.iterator.get.mkString(",")
        assert(str ==
          "hr -> 65,avg -> 0.278,rbi -> 147")
    }
  }

  it should "read Example 2.3.  Mapping Scalars to Sequences" in {
    val yamlstr = """american:
                    |  - Boston Red Sox
                    |  - Detroit Tigers
                    |  - New York Yankees
                    |national  :
                    |  - New York Mets
                    |  - Chicago Cubs
                    |  - Atlanta Braves""".stripMargin
    val doc = YamlDocument.parseOne(yamlstr)
    assert(doc != null)
    doc match {
      case Left(error) => fail(error)
      case Right(adoc) =>
        assert(adoc.isMapping)
        val americanopt = adoc.getMapping("american")
        //println(s"americanopt ${americanopt} Seq ${americanopt.get.datum.getClass.getName}")
        assert(americanopt.isDefined)
        assert(americanopt.get.isSequence)
        assert(americanopt.get.sequence.get.length == 3)
        val DetroitTigers = americanopt.flatMap(opt => opt.getSequence(1))
        assert(DetroitTigers.isDefined)
        assert(DetroitTigers.get.datum == "Detroit Tigers")
        val nationalopt = adoc.getMapping("national")
        assert(nationalopt.isDefined)
        assert(nationalopt.get.isSequence)
        assert(nationalopt.get.sequence.get.length == 3)
        // get a list of nations
        val nations = nationalopt.get.sequence.get.toList
        assert(nations == List("New York Mets", "Chicago Cubs", "Atlanta Braves"))
      //americanopt.map(opt => opt.)
    }
  }

  it should "read Example 2.4.  Sequence of Mappings" in {
    val yamlstr = """-
                    |  name: Mark McGwire
                    |  hr:   65
                    |  avg:  0.278
                    |-
                    |  name: Sammy Sosa
                    |  hr:   63
                    |  avg:  0.288""".stripMargin
    val doc = YamlDocument.parseOne(yamlstr)
    assert(doc != null)
    doc match {
      case Left(error) => fail(error)
      case Right(doc) => assert(doc.isSequence)
    }
  }

  it should "fail to read Example 2.4.  Sequence of Mappings" in {
    val yamlstr = """-
                    |  name: Mark McGwire
                    |  hr:   65
                    |  avg:  0.278
                    |    some jumk:
                    |-
                    |  name: Sammy Sosa
                    |  hr:   63
                    |  avg:  0.288""".stripMargin
    val doc = YamlDocument.parseOne(yamlstr)
    assert(doc != null)
    doc match {
      case Left(error) => assert(error.length > 0)
      case Right(doc) => {
        assert(doc.isSequence)
        fail(s"Should not parse ${yamlstr}")
      }
    }
  }

  it should "read Example 2.5. Sequence of Sequences" in {
    val yamlstr = """- [name        , hr, avg  ]
                    |- [Mark McGwire, 65, 0.278]
                    |- [Sammy Sosa  , 63, 0.288]""".stripMargin
    val doc = YamlDocument.parseOne(yamlstr)
    assert(doc != null)
    doc match {
      case Left(error) => fail(error)
      case Right(doc) => assert(doc.isSequence)
    }
  }

  it should "read Example 2.6. Mapping of Mappings" in {
    val yamlstr = """Mark McGwire: {hr: 65, avg: 0.278}
                    |Sammy Sosa: {
                    |    hr: 63,
                    |    avg: 0.288
                    |  }""".stripMargin
    val doc = YamlDocument.parseOne(yamlstr)
    assert(doc != null)
    doc match {
      case Left(error) => fail(error)
      case Right(doc) => assert(doc.isMapping)
    }
  }

  it should "read Example 2.7.  Two Documents in a Stream" in {
    val yamlstr = """# Ranking of 1998 home runs
                    |---
                    |- Mark McGwire
                    |- Sammy Sosa
                    |- Ken Griffey
                    |
                    |# Team ranking
                    |---
                    |- Chicago Cubs
                    |- St Louis Cardinals""".stripMargin
    val docs = YamlDocument.parseAll(yamlstr)
    assert(docs != null)
    docs.foreach(either => either match {
        case Left(error) => fail(error)
        case Right(doc) => assert(doc.isSequence)
      }
    )
  }

  it should "read Example 2.8.  Play by Play Feed from a Game" in {
    val yamlstr = """---
                    |time: 20:03:20
                    |player: Sammy Sosa
                    |action: strike (miss)
                    |...
                    |---
                    |time: 20:03:47
                    |player: Sammy Sosa
                    |action: grand slam
                    |...""".stripMargin
    val docs = YamlDocument.parseAll(yamlstr)
    assert(docs != null)
    docs.foreach(either => either match {
        case Left(error) => fail(error)
        case Right(doc) => assert(doc.isMapping)
      }
    )
  }

  it should "fail to read Example 2.8.  Play by Play Feed from a Game with yaml error " in {
    val yamlstr = """---
                    |time: 20:03:20
                    |player: Sammy Sosa
                    |  some junk:
                    |action: strike (miss)
                    |...
                    |---
                    |time: 20:03:47
                    |player: Sammy Sosa
                    |action: grand slam
                    |...""".stripMargin
    val docs = YamlDocument.parseAll(yamlstr)
    assert(docs != null)
    docs.map(either => either match {
      case Left(error) => assert(error.length > 0)
      case Right(doc) => {
        assert(doc.isMapping)
        fail(s"Should not parse ${yamlstr}")
      }
    }
    )
  }
  it should "read Example 2.9.  Single Document with Two Comments" in {
    val yamlstr = """---
                    |hr: # 1998 hr ranking
                    |  - Mark McGwire
                    |  - Sammy Sosa
                    |rbi:
                    |  # 1998 rbi ranking
                    |  - Sammy Sosa
                    |  - Ken Griffey""".stripMargin
    val doc = YamlDocument.parseOne(yamlstr)
    assert(doc != null)
    doc match {
      case Left(error) => fail(error)
      case Right(doc) => assert(doc.isMapping)
    }
  }

  it should "read Example 2.10.  Node for “Sammy Sosa nappears twice in this document" in {
    val yamlstr = """---
                    |hr:
                    |  - Mark McGwire
                    |  # Following node labeled SS
                    |  - &SS Sammy Sosa
                    |rbi:
                    |  - *SS # Subsequent occurrence
                    |  - Ken Griffey""".stripMargin
    val doc = YamlDocument.parseOne(yamlstr)
    assert(doc != null)
    doc match {
      case Left(error) => fail(error)
      case Right(doc) => assert(doc.isMapping)
    }
  }

  it should "read Example 2.11. Mapping between Sequences" in {
    val yamlstr = """? - Detroit Tigers
                    |  - Chicago cubs
                    |:
                    |  - 2001-07-23
                    |
                    |? [ New York Yankees,
                    |    Atlanta Braves ]
                    |: [ 2001-07-02, 2001-08-12,
                    |    2001-08-14 ]""".stripMargin
    val doc = YamlDocument.parseOne(yamlstr)
    assert(doc != null)
    doc match {
      case Left(error) => fail(error)
      case Right(doc) => assert(doc.isMapping)
    }
  }

  it should "read Example 2.12. Compact Nested Mapping" in {
    val yamlstr = """---
                    |# Products purchased
                    |- item    : Super Hoop
                    |  quantity: 1
                    |- item    : Basketball
                    |  quantity: 4
                    |- item    : Big Shoes
                    |  quantity: 1""".stripMargin
    val doc = YamlDocument.parseOne(yamlstr)
    assert(doc != null)
    doc match {
      case Left(error) => fail(error)
      case Right(doc) => assert(doc.isSequence)
    }
  }

  it should "read Example 2.13.  In literals, newlines are preserved" in {
    val yamlstr = """# ASCII Art
                    ;--- |
                    ;  \//||\/||
                    ;  // ||  ||__""".stripMargin(';')
    val doc = YamlDocument.parseOne(yamlstr)
    assert(doc != null)
    doc match {
      case Left(error) => fail(error)
      case Right(doc) => assert(doc.isScalar)
    }
  }

  it should "read Example 2.14.  In the folded scalars, newlines become spaces" in {
    val yamlstr = """--- >
                    |  Mark McGwire's
                    |  year was crippled
                    |  by a knee injury.""".stripMargin
    val doc = YamlDocument.parseOne(yamlstr)
    assert(doc != null)
    doc match {
      case Left(error) => fail(error)
      case Right(doc) => assert(doc.isScalar)
    }
  }

  it should "read Example 2.15.  Folded newlines are preserved for 'more indented' and blank lines" in {
    val yamlstr = """>
                    | Sammy Sosa completed another
                    | fine season with great stats.
                    |
                    |   63 Home Runs
                    |   0.288 Batting Average
                    |
                    | What a year!""".stripMargin
    val doc = YamlDocument.parseOne(yamlstr)
    assert(doc != null)
    doc match {
      case Left(error) => fail(error)
      case Right(doc) => assert(doc.isScalar)
    }
  }

  it should "read Example 2.16.  Indentation determines scope" in {
    val yamlstr = """name: Mark McGwire
                    |accomplishment: >
                    |  Mark set a major league
                    |  home run record in 1998.
                    |stats: |
                    |  65 Home Runs
                    |  0.278 Batting Average""".stripMargin
    val doc = YamlDocument.parseOne(yamlstr)
    assert(doc != null)
    doc match {
      case Left(error) => fail(error)
      case Right(doc) => assert(doc.isMapping)
    }
  }

  it should "read Example 2.17. Quoted Scalars" in {
    val yamlstr = """unicode: "Sosa did fine.\u263A"
                    |control: "\b1998\t1999\t2000\n"
                    |hex esc: "\x0d\x0a is \r\n"
                    |
                    |single: '"Howdy!" he cried.'
                    |quoted: ' # Not a ''comment''.'
                    |tie-fighter: '|\-*-/|'""".stripMargin
    val doc = YamlDocument.parseOne(yamlstr)
    assert(doc != null)
    doc match {
      case Left(error) => fail(error)
      case Right(doc) => assert(doc.isMapping)
    }
  }

  it should "read Example 2.18. Multi-line Flow Scalars" in {
    val yamlstr = """plain:
                    |  This unquoted scalar
                    |  spans many lines.
                    |
                    |quoted: "So does this
                    |  quoted scalar.\n"""".stripMargin
    val doc = YamlDocument.parseOne(yamlstr)
    assert(doc != null)
    doc match {
      case Left(error) => fail(error)
      case Right(doc) => assert(doc.isMapping)
    }
  }

  it should "read Example 2.27. Invoice" in {
    val yamlstr = """---
                    |invoice: 34843
                    |date   : 2001-01-23
                    |bill-to: &id001
                    |    given  : Chris
                    |    family : Dumars
                    |    address:
                    |        lines: |
                    |            458 Walkman Dr.
                    |            Suite #292
                    |        city    : Royal Oak
                    |        state   : MI
                    |        postal  : 48046
                    |ship-to: *id001
                    |product:
                    |    - sku         : BL394D
                    |      quantity    : 4
                    |      large       : 17179869184
                    |      description : Basketball
                    |      price       : 450.00
                    |    - sku         : BL4438H
                    |      quantity    : 1
                    |      description : Super Hoop
                    |      price       : 2392.00
                    |tax  : 251.42
                    |total: 4443.52
                    |comments:
                    |    Late afternoon is best.
                    |    Backup contact is Nancy
                    |    Billsmer @ 338-4338.""".stripMargin
    val doc = YamlDocument.parseOne(yamlstr)
    assert(doc != null)
    doc match {
      case Left(error) => fail(error)
      case Right(doc) =>
        assert(doc.isMapping)
        val product: Option[YamlDocument] = doc.getMapping("product")
        //println(s"product $product")
        //println(s"product ${product.get.datum.getClass.getName}")
        assert(product.isDefined)
        assert(product.get.isSequence)
        //product.foreach(obj => println(s"obj $obj"))
        val res = product.map(obj => obj.isSequence)
        assert(res.get == true)
        val sku = product.get.getSequence(0)
        assert(sku.isDefined)
        assert(sku.get.isMapping)
        assert(sku.get.getMapping("sku").get.isScalar)
        assert(sku.get.getMapping("sku").get.datum == "BL394D")
        assert(sku.get.getMapping("quantity").get.isScalar)
        assert(sku.get.getMapping("quantity").get.get[Int].get == 4)
        // either
        val quantity: Int = doc.getMapping("product").flatMap(prod => prod.getSequence(0).flatMap(sku0 => sku0.getMapping("quantity").flatMap(qty => qty.get[Int]))).get
        assert(quantity == 4)
        // or
        val qty: Option[Int] = for {
          prod <- doc.getMapping("product")
          skuk0 <- prod.getSequence(0)
          qty <- skuk0.getMapping("quantity")
          qtyi <- qty.get[Int]
        } yield {
          qtyi
        }
        assert(qty.get == 4)

    }
  }

}
