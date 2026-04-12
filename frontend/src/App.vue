<template>
  <DefaultLayout v-if="isLoggedIn" />
  <router-view v-else />
</template>

<script setup>
import { computed } from 'vue'
import DefaultLayout from '@/components/Layout/DefaultLayout.vue'

// 判断用户是否已登录
const isLoggedIn = computed(() => {
  const token = localStorage.getItem('token')
  const userStr = localStorage.getItem('user')
  return !!(token && userStr)
})
</script>
