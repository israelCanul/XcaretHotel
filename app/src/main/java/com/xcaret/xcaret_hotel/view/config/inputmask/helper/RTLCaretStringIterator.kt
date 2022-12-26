package com.xcaret.xcaret_hotel.view.config.inputmask.helper

import com.xcaret.xcaret_hotel.view.config.inputmask.model.CaretString

class RTLCaretStringIterator(caretString: CaretString) : CaretStringIterator(caretString) {
    override fun insertionAffectsCaret(): Boolean {
        return this.currentIndex <= this.caretString.caretPosition
    }
}