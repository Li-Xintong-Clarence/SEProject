<template>
  <div class="login-page">
    <div class="login-hero">
      <div class="hero-inner">
        <img class="hero-logo" src="/brand-logo.png" alt="" width="120" height="120" />
        <h1 class="hero-title">CapyGlide</h1>
        <p class="hero-tag">卡皮滑行 · 轻松租一辆，慢慢逛一城</p>
        <p class="hero-desc">和卡皮巴拉一样从容：定位附近车辆，一键预订，随时出发。</p>
      </div>
    </div>

    <div class="login-panel">
      <el-card class="login-card" shadow="never">
        <h2 class="card-title">欢迎回来</h2>
        <p class="card-sub">登录后继续你的滑行之旅</p>
        <el-form ref="formRef" :model="form" :rules="rules" label-width="0">
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="用户名" size="large" prefix-icon="User" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="密码"
              size="large"
              prefix-icon="Lock"
              show-password
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="large" @click="handleLogin" :loading="loading" class="login-button">
              {{ loading ? '登录中...' : '登录' }}
            </el-button>
          </el-form-item>
        </el-form>
        <div class="register-link">
          还没有账号？<router-link to="/register">立即注册</router-link>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { login } from '@/api/auth'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = ref({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const handleLogin = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await login({ username: form.value.username, password: form.value.password })
        if (res.token && res.user) {
          localStorage.setItem('token', res.token)
          localStorage.setItem('user', JSON.stringify(res.user))
          ElMessage.success('登录成功')
          router.push('/scooters')
        } else {
          ElMessage.error('登录失败：返回数据格式不正确')
        }
      } catch (error) {
        console.error('登录出错:', error)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: grid;
  grid-template-columns: minmax(0, 1.05fr) minmax(320px, 420px);
}

@media (max-width: 900px) {
  .login-page {
    grid-template-columns: 1fr;
  }
  .login-hero {
    min-height: 220px;
    padding: 32px 24px 16px;
  }
}

.login-hero {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px;
  background:
    radial-gradient(ellipse 80% 60% at 20% 30%, rgba(107, 154, 196, 0.35) 0%, transparent 55%),
    radial-gradient(ellipse 60% 50% at 85% 70%, rgba(232, 220, 200, 0.45) 0%, transparent 50%),
    linear-gradient(145deg, var(--cg-navy-deep) 0%, var(--cg-navy) 55%, var(--cg-navy-soft) 100%);
  color: #fff;
  overflow: hidden;
}

.login-hero::before {
  content: '';
  position: absolute;
  inset: 0;
  background-image: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.04'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
  opacity: 0.9;
  pointer-events: none;
}

.hero-inner {
  position: relative;
  text-align: center;
  max-width: 420px;
}

.hero-logo {
  border-radius: 28px;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.35);
  margin-bottom: 20px;
}

.hero-title {
  margin: 0 0 8px;
  font-size: 2.25rem;
  font-weight: 800;
  letter-spacing: 0.04em;
}

.hero-tag {
  margin: 0 0 16px;
  font-size: 1rem;
  color: var(--cg-sand);
  font-weight: 600;
}

.hero-desc {
  margin: 0;
  font-size: 0.95rem;
  line-height: 1.65;
  opacity: 0.88;
}

.login-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32px 24px;
  background: var(--cg-mist);
}

.login-card {
  width: 100%;
  max-width: 400px;
  border-radius: var(--cg-radius-lg) !important;
  box-shadow: var(--cg-shadow) !important;
  padding: 8px 4px 16px;
}

.card-title {
  margin: 0 0 6px;
  text-align: center;
  font-size: 1.5rem;
  font-weight: 800;
  color: var(--cg-navy);
}

.card-sub {
  margin: 0 0 28px;
  text-align: center;
  font-size: 0.9rem;
  color: #6b7280;
}

.login-button {
  width: 100%;
  height: 46px;
  margin-top: 4px;
}

.register-link {
  text-align: center;
  margin-top: 8px;
  font-size: 14px;
  color: #6b7280;
}

.register-link a {
  color: var(--cg-navy-soft);
  font-weight: 600;
  text-decoration: none;
}

.register-link a:hover {
  color: var(--cg-accent);
}
</style>
