fun main() {
    val solution = ZebraPuzzle().matrix
    for (i in 0..4) {
        println(
            "House ${i + 1}: "
                    + Color.values()[parse(solution, i, Categories.COLOR.range)] + " "
                    + Nationality.values()[parse(solution, i, Categories.NATIONALITY.range)] + " "
                    + Drink.values()[parse(solution, i, Categories.DRINK.range)] + " "
                    + Pet.values()[parse(solution, i, Categories.PET.range)] + " "
                    + Cigarette.values()[parse(solution, i, Categories.CIGARETTE.range)]
        )
    }
}

class ZebraPuzzle {

    val matrix = Array(30) { IntArray(30) }

    init {
        matrix.indices.forEach { connect(it, it) }                  // Matrix includes an identity diagonal (Zebra connected to zebra, water connected to water)

        connect(Number.ZERO.i, Nationality.NORWEGIAN.i)             // The Norwegian lives in the first house.
        connect(Number.TWO.i, Drink.MILK.i)                         // Milk is drunk in the middle house.
        connect(Color.RED.i, Nationality.ENGLISHMAN.i)              // The Englishman lives in the red house.
        connect(Color.GREEN.i, Drink.COFFEE.i)                      // Coffee is drunk in the green house.
        connect(Color.YELLOW.i, Cigarette.KOOLS.i)                  // Kools are smoked in the yellow house.
        connect(Nationality.SPANIARD.i, Pet.DOG.i)                  // The Spaniard owns the dog.
        connect(Nationality.UKRANIAN.i, Drink.TEA.i)                // The Ukrainian drinks tea.
        connect(Nationality.JAPANESE.i, Cigarette.PARLIAMENTS.i)    // The Japanese smokes Parliaments.
        connect(Pet.SNAILS.i, Cigarette.OLD_GOLDS.i)                // The Old Gold smoker owns snails.
        connect(Drink.ORANGE_JUICE.i, Cigarette.LUCKY_STRIKES.i)    // The Lucky Strike smoker drinks orange juice.

        disconnect(Color.BLUE.i, Nationality.NORWEGIAN.i)           // The Norwegian lives next to the blue house.
        disconnect(Pet.FOX.i, Cigarette.CHESTERFIELDS.i)            // The man who smokes Chesterfields lives in the house next to the man with the fox.
        disconnect(Pet.HORSE.i, Cigarette.KOOLS.i)                  // Kools are smoked in the house next to the house where the horse is kept.
        disconnect(Number.FOUR.i, Color.IVORY.i)                    // The green house is immediately to the right of the ivory house.
        disconnect(Number.ZERO.i, Color.GREEN.i)                    // The green house is immediately to the right of the ivory house.

        while (matrix.any { row -> row.any { it == 0 } }) {

            // Adjacency operations
            adjacent(Pet.FOX.i, Cigarette.CHESTERFIELDS.i)          // The man who smokes Chesterfields lives in the house next to the man with the fox.
            adjacent(Pet.HORSE.i, Cigarette.KOOLS.i)                // Kools are smoked in the house next to the house where the horse is kept.
            adjacent(Color.BLUE.i, Nationality.NORWEGIAN.i)         // The Norwegian lives next to the blue house.
            leftRight(Color.IVORY.i, Color.GREEN.i)                 // The green house is immediately to the right of the ivory house.

            // Logical operations
            connectTransitively()
            onlyOneRemains()
            starCrossedElements()
        }
    }

    /********************************
           LOGICAL OPERATIONS
    *********************************/

    /*
     *  If  A is connected to / disconnected from B
     *  and B is connected to / disconnected from C,
     *  then A is connected to / disconnected from C
     */

    private fun connectTransitively() {
        for (i in matrix.indices) {
            val connected = mutableListOf<Int>()
            val disconnected = mutableListOf<Int>()

            for (j in matrix.indices) {
                if (areConnected(i, j)) {
                    connected.add(j)
                } else if (areDisconnected(i, j)) {
                    disconnected.add(j)
                }
            }

            for (j in connected) {
                connected.forEach { connect(j, it) }
                disconnected.forEach { disconnect(j, it) }
            }
        }
    }

    /*
     *  If all elements in a category,
     *  except for A, are disconnected from B,
     *  then A is connected to B.
     */

    private fun onlyOneRemains() {
        for (i in matrix.indices) {
            for (j in matrix.indices) {
                if (!areConnected(i, j) && !areDisconnected(i, j)) {
                    val others = othersInCategory(j)
                    if (others.all { areDisconnected(it, i) }) {
                        connect(i, j)
                    }
                }
            }
        }
    }

    /*
     *  If two elements in different categories, A and B,
     *  have no potential common connection in a third category,
     *  they must be disconnected.
     *
     *  In other words, there are four contestants in a pie contest.
     *  Annie and Betty did not enter chocolate pies.
     *  Celia and Delia did not win first prize.
     *  So, the chocolate pie did not take first prize.
     */

    private fun starCrossedElements() {
        for (i in 0..29) {          // Each element
            for (j in 0..29) {      // Each element...
                if (j == i || j in othersInCategory(i)) break    // ...except for those in the same category as i
                for (k in 0..5) {                           // Each category...
                    val category = Categories.values()[k].range
                    if (category.contains(i) || category.contains(j)) break     // ...except for those that contain i or j
                    if (category.all { areDisconnected(it, i) || areDisconnected(it, j) }) {
                        disconnect(i, j)
                        break
                    }
                }
            }
        }
    }

    /********************************
           ADJACENCY OPERATIONS
    *********************************/

    private fun adjacent(a: Int, b: Int) {
        adjacentHelper(a, b)
        adjacentHelper(b, a)
    }

    private fun adjacentHelper(a: Int, b: Int) {
        val aHouseNum = getHouseNum(a)
        if (aHouseNum != -99) {
            tryToPutBNextToA(b, aHouseNum)
        }
    }

    private fun tryToPutBNextToA(house: Int, adjacentHouse: Int) {
        val potentialLeft = adjacentHouse - 1
        val potentialRight = adjacentHouse + 1

        val leftIsValid = potentialLeft.isValidHouseNum() && potentialLeft.hasNoMatchIn(othersInCategory(house))
        val rightIsValid = potentialRight.isValidHouseNum() && potentialRight.hasNoMatchIn(othersInCategory(house))

        if (leftIsValid && !rightIsValid) {
            connect(house, potentialLeft)
        } else if (rightIsValid && !leftIsValid) {
            connect(house, potentialRight)
        }
    }

    private fun leftRight(left: Int, right: Int) {
        val leftHouseNum = getHouseNum(left)
        if (leftHouseNum != -99) {
            connect(right, leftHouseNum + 1)
            return
        }

        val rightHouseNum = getHouseNum(right)
        if (rightHouseNum != -99) {
            connect(left, rightHouseNum - 1)
            return
        }

        val leftCantBe = matrix[left].filter { it == -1 }.filterIndexed { index, _ -> index.isValidHouseNum() }
        val soRightCantBe = leftCantBe.map { it + 1 }.filter { it.isValidHouseNum() }
        soRightCantBe.forEach {
            disconnect(right, it)
        }

        val rightCantBe = matrix[right].filter { it == -1 }.filterIndexed { index, _ -> index.isValidHouseNum() }
        val soLeftCantBe = rightCantBe.map { it + 1 }.filter { it.isValidHouseNum() }
        soLeftCantBe.forEach {
            disconnect(left, it)
        }
    }

    /********************************
             UTILITY FUNCTIONS
    *********************************/

    private fun connect(a: Int, b: Int) {
        if (areConnected(a, b)) return
        if (areDisconnected(a, b)) print("OH NO")
        matrix[a][b] = 1
        matrix[b][a] = 1
        disconnectOthers(a, b)
        disconnectOthers(b, a)
    }

    private fun disconnect(a: Int, b: Int) {
        if (areConnected(a, b)) print("OH NO")
        matrix[a][b] = -1
        matrix[b][a] = -1
    }

    private fun disconnectOthers(a: Int, b: Int) {
        for (other in othersInCategory(a)) {
            disconnect(other, b)
        }
    }

    private fun areConnected(a: Int, b: Int): Boolean {
        return matrix[a][b] == 1
    }

    private fun areDisconnected(a: Int, b: Int): Boolean {
        return matrix[a][b] == -1
    }

    private fun getHouseNum(i: Int): Int {
        for (n in 0..4) {
            if (areConnected(i, n)) return n
        }
        return -99
    }

    private fun othersInCategory(i: Int): List<Int> {
        return when (i) {
            in 0..4 -> (0..4).minus(i)
            in 5..9 -> (5..9).minus(i)
            in 10..14 -> (10..14).minus(i)
            in 15..19 -> (15..19).minus(i)
            in 20..24 -> (20..24).minus(i)
            in 25..29 -> (25..29).minus(i)
            else -> emptyList()
        }
    }

    private fun Int.isValidHouseNum() = this in (0..4)
    private fun Int.hasNoMatchIn(others: List<Int>): Boolean = others.none { areConnected(this, it) }
}

fun parse(solution: Array<IntArray>, houseNum: Int, categoryRange: IntRange): Int {
    for (i in categoryRange) {
        if (solution[houseNum][i] == 1)
            return i % categoryRange.first
    }
    error("Invalid solution")
}