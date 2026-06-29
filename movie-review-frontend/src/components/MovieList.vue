<script setup>
import { reactive, watch } from 'vue'

const props = defineProps({
  movies: {
    type: Array,
    required: true
  }
})

const emit = defineEmits(['delete-movie', 'update-movie'])

const commentDrafts = reactive({})
const ratingDrafts = reactive({})

watch(
  () => props.movies,
  (movies) => {
    movies.forEach((movie) => {
      if (commentDrafts[movie.id] === undefined) {
        commentDrafts[movie.id] = movie.comment || ''
      }

      if (ratingDrafts[movie.id] === undefined) {
        ratingDrafts[movie.id] = movie.rating ?? ''
      }
    })
  },
  { immediate: true }
)

function toggleWatched(movie) {
  const nextWatched = !movie.watched

  emit('update-movie', {
    ...movie,
    watched: nextWatched,
    rating: nextWatched ? movie.rating : null,
    comment: nextWatched ? movie.comment : ''
  })
}

function saveRating(movie) {
  const ratingText = String(ratingDrafts[movie.id]).replace(',', '.')
  const rating = Number(ratingText)

  if (Number.isNaN(rating) || rating < 0 || rating > 10) {
    alert('Bitte gib eine Bewertung zwischen 0 und 10 ein.')
    return
  }

  emit('update-movie', {
    ...movie,
    rating: Math.round(rating * 10) / 10
  })
}

function saveComment(movie) {
  emit('update-movie', {
    ...movie,
    comment: commentDrafts[movie.id] || ''
  })
}
</script>

<template>
  <div>
    <h2>Watchlist</h2>

    <p v-if="movies.length === 0">
      Noch keine Filme gespeichert.
    </p>

    <ul v-else>
      <li v-for="movie in movies" :key="movie.id">
        <strong>{{ movie.title }}</strong>

        <span v-if="movie.releaseYear">
          ({{ movie.releaseYear }})
        </span>

        <p>
          Kritiker-Rating:
          {{ movie.criticRating ?? 'Nicht vorhanden' }}
        </p>

        <p>
          Status:
          {{ movie.watched ? '✅' : '❌' }}
        </p>

        <button @click="toggleWatched(movie)">
          {{ movie.watched ? 'Als ungesehen markieren' : 'Als gesehen markieren' }}
        </button>

        <div v-if="movie.watched">
          <p>
            Deine Bewertung:
            {{ movie.rating ?? 'Noch nicht bewertet' }}
          </p>

          <label>Eigene Bewertung:</label>
          <br>

          <input
            v-model="ratingDrafts[movie.id]"
            type="text"
            inputmode="decimal"
            placeholder="z. B. 8,5"
          >

          <br>

          <button @click="saveRating(movie)">
            Bewertung speichern
          </button>

          <div>
            <label>Kommentar:</label>
            <br>

            <textarea
              v-model="commentDrafts[movie.id]"
              placeholder="Kommentar schreiben..."
            ></textarea>

            <br>

            <button @click="saveComment(movie)">
              Kommentar speichern
            </button>
          </div>
        </div>

        <button @click="emit('delete-movie', movie.id)">
          Löschen
        </button>
      </li>
    </ul>
  </div>
</template>