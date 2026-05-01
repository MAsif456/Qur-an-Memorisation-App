package com.example.mob_dev_portfolio.domain.recitation

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ArabicNormaliserTest {

    @Test
    fun `empty input returns empty string`() {
        assertThat(ArabicNormaliser.normalise("")).isEmpty()
    }

    @Test
    fun `strips fatha kasra damma sukun shadda tanween`() {
        val input = "بِسْمِ اللَّهِ"
        val output = ArabicNormaliser.normalise(input)
        assertThat(output).isEqualTo("بسم الله")
    }

    @Test
    fun `strips tatweel`() {
        val input = "اللـه"
        val output = ArabicNormaliser.normalise(input)
        assertThat(output).isEqualTo("الله")
    }

    @Test
    fun `folds hamza variants to bare alif`() {
        val input = "أ إ آ ٱ"
        val output = ArabicNormaliser.normalise(input)
        assertThat(output).isEqualTo("ا ا ا ا")
    }

    @Test
    fun `folds ta marbouta to ha and alif maqsura to ya`() {
        val input = "صلاة موسى"
        val output = ArabicNormaliser.normalise(input)
        assertThat(output).isEqualTo("صلاه موسي")
    }

    @Test
    fun `collapses repeated whitespace`() {
        val input = "  بسم   الله  "
        val output = ArabicNormaliser.normalise(input)
        assertThat(output).isEqualTo("بسم الله")
    }

    @Test
    fun `words returns empty list for empty input`() {
        assertThat(ArabicNormaliser.words("")).isEmpty()
    }

    @Test
    fun `words splits on whitespace after normalisation`() {
        val tokens = ArabicNormaliser.words("بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ")
        assertThat(tokens).containsExactly("بسم", "الله", "الرحمن").inOrder()
    }
}
