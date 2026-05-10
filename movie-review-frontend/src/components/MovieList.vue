<script setup>
defineProps({
  movies: Array
})
</script>

<template>
  <div>
    <h2>Movie List</h2>

    <ul>
      <li v-for="movie in movies" :key="movie.id">
        {{ movie.title }} ({{ movie.year }})
      </li>
    </ul>
  </div>
</template>