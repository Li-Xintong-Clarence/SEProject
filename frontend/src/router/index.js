import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    {
        path: '/',
        redirect: '/login'
    },
    {
        path: '/login',
        name: 'Login',
        component: () => import('../views/user/Login.vue')
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('../views/user/Register.vue')
    },
    {
        path: '/scooters',
        name: 'Scooters',
        component: () => import('../views/user/ScooterList.vue')
    },
    {
        path: '/booking',
        name: 'Booking',
        component: () => import('../views/user/Booking.vue')
    },
    {
        path: '/profile',
        name: 'Profile',
        component: () => import('../views/user/Profile.vue')
    }
    // 后续可添加 admin 路由
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router