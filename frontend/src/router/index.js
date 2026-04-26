import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  // 首页重定向
  { path: '/', redirect: '/login' },

  // 用户相关页面
  { path: '/login', name: 'Login', component: () => import('../views/user/Login.vue') },
  { path: '/register', name: 'Register', component: () => import('../views/user/Register.vue') },
  { path: '/scooters', name: 'Scooters', component: () => import('../views/user/MapView.vue') },
  { path: '/scooters/list', name: 'ScooterList', component: () => import('../views/user/ScooterList.vue') },
  { path: '/booking', name: 'Booking', component: () => import('../views/user/Booking.vue') },
  { path: '/trip', name: 'CurrentTrip', component: () => import('../views/user/CurrentTrip.vue') },
  { path: '/profile', name: 'Profile', component: () => import('../views/user/Profile.vue') },

  // 管理员页面
  { path: '/admin/login', name: 'AdminLogin', component: () => import('../views/admin/AdminLogin.vue') },
  {
    path: '/admin',
    name: 'AdminDashboard',
    meta: { requiresAdmin: true },
    component: () => import('../views/admin/AdminDashboard.vue')
  },
  {
    path: '/admin/users',
    name: 'UserManagement',
    meta: { requiresAdmin: true },
    component: () => import('../views/admin/UserManagement.vue')
  },
  {
    path: '/admin/issues',
    name: 'IssuesManagement',
    meta: { requiresAdmin: true },
    component: () => import('../views/admin/IssuesManagement.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫 - 检查管理员权限
router.beforeEach((to, from, next) => {
  if (to.meta.requiresAdmin) {
    const token = localStorage.getItem('token')
    let user = null
    try {
      user = JSON.parse(localStorage.getItem('user') || 'null')
    } catch {
      user = null
    }
    if (!token || !user || user.role !== 'ADMIN') {
      return next({ path: '/admin/login', query: { redirect: to.fullPath } })
    }
  }
  next()
})

export default router
