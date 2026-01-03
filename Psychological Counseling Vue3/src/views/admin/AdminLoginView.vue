<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import api from '@/api'

const router = useRouter()
const route = useRoute()

const formData = ref({
  username: '',
  password: ''
})

const loading = ref(false)
const errorMessage = ref('')

// 检查是否是登录过期跳转过来的
onMounted(() => {
  if (route.query.expired === 'true') {
    errorMessage.value = '登录已过期，请重新登录'
  }
})

const handleLogin = async () => {
  errorMessage.value = ''
  loading.value = true
  try {
    const response = await api.post('/api/admin/auth/login', formData.value)
    
    if (response.data.success) {
      const { token, admin, role } = response.data.body
      
      // 保存管理员登录信息
      localStorage.setItem('admin_token', token)
      localStorage.setItem('admin_user', JSON.stringify(admin))
      localStorage.setItem('role', role)
      
      // 跳转到管理员主页
      router.push('/admin/home')
    } else {
      errorMessage.value = response.data.message || '登录失败'
    }
  } catch (error) {
    errorMessage.value = error.response?.data?.message || '登录失败，用户名或密码错误'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-screen bg-gray-900 flex flex-col justify-center py-12 sm:px-6 lg:px-8">
    <div class="sm:mx-auto sm:w-full sm:max-w-md">
      <div class="flex justify-center items-center gap-2">
        <div class="w-10 h-10 bg-purple-600 rounded-lg flex items-center justify-center text-white">
          <i class="fa-solid fa-shield-halved text-xl"></i>
        </div>
        <span class="font-bold text-2xl text-white tracking-tight">管理后台</span>
      </div>
      <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-100">
        管理员登录
      </h2>
      <p class="mt-2 text-center text-sm text-gray-400">
        仅限授权管理员访问
      </p>
    </div>

    <div class="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
      <div class="bg-gray-800 py-8 px-4 shadow-xl sm:rounded-lg sm:px-10 border border-gray-700">
        <form class="space-y-6" @submit.prevent="handleLogin">
          
          <div v-if="errorMessage" class="bg-red-900/50 border border-red-700 text-red-400 px-4 py-3 rounded-md text-sm">
            {{ errorMessage }}
          </div>

          <div>
            <label for="username" class="block text-sm font-medium text-gray-300">
              用户名
            </label>
            <div class="mt-1">
              <input id="username" v-model="formData.username" name="username" type="text" required 
                placeholder="请输入管理员用户名"
                class="appearance-none block w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-md shadow-sm placeholder-gray-400 text-white focus:outline-none focus:ring-purple-500 focus:border-purple-500 sm:text-sm">
            </div>
          </div>

          <div>
            <label for="password" class="block text-sm font-medium text-gray-300">密码</label>
            <div class="mt-1">
              <input id="password" v-model="formData.password" name="password" type="password" required 
                placeholder="请输入密码"
                class="appearance-none block w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-md shadow-sm placeholder-gray-400 text-white focus:outline-none focus:ring-purple-500 focus:border-purple-500 sm:text-sm">
            </div>
          </div>

          <div>
            <button type="submit" :disabled="loading"
              class="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-purple-600 hover:bg-purple-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-purple-500 disabled:opacity-50 disabled:cursor-not-allowed">
              <span v-if="loading">登录中...</span>
              <span v-else>登 录</span>
            </button>
          </div>
        </form>
        
        <div class="mt-6">
          <div class="relative">
            <div class="absolute inset-0 flex items-center">
              <div class="w-full border-t border-gray-600" />
            </div>
            <div class="relative flex justify-center text-sm">
              <span class="px-2 bg-gray-800 text-gray-500">
                <i class="fa-solid fa-lock mr-1"></i>
                安全登录
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
