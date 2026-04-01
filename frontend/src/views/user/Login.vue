<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2>电动滑板车租赁系统</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="0">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" class="login-button">
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form-item>
      </el-form>
      <div class="register-link">
        还没有账号？<router-link to="/register">立即注册</router-link>
      </div>
    </el-card>
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
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
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
        // 根据后端文档，成功时返回 { token, user }
        if (res.token && res.user) {
          localStorage.setItem('token', res.token)
          // 存储整个 user 对象（包含 id, username, role 等）
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
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f0f2f5;
}
.login-card {
  width: 400px;
  max-width: 90%;
}
.login-card h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #409eff;
}
.login-button {
  width: 100%;
}
.register-link {
  text-align: center;
  margin-top: 15px;
  font-size: 14px;
}
.register-link a {
  color: #409eff;
  text-decoration: none;
}
</style>