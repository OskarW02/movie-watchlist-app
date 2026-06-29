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

const showRatingPopup = ref(false)

const selectRating = (rating) => {
  newMovie.value.rating = rating
  showRatingPopup.value = false
}

async function updateMovie(movie) {
  console.log('Update wird gesendet:', movie)

  try {
    const response = await axios.put(`${API_URL}/${movie.id}`, movie)

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

const deleteMovie = async (id) => {
  try {
    await axios.delete(`${API_URL}/${id}`)

    movies.value = movies.value.filter(movie => movie.id !== id)
  } catch (error) {
    console.error('Fehler beim Löschen des Films:', error)
  }
}

function parseRating(value) {
  const number = Number(String(value).replace(',', '.'))

  if (Number.isNaN(number)) {
    return 0.0
  }

  return Math.round(number * 10) / 10
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
        <label>Dein Rating:</label>
        <input
          v-model="newMovie.rating"
          type="text"
          inputmode="decimal"
          placeholder="z. B. 8,5"
          required
        />

      <button type="submit">Film hinzufügen</button>
    </form>

    <div v-if="showRatingPopup" class="popup-overlay">
      <div class="popup">
        <h3>Bewertung auswählen</h3>

        <div class="rating-options">
          <button
            v-for="rating in 5"
            :key="rating"
            type="button"
            @click="selectRating(rating)"
          >
            {{ rating }} ⭐
          </button>
        </div>

        <button type="button" @click="showRatingPopup = false">
          Abbrechen
        </button>
      </div>
    </div>

    <MovieList
      :movies="movies"
      @delete-movie="deleteMovie"
      @update-movie="updateMovie"
    />
  </div>
</template>

<style scoped>
.popup-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);

  display: flex;
  justify-content: center;
  align-items: center;
}

.popup {
  background: white;
  color: black;
  padding: 24px;
  border-radius: 12px;
  text-align: center;
  min-width: 250px;
}

.rating-options {
  display: flex;
  gap: 8px;
  justify-content: center;
  margin: 16px 0;
}

.rating-options button {
  padding: 10px;
  cursor: pointer;
}
</style>