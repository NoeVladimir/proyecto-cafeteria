import { ref } from 'vue'

async function request(endpoint, payload) {
  const response = await fetch(`/api/auth/${endpoint}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  })

  const data = await response.json().catch(() => ({}))
  if (!response.ok) {
    throw new Error(data.detail || data.message || 'No se pudo completar la solicitud')
  }

  return data
}

export async function api(path, token, options = {}) {
  const response = await fetch(`/api${path}`, {
    ...options,
    headers: { 'Content-Type': 'application/json', ...(token ? { 'X-Auth-Token': token } : {}), ...options.headers }
  })
  const data = await response.json().catch(() => ({}))
  if (!response.ok) throw new Error(data.detail || data.message || 'No se pudo completar la solicitud')
  return data
}

export function useAuth() {
  const loading = ref(false)
  const error = ref('')
  const user = ref(JSON.parse(localStorage.getItem('cafeteria-user') || 'null'))

  async function register(name, email, password) {
    return authenticate('register', { name, email, password })
  }

  async function login(email, password) {
    return authenticate('login', { email, password })
  }

  async function authenticate(endpoint, payload) {
    loading.value = true
    error.value = ''

    try {
      const response = await request(endpoint, payload)
      user.value = response
      localStorage.setItem('cafeteria-user', JSON.stringify(response))
      return response
    } catch (requestError) {
      error.value = requestError.message
      throw requestError
    } finally {
      loading.value = false
    }
  }

  function logout() {
    user.value = null
    localStorage.removeItem('cafeteria-user')
  }

  return { user, loading, error, register, login, logout }
}
