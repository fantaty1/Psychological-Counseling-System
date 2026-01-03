<script setup>
import { ref, onMounted, computed } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import api, { getAvatarUrl, removeToken, removeUser } from '@/api'

const router = useRouter()

// 医生信息
const doctorInfo = ref({
  name: '',
  avatar: '',
  title: '',
  rating: 0
})

// 计算属性：获取正确的头像URL
const avatarUrl = computed(() => {
  return getAvatarUrl(doctorInfo.value.avatar) || null
})

// 今日统计
const todayStats = ref({
  appointments: 0,
  completed: 0,
  pending: 0,
  messages: 0
})

// 今日预约列表
const todayAppointments = ref([])

// 待处理消息
const pendingMessages = ref([])

// 加载状态
const loading = ref(true)

// 当前时间问候语
const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return '凌晨好'
  if (hour < 9) return '早安'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 18) return '下午好'
  if (hour < 22) return '晚上好'
  return '夜深了'
})

// 加载医生信息
const loadDoctorInfo = async () => {
  try {
    const response = await api.get('/api/doctor/profile')
    if (response.data.success) {
      doctorInfo.value = response.data.body
    }
  } catch (error) {
    console.error('加载医生信息失败:', error)
  }
}

// 加载今日统计
const loadTodayStats = async () => {
  try {
    const response = await api.get('/api/doctor/stats/today')
    if (response.data.success) {
      todayStats.value = response.data.body
    }
  } catch (error) {
    console.error('加载统计失败:', error)
  }
}

// 加载今日预约
const loadTodayAppointments = async () => {
  try {
    const response = await api.get('/api/doctor/appointments/today')
    if (response.data.success) {
      todayAppointments.value = response.data.body || []
    }
  } catch (error) {
    console.error('加载预约失败:', error)
  }
}

// 加载待处理消息
const loadPendingMessages = async () => {
  try {
    const response = await api.get('/api/doctor/messages/pending')
    if (response.data.success) {
      pendingMessages.value = response.data.body || []
    }
  } catch (error) {
    console.error('加载消息失败:', error)
  }
}

// 处理预约状态
const handleAppointmentAction = async (appointmentId, action) => {
  try {
    await api.put(`/api/doctor/appointments/${appointmentId}/${action}`)
    await loadTodayAppointments()
    await loadTodayStats()
  } catch (error) {
    console.error('操作失败:', error)
  }
}

// 退出登录
const handleLogout = () => {
  removeToken()
  removeUser()
  localStorage.removeItem('role')
  router.push('/login')
}

// 格式化时间
const formatTime = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

onMounted(async () => {
  loading.value = true
  await Promise.all([
    loadDoctorInfo(),
    loadTodayStats(),
    loadTodayAppointments(),
    loadPendingMessages()
  ])
  loading.value = false
})
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 顶部导航 -->
    <nav class="bg-white shadow-sm border-b border-gray-100 sticky top-0 z-50">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16">
          <div class="flex items-center gap-8">
            <div class="flex-shrink-0 flex items-center gap-2">
              <div class="w-8 h-8 bg-indigo-500 rounded-lg flex items-center justify-center text-white">
                <i class="fa-solid fa-user-doctor"></i>
              </div>
              <span class="font-bold text-xl text-slate-800 tracking-tight">心语空间 · 咨询师端</span>
            </div>
            <div class="hidden md:flex space-x-6">
              <RouterLink to="/doctor/home" class="text-indigo-600 border-b-2 border-indigo-600 px-1 pt-1 text-sm font-medium h-full flex items-center">工作台</RouterLink>
              <RouterLink to="/doctor/appointments" class="text-gray-500 hover:text-indigo-600 hover:border-indigo-300 border-b-2 border-transparent px-1 pt-1 text-sm font-medium h-full flex items-center transition">预约管理</RouterLink>
              <RouterLink to="/doctor/chat" class="text-gray-500 hover:text-indigo-600 hover:border-indigo-300 border-b-2 border-transparent px-1 pt-1 text-sm font-medium h-full flex items-center transition">
                咨询对话
                <span v-if="todayStats.messages > 0" class="ml-1 px-1.5 py-0.5 bg-rose-500 text-white text-xs rounded-full">{{ todayStats.messages }}</span>
              </RouterLink>
              <RouterLink to="/doctor/schedule" class="text-gray-500 hover:text-indigo-600 hover:border-indigo-300 border-b-2 border-transparent px-1 pt-1 text-sm font-medium h-full flex items-center transition">排班设置</RouterLink>
            </div>
          </div>
          <div class="flex items-center gap-4">
            <button class="text-gray-400 hover:text-indigo-600 transition relative">
              <i class="fa-regular fa-bell text-xl"></i>
              <span v-if="todayStats.pending > 0" class="absolute -top-1 -right-1 w-2 h-2 bg-rose-500 rounded-full"></span>
            </button>
            <div class="relative group">
              <div class="flex items-center gap-2 cursor-pointer hover:bg-gray-50 p-1 rounded-lg transition">
                <div v-if="avatarUrl" class="h-8 w-8 rounded-full overflow-hidden">
                  <img class="w-full h-full object-cover" :src="avatarUrl" :alt="doctorInfo.name">
                </div>
                <div v-else class="h-8 w-8 rounded-full bg-gradient-to-br from-indigo-400 to-purple-500 flex items-center justify-center text-white font-bold text-sm">
                  {{ doctorInfo.name?.charAt(0) || 'D' }}
                </div>
                <span class="text-sm font-medium text-gray-700">{{ doctorInfo.name || '加载中...' }}</span>
                <i class="fa-solid fa-chevron-down text-xs text-gray-400"></i>
              </div>
              <!-- 下拉菜单 -->
              <div class="absolute right-0 mt-2 w-48 bg-white rounded-xl shadow-lg border border-gray-100 py-2 opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all duration-200">
                <RouterLink to="/doctor/profile" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-50">
                  <i class="fa-solid fa-user-gear mr-2"></i>个人设置
                </RouterLink>
                <hr class="my-2 border-gray-100">
                <button @click="handleLogout" class="w-full text-left px-4 py-2 text-sm text-rose-600 hover:bg-rose-50">
                  <i class="fa-solid fa-right-from-bracket mr-2"></i>退出登录
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </nav>

    <!-- 主要内容 -->
    <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      
      <!-- 欢迎区域 -->
      <div class="mb-8">
        <h1 class="text-2xl font-bold text-slate-800">{{ greeting }}，{{ doctorInfo.name || '医生' }}</h1>
        <p class="text-gray-500 mt-1">今天是 {{ new Date().toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' }) }}</p>
      </div>

      <!-- 统计卡片 -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <div class="bg-white rounded-2xl p-6 shadow-sm border border-gray-100">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm text-gray-500">今日预约</p>
              <p class="text-3xl font-bold text-slate-800 mt-1">{{ todayStats.appointments }}</p>
            </div>
            <div class="w-12 h-12 rounded-xl bg-indigo-100 flex items-center justify-center">
              <i class="fa-solid fa-calendar-check text-indigo-600 text-xl"></i>
            </div>
          </div>
        </div>
        
        <div class="bg-white rounded-2xl p-6 shadow-sm border border-gray-100">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm text-gray-500">已完成</p>
              <p class="text-3xl font-bold text-green-600 mt-1">{{ todayStats.completed }}</p>
            </div>
            <div class="w-12 h-12 rounded-xl bg-green-100 flex items-center justify-center">
              <i class="fa-solid fa-check-circle text-green-600 text-xl"></i>
            </div>
          </div>
        </div>
        
        <div class="bg-white rounded-2xl p-6 shadow-sm border border-gray-100">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm text-gray-500">待确认</p>
              <p class="text-3xl font-bold text-amber-600 mt-1">{{ todayStats.pending }}</p>
            </div>
            <div class="w-12 h-12 rounded-xl bg-amber-100 flex items-center justify-center">
              <i class="fa-solid fa-clock text-amber-600 text-xl"></i>
            </div>
          </div>
        </div>
        
        <div class="bg-white rounded-2xl p-6 shadow-sm border border-gray-100">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm text-gray-500">未读消息</p>
              <p class="text-3xl font-bold text-rose-600 mt-1">{{ todayStats.messages }}</p>
            </div>
            <div class="w-12 h-12 rounded-xl bg-rose-100 flex items-center justify-center">
              <i class="fa-solid fa-comment-dots text-rose-600 text-xl"></i>
            </div>
          </div>
        </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        
        <!-- 今日预约列表 -->
        <div class="lg:col-span-2">
          <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
            <div class="px-6 py-4 border-b border-gray-100 flex items-center justify-between">
              <h2 class="text-lg font-bold text-slate-800">今日预约</h2>
              <RouterLink to="/doctor/appointments" class="text-sm text-indigo-600 hover:text-indigo-700">
                查看全部 <i class="fa-solid fa-arrow-right ml-1"></i>
              </RouterLink>
            </div>
            
            <div v-if="todayAppointments.length === 0" class="p-12 text-center">
              <i class="fa-regular fa-calendar text-4xl text-gray-300 mb-3"></i>
              <p class="text-gray-400">今日暂无预约</p>
            </div>
            
            <div v-else class="divide-y divide-gray-50">
              <div v-for="appointment in todayAppointments" :key="appointment.id" 
                class="px-6 py-4 hover:bg-gray-50 transition flex items-center justify-between">
                <div class="flex items-center gap-4">
                  <div class="w-12 h-12 rounded-full bg-gradient-to-br from-teal-400 to-cyan-500 flex items-center justify-center text-white font-bold">
                    {{ appointment.studentName?.charAt(0) || 'S' }}
                  </div>
                  <div>
                    <h3 class="font-medium text-slate-800">{{ appointment.studentName }}</h3>
                    <p class="text-sm text-gray-500">
                      <i class="fa-regular fa-clock mr-1"></i>{{ appointment.timeSlot }}
                      <span class="mx-2">·</span>
                      <span :class="appointment.type === 'online' ? 'text-indigo-600' : 'text-teal-600'">
                        {{ appointment.type === 'online' ? '线上咨询' : '线下面谈' }}
                      </span>
                    </p>
                  </div>
                </div>
                <div class="flex items-center gap-2">
                  <span v-if="appointment.status === 'pending'" class="px-3 py-1 bg-amber-100 text-amber-700 text-xs rounded-full font-medium">待确认</span>
                  <span v-else-if="appointment.status === 'confirmed'" class="px-3 py-1 bg-green-100 text-green-700 text-xs rounded-full font-medium">已确认</span>
                  <span v-else-if="appointment.status === 'completed'" class="px-3 py-1 bg-gray-100 text-gray-600 text-xs rounded-full font-medium">已完成</span>
                  
                  <div v-if="appointment.status === 'pending'" class="flex gap-1">
                    <button @click="handleAppointmentAction(appointment.id, 'confirm')" 
                      class="p-2 text-green-600 hover:bg-green-50 rounded-lg transition" title="确认">
                      <i class="fa-solid fa-check"></i>
                    </button>
                    <button @click="handleAppointmentAction(appointment.id, 'reject')" 
                      class="p-2 text-rose-600 hover:bg-rose-50 rounded-lg transition" title="拒绝">
                      <i class="fa-solid fa-times"></i>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 待回复消息 -->
        <div>
          <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
            <div class="px-6 py-4 border-b border-gray-100 flex items-center justify-between">
              <h2 class="text-lg font-bold text-slate-800">待回复消息</h2>
              <RouterLink to="/doctor/chat" class="text-sm text-indigo-600 hover:text-indigo-700">
                全部消息 <i class="fa-solid fa-arrow-right ml-1"></i>
              </RouterLink>
            </div>
            
            <div v-if="pendingMessages.length === 0" class="p-8 text-center">
              <i class="fa-regular fa-comments text-4xl text-gray-300 mb-3"></i>
              <p class="text-gray-400">暂无待回复消息</p>
            </div>
            
            <div v-else class="divide-y divide-gray-50">
              <RouterLink v-for="message in pendingMessages" :key="message.id" 
                :to="`/doctor/chat/${message.sessionId}`"
                class="block px-6 py-4 hover:bg-gray-50 transition">
                <div class="flex items-start gap-3">
                  <div class="w-10 h-10 rounded-full bg-gradient-to-br from-purple-400 to-pink-500 flex items-center justify-center text-white font-bold text-sm flex-shrink-0">
                    {{ message.studentName?.charAt(0) || 'S' }}
                  </div>
                  <div class="flex-1 min-w-0">
                    <div class="flex items-center justify-between mb-1">
                      <h4 class="font-medium text-slate-800 text-sm">{{ message.studentName }}</h4>
                      <span class="text-xs text-gray-400">{{ formatTime(message.createdAt) }}</span>
                    </div>
                    <p class="text-sm text-gray-500 line-clamp-2">{{ message.content }}</p>
                  </div>
                </div>
              </RouterLink>
            </div>
          </div>

          <!-- 快捷操作 -->
          <div class="mt-6 bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
            <h2 class="text-lg font-bold text-slate-800 mb-4">快捷操作</h2>
            <div class="grid grid-cols-2 gap-3">
              <RouterLink to="/doctor/schedule" class="p-4 bg-indigo-50 rounded-xl text-center hover:bg-indigo-100 transition">
                <i class="fa-solid fa-calendar-days text-indigo-600 text-xl mb-2"></i>
                <p class="text-sm font-medium text-indigo-700">设置排班</p>
              </RouterLink>
              <RouterLink to="/doctor/profile" class="p-4 bg-purple-50 rounded-xl text-center hover:bg-purple-100 transition">
                <i class="fa-solid fa-user-pen text-purple-600 text-xl mb-2"></i>
                <p class="text-sm font-medium text-purple-700">编辑资料</p>
              </RouterLink>
            </div>
          </div>
        </div>
      </div>

    </main>
  </div>
</template>
