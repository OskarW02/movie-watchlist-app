<script setup>
defineProps({
  movies: {
    type: Array,
    required: true
  }
})

const emit = defineEmits(['delete-movie', 'update-movie'])

function toggleWatched(movie) {
  emit('update-movie', {
    ...movie,
    watched: !movie.watched
  })
}

function updateComment(movie, event) {
  emit('update-movie', {
    ...movie,
    comment: event.target.value
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
        Rating: {{ movie.rating }}/5

        <p>
          Status:
          {{ movie.watched ? 'Gesehen' : 'Noch nicht gesehen' }}
        </p>

        <button @click="toggleWatched(movie)">
          {{ movie.watched ? 'Als ungesehen markieren' : 'Als gesehen markieren' }}
        </button>

        <div>
          <label>Kommentar:</label>
          <br />
          <textarea
            :value="movie.comment || ''"
            placeholder="Kommentar schreiben..."
            @blur="updateComment(movie, $event)"
          ></textarea>
        </div>

        <button @click="emit('delete-movie', movie.id)">
          Löschen
        </button>
      </li>
    </ul>
  </div>
</template>