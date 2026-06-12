package com.manish.wordhaven.data.datasource

import com.manish.wordhaven.domain.model.Level

interface LevelDataSource {
    suspend fun getLevels(): List<Level>
    suspend fun getLevelById(id: Int): Level?
}
