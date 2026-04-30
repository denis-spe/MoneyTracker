// Hear oh Israel, The LORD our GOD, The LORD is one, You shall love the
// LORD your GOD with all your soul and with all your might and with all your strength
// and love your neighbor as your self.
package com.example.moneytracker.backend.storage


sealed class DataAdjust {
    data class Data(val finance: Finance) : DataAdjust()
    data class Adjust(val adjustment: Adjustment) : DataAdjust()
}