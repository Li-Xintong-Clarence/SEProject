<template>
  <DefaultLayout v-if="showLayout" />
  <router-view v-else />
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import DefaultLayout from '@/components/Layout/DefaultLayout.vue'

const route = useRoute()

const showLayout = computed(() => {
  const token = localStorage.getItem('token')
  if (!token) return false
  const p = route.path
  if (['/login', '/register', '/admin/login'].includes(p)) return false
  if (p.startsWith('/admin')) return false
  return true
})
</script>
