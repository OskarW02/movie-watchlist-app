<script setup>
import { ref, onMounted } from 'vue'
import MovieList from './components/MovieList.vue'

const movies = ref([])

onMounted(async () => {
  const response = await fetch('https://movie-watchlist-backend-qpbn.onrender.com/movies')
  movies.value = await response.json()
})
</script>

<template>
  <div>
    <h1>Movie Review App</h1>

    <MovieList :movies="movies" />
  </div>
</template>