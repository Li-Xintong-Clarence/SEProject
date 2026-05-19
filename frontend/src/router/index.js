import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  // 首页重定向
  { path: '/', redirect: '/login' },

  // 公开页面（无需登录）
  { path: '/login', name: 'Login', component: () => import('../views/user/Login.vue') },
  { path: '/register', name: 'Register', component: () => import('../views/user/Register.vue') },
  { path: '/admin/login', name: 'AdminLogin', component: () => import('../views/admin/AdminLogin.vue') },

  // 用户页面（需要登录）
  {
    path: '/scooters',
    name: 'Scooters',
    meta: { requiresAuth: true },
    component: () => import('../views/user/MapView.vue')
  },
  {
    path: '/scan',
    name: 'Scan',
    meta: { requiresAuth: true },
    component: () => import('../views/user/ScanView.vue')
  },
  {
    path: '/booking',
    name: 'Booking',
    meta: { requiresAuth: true },
    component: () => import('../views/user/Booking.vue')
  },
  {
    path: '/trip',
    name: 'CurrentTrip',
    meta: { requiresAuth: true },
    component: () => import('../views/user/CurrentTrip.vue')
  },
  {
    path: '/profile',
    name: 'Profile',
    meta: { requiresAuth: true },
    component: () => import('../views/user/Profile.vue')
  },

  // 管理员页面
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

// 路由守卫 - 检查登录状态和权限
router.beforeEach((to, from) => {
  const token = localStorage.getItem('token')
  let user = null
  try {
    user = JSON.parse(localStorage.getItem('user') || 'null')
  } catch {
    user = null
  }

  // 检查管理员权限
  if (to.meta.requiresAdmin) {
    if (!token || !user || user.role !== 'ADMIN') {
      return { path: '/admin/login', query: { redirect: to.fullPath } }
    }
  }

  // 检查用户登录状态
  if (to.meta.requiresAuth) {
    if (!token || !user) {
      return { path: '/login', query: { redirect: to.fullPath } }
    }
  }

  // 放行
  return true
})

export default router
