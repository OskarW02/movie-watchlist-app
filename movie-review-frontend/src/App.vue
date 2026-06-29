<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import MovieList from './components/MovieList.vue'

const API_URL = 'https://movie-watchlist-backend-vao3.onrender.com/movies'

const movies = ref([])

const newMovie = ref({
  title: '',
  rating: 1,
  releaseYear: new Date().getFullYear(),
  watched: false,
  comment: ''
})

async function updateMovie(movie) {
  try {
    const response = await axios.put(`${API_URL}/${movie.id}`, movie)

    const index = movies.value.findIndex(m => m.id === movie.id)
    if (index !== -1) {
      movies.value[index] = response.data
    }
  } catch (error) {
    console.error('Film konnte nicht aktualisiert werden:', error)
  }
}

const showRatingPopup = ref(false)

const selectRating = (rating) => {
  newMovie.value.rating = rating
  showRatingPopup.value = false
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
    const response = await axios.post(API_URL, newMovie.value)

    movies.value.push(response.data)

    newMovie.value = {
      title: '',
      rating: 1,
      releaseYear: new Date().getFullYear(),
      watched: false,
      comment: ''
    }
  } catch (error) {
    console.error('Fehler beim Speichern des Films:', error)
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
        <input v-model="newMovie.title" type="text" required />
      </div>

      <div>
        <label>Release Year:</label>
        <input v-model.number="newMovie.releaseYear" type="number" required />
      </div>

      <div>
        <label>Rating:</label>

        <button type="button" @click="showRatingPopup = true">
          {{ newMovie.rating }} / 5 ⭐
        </button>
      </div>

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