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
    />
  </div>
</template>