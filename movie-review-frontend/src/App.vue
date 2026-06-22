<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import MovieList from './components/MovieList.vue'

const API_URL = 'https://movie-watchlist-backend-vao3.onrender.com/movies'

const movies = ref([])

const newMovie = ref({
  title: '',
  rating: 1,
  releaseYear: new Date().getFullYear()
})

// GET: Filme aus Backend/DB laden
const loadMovies = async () => {
  try {
    const response = await axios.get(API_URL)
    movies.value = response.data
  } catch (error) {
    console.error('Fehler beim Laden der Filme:', error)
  }
}

// POST: Neuen Film speichern
const addMovie = async () => {
  try {
    const response = await axios.post(API_URL, newMovie.value)

    movies.value.push(response.data)

    newMovie.value = {
      title: '',
      rating: 1,
      releaseYear: new Date().getFullYear()
    }
  } catch (error) {
    console.error('Fehler beim Speichern des Films:', error)
  }
}

// DELETE: Film löschen
const deleteMovie = async (id) => {
  try {
    await axios.delete(`${API_URL}/${id}`)

    movies.value = movies.value.filter(movie => movie.id !== id)
  } catch (error) {
    console.error('Fehler beim Löschen des Films:', error)
  }
}

onMounted(() => {
  loadMovies()
})
</script>

<template>
  <div>
    <h1>Movie Review App</h1>

    <form @submit.prevent="addMovie">
      <div>
        <label>Titel:</label>
        <input v-model="newMovie.title" type="text" required />
      </div>

      <div>
        <label>Release Year:</label>
        <input v-model.number="newMovie.releaseYear" type="number" required />
      </div>

      <div>
        <label>Rating:</label>
        <input v-model.number="newMovie.rating" type="number" min="1" max="5" required />
      </div>

      <button type="submit">Film hinzufügen</button>
    </form>

    <MovieList
      :movies="movies"
      @delete-movie="deleteMovie"
    />
  </div>
</template>