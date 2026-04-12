import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', name: 'Login', component: () => import('../views/user/Login.vue') },
  { path: '/register', name: 'Register', component: () => import('../views/user/Register.vue') },
  { path: '/scooters', name: 'Scooters', component: () => import('../views/user/MapView.vue') },
  { path: '/scooters/list', name: 'ScooterList', component: () => import('../views/user/ScooterList.vue') },
  { path: '/booking', name: 'Booking', component: () => import('../views/user/Booking.vue') },
  { path: '/profile', name: 'Profile', component: () => import('../views/user/Profile.vue') },
  { path: '/admin/login', name: 'AdminLogin', component: () => import('../views/admin/AdminLogin.vue') },
  {
    path: '/admin',
    name: 'Admin',
    meta: { requiresAdmin: true },
    component: () => import('../views/admin/AdminDashboard.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  if (to.meta.requiresAdmin) {
    const token = localStorage.getItem('token')
    let user = null
    try {
      user = JSON.parse(localStorage.getItem('user') || 'null')
    } catch {
      user = null
    }
    if (!token || !user || user.role !== 'ADMIN') {
      return { path: '/admin/login', query: { redirect: to.fullPath } }
    }
  }
  return true
})

export default router
