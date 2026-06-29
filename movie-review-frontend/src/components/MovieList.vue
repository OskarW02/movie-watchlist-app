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

  if (!nextWatched) {
    ratingDrafts[movie.id] = ''
    commentDrafts[movie.id] = ''
  }

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

    <ul v-else class="movie-grid">
      <li
        v-for="movie in movies"
        :key="movie.id"
        class="movie-card"
      >
        <h3>
          {{ movie.title }}
          <span v-if="movie.releaseYear">
            ({{ movie.releaseYear }})
          </span>
        </h3>

        <img
          v-if="movie.posterUrl"
          :src="movie.posterUrl"
          :alt="movie.title"
          class="movie-poster"
        >

        <div
          v-else
          class="poster-placeholder"
        >
          Kein Bild vorhanden
        </div>

        <p>
          Kritiker-Rating:
          {{ movie.criticRating ?? 'Nicht vorhanden' }}
        </p>

        <p>
          Status:
          {{ movie.watched ? '✅ Gesehen' : '❌ Noch nicht gesehen' }}
        </p>

        <button
          v-if="!movie.watched"
          @click="toggleWatched(movie)"
        >
          Als gesehen markieren
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

          <div class="comment-section">
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

          <button @click="toggleWatched(movie)">
            Als ungesehen markieren
          </button>
        </div>

        <button
          class="delete-button"
          @click="emit('delete-movie', movie.id)"
        >
          Löschen
        </button>
      </li>
    </ul>
  </div>
</template>

<style scoped>
.movie-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(360px, 1fr));
  gap: 24px;
  padding: 0;
  list-style: none;
}

.movie-card {
  border: 1px solid #ccc;
  border-radius: 8px;
  padding: 16px;
}

.movie-card h3 {
  margin-top: 0;
}

.movie-poster {
  width: 100%;
  height: 360px;
  object-fit: cover;
  border-radius: 8px;
  margin-bottom: 12px;
  background: #eee;
}

.poster-placeholder {
  width: 100%;
  height: 360px;
  border-radius: 8px;
  margin-bottom: 12px;
  background: #eee;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
}

.movie-card button {
  margin-top: 8px;
  cursor: pointer;
}

.comment-section {
  margin-top: 12px;
}

textarea {
  width: 100%;
  min-height: 80px;
  margin-top: 6px;
  box-sizing: border-box;
}

input {
  margin-top: 6px;
}

.delete-button {
  margin-top: 12px;
}

@media (max-width: 1200px) {
  .movie-grid {
    grid-template-columns: repeat(2, minmax(320px, 1fr));
  }
}

@media (max-width: 750px) {
  .movie-grid {
    grid-template-columns: 1fr;
  }
}
</style>