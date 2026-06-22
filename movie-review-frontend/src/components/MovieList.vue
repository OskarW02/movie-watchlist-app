<script setup>
defineProps({
  movies: {
    type: Array,
    required: true
  }
})

const emit = defineEmits(['delete-movie'])
</script>

<template>
  <div>
    <h2>Movie List</h2>

    <p v-if="movies.length === 0">
      Noch keine Filme gespeichert.
    </p>

    <ul v-else>
      <li v-for="movie in movies" :key="movie.id">
        {{ movie.title }} ({{ movie.releaseYear }}) - Rating: {{ movie.rating }}/5

        <button @click="emit('delete-movie', movie.id)">
          Löschen
        </button>
      </li>
    </ul>
  </div>
</template>