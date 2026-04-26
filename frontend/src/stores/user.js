// src/stores/user.js
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('user') || '{}'))
  const discountProfile = ref(JSON.parse(localStorage.getItem('capyglide_discount_profile') || '{"userType":"none"}'))

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 'ADMIN')
  const username = computed(() => userInfo.value?.username || '')
  const userId = computed(() => userInfo.value?.id)

  function setToken(newToken) {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  function setUserInfo(info) {
    userInfo.value = info
    localStorage.setItem('user', JSON.stringify(info))
  }

  function setDiscountProfile(profile) {
    discountProfile.value = profile
    localStorage.setItem('capyglide_discount_profile', JSON.stringify(profile))
  }

  function logout() {
    token.value = ''
    userInfo.value = {}
    discountProfile.value = { userType: 'none' }
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    localStorage.removeItem('capyglide_discount_profile')
  }

  return {
    token,
    userInfo,
    discountProfile,
    isLoggedIn,
    isAdmin,
    username,
    userId,
    setToken,
    setUserInfo,
    setDiscountProfile,
    logout
  }
})
