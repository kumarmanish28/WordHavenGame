package com.manish.wordhaven.di

import com.manish.wordhaven.data.datasource.LevelDataSource
import com.manish.wordhaven.data.datasource.LocalLevelDataSource
import com.manish.wordhaven.data.datasource.PreferenceDataSource
import com.manish.wordhaven.data.datasource.PreferenceDataSourceImpl
import com.manish.wordhaven.data.repository.GameRepository
import com.manish.wordhaven.data.repository.GameRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindLevelDataSource(
        localLevelDataSource: LocalLevelDataSource
    ): LevelDataSource

    @Binds
    @Singleton
    abstract fun bindPreferenceDataSource(
        preferenceDataSourceImpl: PreferenceDataSourceImpl
    ): PreferenceDataSource

    @Binds
    @Singleton
    abstract fun bindGameRepository(
        gameRepositoryImpl: GameRepositoryImpl
    ): GameRepository
}
