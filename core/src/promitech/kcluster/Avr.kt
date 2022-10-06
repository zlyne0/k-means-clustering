package promitech.kcluster

class Avr {
    private var first: Boolean = true
    var value: Double = 0.0
        private set

    fun add(d: Double) {
        if (first) {
            value = d
            first = false
        } else {
            value = (value + d) / 2
        }
    }

    fun reset() {
        first = true
    }
}