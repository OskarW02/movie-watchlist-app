<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import MovieList from './components/MovieList.vue'

const API_URL = 'https://movie-watchlist-backend-vao3.onrender.com/movies'
const EXTERNAL_MOVIES_URL = 'https://movie-watchlist-backend-vao3.onrender.com/external-movies'

const movies = ref([])
const movieSearch = ref('')
const suggestions = ref([])
let searchTimeout = null
let latestSearchId = 0

const newMovie = ref({
  title: '',
  rating: null,
  releaseYear: null,
  criticRating: null,
  externalId: '',
  watched: false,
  comment: ''
})

function parseRating(value) {
  if (value === null || value === undefined || value === '') {
    return null
  }

  const number = Number(String(value).replace(',', '.'))

  if (Number.isNaN(number)) {
    return null
  }

  if (number < 0 || number > 10) {
    return null
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

const searchExternalMovies = () => {
  const query = movieSearch.value.trim()

  if (searchTimeout) {
    clearTimeout(searchTimeout)
  }

  if (query.length < 3) {
    suggestions.value = []
    return
  }

  searchTimeout = setTimeout(async () => {
    const searchId = ++latestSearchId

    try {
      const response = await axios.get(`${EXTERNAL_MOVIES_URL}/search`, {
        params: {
          title: query
        }
      })

      if (searchId !== latestSearchId) {
        return
      }

      suggestions.value = response.data
    } catch (error) {
      console.error('Fehler bei der Filmsuche:', error)

      if (searchId === latestSearchId) {
        suggestions.value = []
      }
    }
  }, 250)
}

const selectSuggestion = async (suggestion) => {
  try {
    const response = await axios.get(`${EXTERNAL_MOVIES_URL}/${suggestion.externalId}`)
    const details = response.data

    newMovie.value.title = details.title
    newMovie.value.releaseYear = details.releaseYear
    newMovie.value.criticRating = details.criticRating
    newMovie.value.externalId = details.externalId

    movieSearch.value = `${details.title} (${details.releaseYear ?? 'Jahr unbekannt'})`
    suggestions.value = []
  } catch (error) {
    console.error('Fehler beim Laden der Filmdetails:', error)
    alert('Filmdetails konnten nicht geladen werden.')
  }
}

const addMovie = async () => {
  if (!newMovie.value.externalId) {
    alert('Bitte wähle zuerst einen Film aus den Vorschlägen aus.')
    return
  }

  try {
    const movieToSave = {
      ...newMovie.value,
      rating: null,
      watched: false
    }
    const response = await axios.post(API_URL, movieToSave)

    movies.value.push(response.data)

    movieSearch.value = ''
    suggestions.value = []

    newMovie.value = {
      title: '',
      rating: null,
      releaseYear: null,
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
  try {
    const movieToUpdate = {
      ...movie,
      rating: parseRating(movie.rating)
    }

    const response = await axios.put(`${API_URL}/${movie.id}`, movieToUpdate)

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
        <label>Film suchen:</label>
        <input
          v-model="movieSearch"
          type="text"
          placeholder="z. B. Interstellar"
          @input="searchExternalMovies"
        >
      </div>

      <ul v-if="suggestions.length > 0">
        <li
          v-for="suggestion in suggestions"
          :key="suggestion.externalId"
        >
          <button
            type="button"
            @click="selectSuggestion(suggestion)"
          >
            {{ suggestion.title }}
            ({{ suggestion.releaseYear ?? 'Jahr unbekannt' }})
          </button>
        </li>
      </ul>

      <div v-if="newMovie.title">
        <p>
          Ausgewählt:
          <strong>{{ newMovie.title }}</strong>
          <span v-if="newMovie.releaseYear">
            ({{ newMovie.releaseYear }})
          </span>
        </p>

        <p>
          Kritiker-Rating:
          {{ newMovie.criticRating ?? 'Nicht vorhanden' }}
        </p>
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

ul {
  margin-top: 8px;
  margin-bottom: 16px;
}

li {
  margin-bottom: 6px;
}

button {
  cursor: pointer;
}
</style>