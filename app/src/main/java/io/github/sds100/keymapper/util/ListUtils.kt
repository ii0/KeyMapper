package io.github.sds100.keymapper.util

/**
 * Created by sds100 on 11/03/2021.
 */

fun <T> MutableList<T>.move(from: Int, to: Int) {
    if (from == to)
        return

    val element = this.removeAt(from) ?: return
    this.add(to, element)
}

inline fun <reified T> Array<out T>.splitIntoBatches(batchSize: Int): Array<Array<out T>> {
    var arrayToSplit = this

    var batches: Array<Array<out T>> = arrayOf()

    while (arrayToSplit.isNotEmpty()) {
        val batch = if (arrayToSplit.size < batchSize) {
            arrayToSplit
        } else {
            arrayToSplit.sliceArray(0 until batchSize)
        }

        batches = batches.plus(batch)

        arrayToSplit = arrayToSplit.sliceArray(batch.size until arrayToSplit.size)
    }

    return batches
}