package com.setyo.similartext

import java.io.Serializable

data class PendaftaranSkripsi (
    val tb_skripsi: List<Data>
) {
    data class Data(
        val id_skripsi: Int, val judul : String
    ) : Serializable
}
