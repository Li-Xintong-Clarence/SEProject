<template>
  <DefaultLayout v-if="isLoggedIn" />
  <router-view v-else />
</template>

<script setup>
import { computed } from 'vue'
import DefaultLayout from '@/components/Layout/DefaultLayout.vue'

// 简单判断用户是否已登录
const isLoggedIn = computed(() => {
  return !!localStorage.getItem('token')
})
</script>