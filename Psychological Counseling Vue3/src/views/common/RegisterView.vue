<script setup>
import { ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import api from '@/api'

const router = useRouter()

const formData = ref({
  username: '',
  studentId: '',
  email: '',
  password: '',
  confirmPassword: '',
  college: ''
})

const loading = ref(false)
const errorMessage = ref('')

const handleRegister = async () => {
  errorMessage.value = ''
  
  if (formData.value.password !== formData.value.confirmPassword) {
    errorMessage.value = '两次输入的密码不一致'
    return
  }

  loading.value = true
  try {
    // 发送注册请求
    const response = await api.post('/api/auth/register', {
      username: formData.value.username,
      studentId: formData.value.studentId,
      email: formData.value.email,
      password: formData.value.password,
      college: formData.value.college
    })
    
    if (response.data.success) {
      // 注册成功跳转登录
      alert('注册成功，请登录')
      router.push('/login')
    } else {
      errorMessage.value = response.data.message || '注册失败'
    }
  } catch (error) {
    errorMessage.value = error.response?.data?.message || '注册失败，请稍后重试'
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
        注册新账号
      </h2>
      <p class="mt-2 text-center text-sm text-gray-600">
        已有账号？
        <RouterLink to="/login" class="font-medium text-teal-600 hover:text-teal-500">
          立即登录
        </RouterLink>
      </p>
    </div>

    <div class="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
      <div class="bg-white py-8 px-4 shadow sm:rounded-lg sm:px-10">
        <form class="space-y-6" @submit.prevent="handleRegister">
          
          <!-- 错误提示 -->
          <div v-if="errorMessage" class="bg-red-50 border border-red-200 text-red-600 px-4 py-3 rounded-md text-sm">
            {{ errorMessage }}
          </div>

          <div>
            <label for="username" class="block text-sm font-medium text-gray-700">姓名</label>
            <div class="mt-1">
              <input id="username" v-model="formData.username" name="username" type="text" required 
                class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-teal-500 focus:border-teal-500 sm:text-sm">
            </div>
          </div>

          <div>
            <label for="studentId" class="block text-sm font-medium text-gray-700">学号</label>
            <div class="mt-1">
              <input id="studentId" v-model="formData.studentId" name="studentId" type="text" required 
                class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-teal-500 focus:border-teal-500 sm:text-sm">
            </div>
          </div>

          <div>
            <label for="college" class="block text-sm font-medium text-gray-700">学院</label>
            <div class="mt-1">
              <input id="college" v-model="formData.college" name="college" type="text" required 
                class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-teal-500 focus:border-teal-500 sm:text-sm">
            </div>
          </div>

          <div>
            <label for="email" class="block text-sm font-medium text-gray-700">电子邮箱</label>
            <div class="mt-1">
              <input id="email" v-model="formData.email" name="email" type="email" required 
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

          <div>
            <label for="confirmPassword" class="block text-sm font-medium text-gray-700">确认密码</label>
            <div class="mt-1">
              <input id="confirmPassword" v-model="formData.confirmPassword" name="confirmPassword" type="password" required 
                class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-teal-500 focus:border-teal-500 sm:text-sm">
            </div>
          </div>

          <div>
            <button type="submit" :disabled="loading"
              class="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-teal-600 hover:bg-teal-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-teal-500 disabled:opacity-50 disabled:cursor-not-allowed">
              <span v-if="loading">注册中...</span>
              <span v-else>注册</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
