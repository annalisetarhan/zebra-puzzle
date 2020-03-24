enum class Categories(val range: IntRange) {
    NUMBER(0..4), COLOR(5..9), NATIONALITY(10..14),
    DRINK(15..19), PET(20..24), CIGARETTE(25..29)
}

enum class Number(val i: Int) { ZERO(0), ONE(1), TWO(2), THREE(3), FOUR(4) }
enum class Color(val i: Int) { RED(5), GREEN(6), IVORY(7), YELLOW(8), BLUE(9) }
enum class Nationality(val i: Int) { ENGLISHMAN(10), SPANIARD(11), UKRANIAN(12), NORWEGIAN(13), JAPANESE(14) }
enum class Drink(val i: Int) { COFFEE(15), TEA(16), MILK(17), ORANGE_JUICE(18), WATER(19) }
enum class Pet(val i: Int) { DOG(20), SNAILS(21), FOX(22), HORSE(23), ZEBRA(24) }
enum class Cigarette(val i: Int) { OLD_GOLDS(25), KOOLS(26), LUCKY_STRIKES(27), PARLIAMENTS(28), CHESTERFIELDS(29) }