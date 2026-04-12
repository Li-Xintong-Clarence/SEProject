<template>
  <div class="register-page">
    <div class="register-hero">
      <div class="hero-inner">
        <img class="hero-logo" src="/brand-logo.png" alt="" width="96" height="96" />
        <h1 class="hero-title">加入 CapyGlide</h1>
        <p class="hero-tag">卡皮滑行 · 注册即享从容出行</p>
      </div>
    </div>

    <div class="register-panel">
      <el-card class="register-card" shadow="never">
        <h2 class="card-title">创建账号</h2>
        <p class="card-sub">填写信息，开始你的第一次滑行</p>
        <el-form ref="formRef" :model="form" :rules="rules" label-width="0">
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="用户名" size="large" prefix-icon="User" />
          </el-form-item>
          <el-form-item prop="email">
            <el-input v-model="form.email" placeholder="电子邮箱" size="large" prefix-icon="Message" />
          </el-form-item>
          <el-form-item prop="phone">
            <el-input v-model="form.phone" placeholder="手机号（选填）" size="large" prefix-icon="Phone" />
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
          <el-form-item prop="confirmPassword">
            <el-input
              v-model="form.confirmPassword"
              type="password"
              placeholder="确认密码"
              size="large"
              prefix-icon="Lock"
              show-password
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="large" @click="handleRegister" :loading="loading" class="register-button">
              {{ loading ? '注册中...' : '注册' }}
            </el-button>
          </el-form-item>
        </el-form>
        <div class="login-link">
          已有账号？<router-link to="/login">立即登录</router-link>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { register } from '@/api/auth'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = ref({
  username: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.value.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度应为3-20个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleRegister = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const registerData = {
          username: form.value.username,
          email: form.value.email,
          phone: form.value.phone || null,
          password: form.value.password
        }
        await register(registerData)
        ElMessage.success('注册成功，请登录')
        router.push('/login')
      } catch (error) {
        console.error('注册出错:', error)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: grid;
  grid-template-columns: minmax(0, 0.9fr) minmax(320px, 480px);
}

@media (max-width: 960px) {
  .register-page {
    grid-template-columns: 1fr;
  }
  .register-hero {
    min-height: 180px;
    padding: 28px 20px 12px;
  }
}

.register-hero {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  background: linear-gradient(160deg, var(--cg-navy) 0%, var(--cg-charcoal) 100%);
  color: #fff;
}

.hero-inner {
  text-align: center;
}

.hero-logo {
  border-radius: 24px;
  box-shadow: 0 16px 40px rgba(0, 0, 0, 0.3);
  margin-bottom: 16px;
}

.hero-title {
  margin: 0 0 8px;
  font-size: 1.75rem;
  font-weight: 800;
}

.hero-tag {
  margin: 0;
  font-size: 0.95rem;
  color: var(--cg-sand);
}

.register-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 28px 20px;
  background: var(--cg-mist);
}

.register-card {
  width: 100%;
  max-width: 460px;
  border-radius: var(--cg-radius-lg) !important;
  box-shadow: var(--cg-shadow) !important;
  padding: 8px 4px 12px;
}

.card-title {
  margin: 0 0 6px;
  text-align: center;
  font-size: 1.4rem;
  font-weight: 800;
  color: var(--cg-navy);
}

.card-sub {
  margin: 0 0 22px;
  text-align: center;
  font-size: 0.88rem;
  color: #6b7280;
}

.register-button {
  width: 100%;
  height: 46px;
}

.login-link {
  text-align: center;
  margin-top: 6px;
  font-size: 14px;
  color: #6b7280;
}

.login-link a {
  color: var(--cg-navy-soft);
  font-weight: 600;
  text-decoration: none;
}

.login-link a:hover {
  color: var(--cg-accent);
}
</style>
