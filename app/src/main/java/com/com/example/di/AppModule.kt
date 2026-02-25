package com.com.example.di

import android.content.Context
import androidx.room.Room
import com.com.example.data.local.GroceryDao
import com.com.example.data.local.GroceryDatabase
import com.com.example.data.repository.GroceryRepositoryImpl
import com.com.example.domain.repository.GroceryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GroceryDatabase =
        Room.databaseBuilder(
            context,
            GroceryDatabase::class.java,
            "grocery_db"
        ).fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideGroceryDao(database: GroceryDatabase): GroceryDao =
        database.groceryDao()

    @Provides
    @Singleton
    fun provideGroceryRepository(dao: GroceryDao): GroceryRepository =
        GroceryRepositoryImpl(dao)
}

