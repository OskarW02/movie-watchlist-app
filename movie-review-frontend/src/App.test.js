import { mount, flushPromises } from '@vue/test-utils'
import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest'
import axios from 'axios'
import App from './App.vue'

vi.mock('axios')

const API_URL = 'https://movie-watchlist-backend-vao3.onrender.com/movies'
const EXTERNAL_MOVIES_URL = 'https://movie-watchlist-backend-vao3.onrender.com/external-movies'

const suggestion = {
  title: 'Interstellar',
  releaseYear: 2014,
  externalId: 'tt0816692'
}

const details = {
  title: 'Interstellar',
  releaseYear: 2014,
  criticRating: 8.7,
  externalId: 'tt0816692',
  posterUrl: 'https://example.com/poster.jpg'
}

function mockAxiosGet(savedMovies = []) {
  axios.get.mockImplementation((url) => {
    if (url === API_URL) {
      return Promise.resolve({
        data: savedMovies
      })
    }

    if (url === `${EXTERNAL_MOVIES_URL}/search`) {
      return Promise.resolve({
        data: [suggestion]
      })
    }

    if (url === `${EXTERNAL_MOVIES_URL}/tt0816692`) {
      return Promise.resolve({
        data: details
      })
    }

    return Promise.resolve({
      data: []
    })
  })
}

async function searchForInterstellar(wrapper) {
  const input = wrapper.find('input[type="text"]')

  await input.setValue('inter')

  await vi.advanceTimersByTimeAsync(300)
  await flushPromises()
}

async function selectInterstellar(wrapper) {
  await searchForInterstellar(wrapper)

  const button = wrapper
    .findAll('button')
    .find(button => button.text().includes('Interstellar'))

  expect(button).toBeTruthy()

  await button.trigger('click')
  await flushPromises()
}

describe('App.vue', () => {
  beforeEach(() => {
    vi.useFakeTimers()
    vi.spyOn(window, 'alert').mockImplementation(() => {})

    axios.get.mockReset()
    axios.post.mockReset()
    axios.put.mockReset()
    axios.delete.mockReset()
  })

  afterEach(() => {
    vi.useRealTimers()
    vi.restoreAllMocks()
  })

  it('loads movies when the app starts', async () => {
    mockAxiosGet([
      {
        id: 1,
        title: 'Matrix',
        releaseYear: 1999,
        rating: null,
        criticRating: 8.7,
        externalId: 'tt0133093',
        posterUrl: null,
        watched: false,
        comment: ''
      }
    ])

    const wrapper = mount(App)

    await flushPromises()

    expect(axios.get).toHaveBeenCalledWith(API_URL)
    expect(wrapper.text()).toContain('Matrix')
  })

  it('does not search movies when the input has less than 3 characters', async () => {
    mockAxiosGet([])

    const wrapper = mount(App)

    await flushPromises()
    axios.get.mockClear()

    const input = wrapper.find('input[type="text"]')

    await input.setValue('in')

    await vi.advanceTimersByTimeAsync(300)
    await flushPromises()

    expect(axios.get).not.toHaveBeenCalled()
  })

  it('searches movies when the input has at least 3 characters', async () => {
    mockAxiosGet([])

    const wrapper = mount(App)

    await flushPromises()
    axios.get.mockClear()

    await searchForInterstellar(wrapper)

    expect(axios.get).toHaveBeenCalledWith(
      `${EXTERNAL_MOVIES_URL}/search`,
      {
        params: {
          title: 'inter'
        }
      }
    )
  })

  it('selects a movie suggestion and shows movie details', async () => {
    mockAxiosGet([])

    const wrapper = mount(App)

    await flushPromises()

    await selectInterstellar(wrapper)

    expect(wrapper.text()).toContain('Ausgewählt:')
    expect(wrapper.text()).toContain('Interstellar')
    expect(wrapper.text()).toContain('Kritiker-Rating:')
    expect(wrapper.text()).toContain('8.7')

    const poster = wrapper.find('img.selected-poster')

    expect(poster.exists()).toBe(true)
    expect(poster.attributes('src')).toBe('https://example.com/poster.jpg')
  })

  it('adds the selected movie to the watchlist without own rating', async () => {
    mockAxiosGet([])

    axios.post.mockResolvedValue({
      data: {
        id: 1,
        ...details,
        rating: null,
        watched: false,
        comment: ''
      }
    })

    const wrapper = mount(App)

    await flushPromises()

    await selectInterstellar(wrapper)

    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()

    expect(axios.post).toHaveBeenCalledWith(
      API_URL,
      expect.objectContaining({
        title: 'Interstellar',
        releaseYear: 2014,
        criticRating: 8.7,
        externalId: 'tt0816692',
        posterUrl: 'https://example.com/poster.jpg',
        rating: null,
        watched: false,
        comment: ''
      })
    )
  })
})