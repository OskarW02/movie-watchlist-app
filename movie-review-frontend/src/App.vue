<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import MovieList from './components/MovieList.vue'

const API_URL = 'https://movie-watchlist-backend-vao3.onrender.com/movies'

const movies = ref([])

const newMovie = ref({
  title: '',
  rating: '0.0',
  releaseYear: new Date().getFullYear(),
  criticRating: null,
  externalId: '',
  watched: false,
  comment: ''
})

function parseRating(value) {
  const number = Number(String(value).replace(',', '.'))

  if (Number.isNaN(number)) {
    return 0.0
  }

  return Math.round(number * 10) / 10
}

const loadMovies = async () => {
  try {
    const response = await axios.get(API_URL)
    movies.value = response.data
  } catch (error) {
    console.error('Fehler beim Laden der Filme:', error)
  }
}

const addMovie = async () => {
  try {
    const movieToSave = {
      ...newMovie.value,
      rating: parseRating(newMovie.value.rating),
      releaseYear: Number(newMovie.value.releaseYear)
    }

    const response = await axios.post(API_URL, movieToSave)

    movies.value.push(response.data)

    newMovie.value = {
      title: '',
      rating: '0.0',
      releaseYear: new Date().getFullYear(),
      criticRating: null,
      externalId: '',
      watched: false,
      comment: ''
    }
  } catch (error) {
    console.error('Fehler beim Speichern des Films:', error)

    if (error.response) {
      console.error('Status:', error.response.status)
      console.error('Backend Antwort:', error.response.data)
      alert(`Film konnte nicht gespeichert werden. Status: ${error.response.status}`)
    } else {
      alert('Film konnte nicht gespeichert werden. Keine Antwort vom Backend.')
    }
  }
}

async function updateMovie(movie) {
  console.log('Update wird gesendet:', movie)

  try {
    const movieToUpdate = {
      ...movie,
      rating: parseRating(movie.rating)
    }

    const response = await axios.put(`${API_URL}/${movie.id}`, movieToUpdate)

    console.log('Update erfolgreich:', response.data)

    const index = movies.value.findIndex(m => m.id === movie.id)
    if (index !== -1) {
      movies.value[index] = response.data
    }
  } catch (error) {
    console.error('Film konnte nicht aktualisiert werden:', error)

    if (error.response) {
      console.error('Status:', error.response.status)
      console.error('Backend Antwort:', error.response.data)
      alert(`Update fehlgeschlagen. Status: ${error.response.status}`)
    } else {
      alert('Update fehlgeschlagen. Keine Antwort vom Backend.')
    }
  }
}

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
        <input v-model="newMovie.title" type="text" required>
      </div>

      <div>
        <label>Release Year:</label>
        <input v-model.number="newMovie.releaseYear" type="number" required>
      </div>

      <div>
        <label>Dein Rating:</label>
        <input
          v-model="newMovie.rating"
          type="text"
          inputmode="decimal"
          placeholder="z. B. 8,5"
          required
        >
      </div>

      <button type="submit">Film hinzufügen</button>
    </form>

    <MovieList
      :movies="movies"
      @delete-movie="deleteMovie"
      @update-movie="updateMovie"
    />
  </div>
</template>

<style scoped>
form {
  margin-bottom: 24px;
}

form div {
  margin-bottom: 12px;
}

input {
  margin-left: 8px;
}

button {
  cursor: pointer;
}
</style>