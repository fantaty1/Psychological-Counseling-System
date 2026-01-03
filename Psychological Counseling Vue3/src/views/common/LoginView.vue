<script setup>
import { ref, onMounted } from 'vue'
import { RouterLink, useRouter, useRoute } from 'vue-router'
import api, { setToken, setUser } from '@/api'

const router = useRouter()
const route = useRoute()

// 角色类型：student-学生, doctor-医生
const userRole = ref('student')

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
    // 发送登录请求，携带角色信息
    const response = await api.post('/api/auth/login', {
      ...formData.value,
      role: userRole.value
    })
    
    if (response.data.success) {
      const { token, user, role } = response.data.body
      
      // 根据角色决定目标路径
      const targetPath = role === 'doctor' ? '/doctor/home' : '/'
      
      // 手动设置正确的 localStorage key
      if (role === 'doctor') {
        localStorage.setItem('doctor_token', token)
        localStorage.setItem('doctor_user', JSON.stringify(user))
      } else {
        localStorage.setItem('student_token', token)
        localStorage.setItem('student_user', JSON.stringify(user))
      }
      localStorage.setItem('role', role || userRole.value)
      
      // 保存完token后再跳转
      router.push(targetPath)
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
  <div class="min-h-screen bg-gray-50 flex flex-col justify-center py-12 sm:px-6 lg:px-8">
    <div class="sm:mx-auto sm:w-full sm:max-w-md">
      <div class="flex justify-center items-center gap-2">
        <div class="w-10 h-10 bg-teal-500 rounded-lg flex items-center justify-center text-white">
          <i class="fa-solid fa-heart-pulse text-xl"></i>
        </div>
        <span class="font-bold text-2xl text-slate-800 tracking-tight">心语空间</span>
      </div>
      <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-900">
        登录您的账号
      </h2>
      
      <!-- 角色选择 -->
      <div class="mt-6 flex justify-center gap-4">
        <button 
          type="button"
          @click="userRole = 'student'"
          :class="[
            'flex-1 max-w-[140px] py-3 px-4 rounded-xl border-2 transition-all duration-200 flex flex-col items-center gap-2',
            userRole === 'student' 
              ? 'border-teal-500 bg-teal-50 text-teal-700' 
              : 'border-gray-200 bg-white text-gray-500 hover:border-gray-300'
          ]">
          <i class="fa-solid fa-user-graduate text-2xl"></i>
          <span class="text-sm font-medium">我是学生</span>
        </button>
        <button 
          type="button"
          @click="userRole = 'doctor'"
          :class="[
            'flex-1 max-w-[140px] py-3 px-4 rounded-xl border-2 transition-all duration-200 flex flex-col items-center gap-2',
            userRole === 'doctor' 
              ? 'border-indigo-500 bg-indigo-50 text-indigo-700' 
              : 'border-gray-200 bg-white text-gray-500 hover:border-gray-300'
          ]">
          <i class="fa-solid fa-user-doctor text-2xl"></i>
          <span class="text-sm font-medium">我是咨询师</span>
        </button>
      </div>
    </div>

    <div class="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
      <div class="bg-white py-8 px-4 shadow sm:rounded-lg sm:px-10">
        <form class="space-y-6" @submit.prevent="handleLogin">
          
          <div v-if="errorMessage" class="bg-red-50 border border-red-200 text-red-600 px-4 py-3 rounded-md text-sm">
            {{ errorMessage }}
          </div>

          <div>
            <label for="username" class="block text-sm font-medium text-gray-700">
              {{ userRole === 'doctor' ? '手机号/邮箱' : '学号/邮箱' }}
            </label>
            <div class="mt-1">
              <input id="username" v-model="formData.username" name="username" type="text" required 
                :placeholder="userRole === 'doctor' ? '请输入手机号或邮箱' : '请输入学号或邮箱'"
                class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-teal-500 focus:border-teal-500 sm:text-sm">
            </div>
          </div>

          <div>
            <label for="password" class="block text-sm font-medium text-gray-700">密码</label>
            <div class="mt-1">
              <input id="password" v-model="formData.password" name="password" type="password" required 
                class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-teal-500 focus:border-teal-500 sm:text-sm">
            </div>
          </div>

          <div class="flex items-center justify-between">
            <div class="flex items-center">
              <input id="remember-me" name="remember-me" type="checkbox" class="h-4 w-4 text-teal-600 focus:ring-teal-500 border-gray-300 rounded">
              <label for="remember-me" class="ml-2 block text-sm text-gray-900">
                记住我
              </label>
            </div>

            <div class="text-sm">
              <a href="#" class="font-medium text-teal-600 hover:text-teal-500">
                忘记密码?
              </a>
            </div>
          </div>

          <div>
            <button type="submit" :disabled="loading"
              :class="[
                'w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white focus:outline-none focus:ring-2 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed',
                userRole === 'doctor' 
                  ? 'bg-indigo-600 hover:bg-indigo-700 focus:ring-indigo-500' 
                  : 'bg-teal-600 hover:bg-teal-700 focus:ring-teal-500'
              ]">
              <span v-if="loading">登录中...</span>
              <span v-else>{{ userRole === 'doctor' ? '咨询师登录' : '学生登录' }}</span>
            </button>
          </div>

          <div class="text-center text-sm">
            <RouterLink v-if="userRole === 'student'" to="/register" class="font-medium text-teal-600 hover:text-teal-500">
              还没有账号？立即注册
            </RouterLink>
            <RouterLink v-else to="/doctor/register" class="font-medium text-indigo-600 hover:text-indigo-500">
              还没有账号？咨询师注册
            </RouterLink>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
