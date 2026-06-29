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

watch(
  () => props.movies,
  (movies) => {
    movies.forEach((movie) => {
      if (commentDrafts[movie.id] === undefined) {
        commentDrafts[movie.id] = movie.comment || ''
      }
    })
  },
  { immediate: true }
)

function toggleWatched(movie) {
  emit('update-movie', {
    ...movie,
    watched: !movie.watched
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
    <h2>Movie List</h2>

    <p v-if="movies.length === 0">
      Noch keine Filme gespeichert.
    </p>

    <ul v-else>
      <li v-for="movie in movies" :key="movie.id">
        <strong>{{ movie.title }}</strong>
        ({{ movie.releaseYear }}) -

        <p>
          Dein Rating: {{ movie.rating ?? 'Noch nicht bewertet' }}
        </p>

        <p>
          Kritiker-Rating: {{ movie.criticRating ?? 'Noch nicht vorhanden' }}
        </p>

        <p>
          Status: {{ movie.watched ? 'Gesehen' : 'Noch nicht gesehen' }}
        </p>

        <button @click="toggleWatched(movie)">
          {{ movie.watched ? 'Als ungesehen markieren' : 'Als gesehen markieren' }}
        </button>

        <div>
          <label>Kommentar:</label>
          <br />

          <textarea
            v-model="commentDrafts[movie.id]"
            placeholder="Kommentar schreiben..."
          ></textarea>

          <br />

          <button @click="saveComment(movie)">
            Kommentar speichern
          </button>
        </div>

        <button @click="emit('delete-movie', movie.id)">
          Löschen
        </button>
      </li>
    </ul>
  </div>
</template>