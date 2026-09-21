import { computed } from 'vue'

export function greeting(name = 'cafeteria') {
  return `Bienvenido a ${name}`
}

export function useGreeting(name) {
  const message = computed(() => greeting(name))

  return { message }
}
