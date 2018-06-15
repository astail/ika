package net.astail

case class Maps_ex(id: Int, name: String, statink: String)
case class Rule_ex(key: String, name: String, statink: String)
case class Result(rule: String, rule_ex: Rule_ex, maps: List[String], maps_ex: List[Maps_ex],
  start: String, start_utc: String, start_t: Int, end: String, end_utc: String, end_t: Int)


case class Weapons(id: Int, image: String, name: String)
case class Stage(image: String, name: String)
case class ResultCoop(start: String, start_utc: String, start_t: Int, end: String, end_utc: String, end_t: Int,
  stage: Stage, weapons: List[Weapons])