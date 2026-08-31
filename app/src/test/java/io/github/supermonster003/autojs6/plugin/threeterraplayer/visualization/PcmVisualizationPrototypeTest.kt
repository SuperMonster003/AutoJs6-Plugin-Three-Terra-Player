package io.github.supermonster003.autojs6.plugin.threeterraplayer.visualization

import kotlin.math.abs
import kotlin.math.sqrt
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Test-only prototype for the cheapest permission-free visualization signal. */
class PcmVisualizationPrototypeTest {

    @Test
    fun fixedBucketsProduceBoundedRmsWithoutDependingOnPlaybackUi() {
        val probe = Pcm16RmsPrototype(bucketSamples = 128)
        val silence = probe.analyze(ShortArray(256))
        val halfScaleSquareWave = probe.analyze(
            ShortArray(256) { index -> if (index % 2 == 0) 16_384 else -16_384 },
        )

        assertEquals(listOf(0f, 0f), silence)
        assertEquals(2, halfScaleSquareWave.size)
        halfScaleSquareWave.forEach { rms ->
            assertTrue(rms in 0f..1f)
            assertTrue(abs(rms - 0.5f) < 0.001f)
        }
    }

    @Test
    fun finalPartialBucketIsRetained() {
        val result = Pcm16RmsPrototype(bucketSamples = 4).analyze(
            shortArrayOf(Short.MAX_VALUE, Short.MAX_VALUE, Short.MAX_VALUE, Short.MAX_VALUE, 0),
        )

        assertEquals(2, result.size)
        assertTrue(result.first() > 0.99f)
        assertEquals(0f, result.last())
    }

    private class Pcm16RmsPrototype(private val bucketSamples: Int) {
        init {
            require(bucketSamples > 0)
        }

        fun analyze(samples: ShortArray): List<Float> = buildList {
            var offset = 0
            while (offset < samples.size) {
                val end = (offset + bucketSamples).coerceAtMost(samples.size)
                var squareSum = 0.0
                for (index in offset until end) {
                    val normalized = samples[index] / Short.MAX_VALUE.toDouble()
                    squareSum += normalized * normalized
                }
                add(sqrt(squareSum / (end - offset)).toFloat().coerceIn(0f, 1f))
                offset = end
            }
        }
    }
}
