import { mount } from '@vue/test-utils'
import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest'
import MovieList from './MovieList.vue'

function createMovie(overrides = {}) {
  return {
    id: 1,
    title: 'Interstellar',
    releaseYear: 2014,
    rating: null,
    criticRating: 8.7,
    externalId: 'tt0816692',
    posterUrl: 'https://example.com/poster.jpg',
    watched: false,
    comment: '',
    ...overrides
  }
}

function findButtonByText(wrapper, text) {
  return wrapper
    .findAll('button')
    .find(button => button.text() === text)
}

describe('MovieList.vue', () => {
  beforeEach(() => {
    vi.spyOn(window, 'alert').mockImplementation(() => {})
  })

  afterEach(() => {
    vi.restoreAllMocks()
  })

  it('shows empty message when there are no movies', () => {
    const wrapper = mount(MovieList, {
      props: {
        movies: []
      }
    })

    expect(wrapper.text()).toContain('Noch keine Filme gespeichert.')
  })

  it('shows movie title, year, poster and status', () => {
    const wrapper = mount(MovieList, {
      props: {
        movies: [createMovie()]
      }
    })

    expect(wrapper.text()).toContain('Interstellar')
    expect(wrapper.text()).toContain('2014')
    expect(wrapper.text()).toContain('Kritiker-Rating:')
    expect(wrapper.text()).toContain('8.7')
    expect(wrapper.text()).toContain('❌ Noch nicht gesehen')

    const poster = wrapper.find('img.movie-poster')

    expect(poster.exists()).toBe(true)
    expect(poster.attributes('src')).toBe('https://example.com/poster.jpg')
  })

  it('emits update when a movie is marked as watched', async () => {
    const wrapper = mount(MovieList, {
      props: {
        movies: [createMovie()]
      }
    })

    const button = findButtonByText(wrapper, 'Als gesehen markieren')

    await button.trigger('click')

    expect(wrapper.emitted('update-movie')).toBeTruthy()

    const emittedMovie = wrapper.emitted('update-movie')[0][0]

    expect(emittedMovie.watched).toBe(true)
  })

  it('emits update with a valid rating between 0 and 10', async () => {
    const wrapper = mount(MovieList, {
      props: {
        movies: [
          createMovie({
            watched: true
          })
        ]
      }
    })

    const input = wrapper.find('input')

    await input.setValue('8,5')

    const button = findButtonByText(wrapper, 'Bewertung speichern')

    await button.trigger('click')

    expect(wrapper.emitted('update-movie')).toBeTruthy()

    const emittedMovie = wrapper.emitted('update-movie')[0][0]

    expect(emittedMovie.rating).toBe(8.5)
  })

  it('rejects a rating above 10', async () => {
    const wrapper = mount(MovieList, {
      props: {
        movies: [
          createMovie({
            watched: true
          })
        ]
      }
    })

    const input = wrapper.find('input')

    await input.setValue('11')

    const button = findButtonByText(wrapper, 'Bewertung speichern')

    await button.trigger('click')

    expect(window.alert).toHaveBeenCalledWith('Bitte gib eine Bewertung zwischen 0 und 10 ein.')
    expect(wrapper.emitted('update-movie')).toBeFalsy()
  })
})