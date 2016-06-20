# Yaml Document
Yaml Document provides a generic type safe Scala interface around Yaml documents
parsed using the snakeyaml library. There are no mappings or implicits for
conversion to case classes as the idea is to process Yaml documents
in a generic manner taking inspration from generic processing using Avro.
Although in this case there is no embedded schema.

This means
= Yaml documents that have fields added won't break code
* unwanted Yaml fields can be ignored
* Fields can be accessed with iterators

## Example 1

For example with this Yaml string
```
american:
  - Boston Red Sox
  - Detroit Tigers
  - New York Yankees
national  :
  - New York Mets
  - Chicago Cubs
  - Atlanta Braves
```
Can be accessed as follows
```
val doc = YamlDocument.parseOne(yamlstr)
val mapping = doc.getMapping("american")
```
to return an `Option[YamlDocument]` which can be subsequently queried.

## Example 2

The following yaml document
```
product:
    - sku         : BL394D
      quantity    : 4
      large       : 17179869184
      description : Basketball
      price       : 450.00
    - sku         : BL4438H
      quantity    : 1
      description : Super Hoop
      price       : 2392.00
```
can be accessed as follows using for comprehension
```
val qty: Option[Int] = for {
     prod <- doc.getMapping("product")
     skuk0 <- prod.getSequence(0)
     qty <- skuk0.getMapping("quantity")
     qtyi <- qty.getInt
   } yield {
     qtyi
   }
```

## Example 3 Iteration

TBC



