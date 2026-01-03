<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/api'

const router = useRouter()

const appointments = ref([])
const loading = ref(true)
const activeTab = ref('all') // all, pending, confirmed, completed, cancelled

// 获取预约列表
const fetchAppointments = async () => {
  try {
    loading.value = true
    const response = await api.get('/api/user/appointments')
    if (response.data.success) {
      appointments.value = response.data.body
    }
  } catch (error) {
    console.error('获取预约列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 过滤预约
const filteredAppointments = computed(() => {
  if (activeTab.value === 'all') {
    return appointments.value
  }
  return appointments.value.filter(apt => apt.status === activeTab.value)
})

// 状态映射
const statusMap = {
  pending: { text: '待确认', color: 'yellow' },
  confirmed: { text: '已确认', color: 'green' },
  completed: { text: '已完成', color: 'gray' },
  rejected: { text: '已拒绝', color: 'red' },
  cancelled: { text: '已取消', color: 'gray' }
}

// 格式化日期
const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', { month: 'long', day: 'numeric', weekday: 'long' })
}

// 取消预约
const cancelAppointment = async (appointmentId) => {
  if (!confirm('确定要取消这个预约吗？')) return
  
  try {
    const response = await api.put(`/api/user/appointments/${appointmentId}/cancel`)
    if (response.data.success) {
      alert('预约已取消')
      await fetchAppointments()
    }
  } catch (error) {
    console.error('取消预约失败:', error)
    alert(error.response?.data?.message || '取消失败')
  }
}

onMounted(() => {
  fetchAppointments()
})
</script>

<template>
  <div class="bg-gray-50 text-slate-800 min-h-screen flex flex-col">
    <!-- 顶部导航栏 -->
    <nav class="bg-white shadow-sm border-b border-gray-100 sticky top-0 z-50">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16">
          <div class="flex items-center gap-4">
            <button @click="router.back()" class="text-gray-500 hover:text-teal-600 transition">
              <i class="fa-solid fa-arrow-left mr-1"></i> 返回
            </button>
            <div class="h-6 w-px bg-gray-200"></div>
            <h1 class="font-bold text-xl text-slate-800">我的预约</h1>
          </div>
          <button @click="router.push('/doctors')" 
                  class="text-teal-600 hover:text-teal-700 font-medium">
            <i class="fa-solid fa-plus mr-1"></i> 新建预约
          </button>
        </div>
      </div>
    </nav>

    <!-- 主内容 -->
    <main class="flex-1 max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-8 w-full">
      
      <!-- 状态筛选标签 -->
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 mb-6 p-4">
        <div class="flex gap-2 overflow-x-auto">
          <button @click="activeTab = 'all'"
                  :class="['px-4 py-2 rounded-lg text-sm font-medium transition whitespace-nowrap',
                           activeTab === 'all' ? 'bg-teal-500 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200']">
            全部
          </button>
          <button @click="activeTab = 'pending'"
                  :class="['px-4 py-2 rounded-lg text-sm font-medium transition whitespace-nowrap',
                           activeTab === 'pending' ? 'bg-teal-500 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200']">
            待确认
          </button>
          <button @click="activeTab = 'confirmed'"
                  :class="['px-4 py-2 rounded-lg text-sm font-medium transition whitespace-nowrap',
                           activeTab === 'confirmed' ? 'bg-teal-500 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200']">
            已确认
          </button>
          <button @click="activeTab = 'completed'"
                  :class="['px-4 py-2 rounded-lg text-sm font-medium transition whitespace-nowrap',
                           activeTab === 'completed' ? 'bg-teal-500 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200']">
            已完成
          </button>
          <button @click="activeTab = 'cancelled'"
                  :class="['px-4 py-2 rounded-lg text-sm font-medium transition whitespace-nowrap',
                           activeTab === 'cancelled' ? 'bg-teal-500 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200']">
            已取消
          </button>
        </div>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="text-center py-12">
        <i class="fa-solid fa-spinner fa-spin text-4xl text-teal-500 mb-4"></i>
        <p class="text-gray-500">加载中...</p>
      </div>

      <!-- 预约列表 -->
      <div v-else-if="filteredAppointments.length > 0" class="space-y-4">
        <div v-for="appointment in filteredAppointments" :key="appointment.id"
             class="bg-white rounded-xl shadow-sm border border-gray-100 p-6 hover:shadow-md transition">
          
          <div class="flex items-start justify-between mb-4">
            <div class="flex items-center gap-4">
              <img :src="appointment.doctorAvatar || 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2'" 
                   :alt="appointment.doctorName"
                   class="w-16 h-16 rounded-full object-cover border-2 border-teal-50">
              <div>
                <h3 class="text-lg font-bold text-slate-800">{{ appointment.doctorName }}</h3>
                <p class="text-sm text-gray-500">心理咨询师</p>
              </div>
            </div>
            <span :class="[
              'px-3 py-1 rounded-full text-xs font-medium',
              statusMap[appointment.status]?.color === 'yellow' && 'bg-yellow-100 text-yellow-700',
              statusMap[appointment.status]?.color === 'green' && 'bg-green-100 text-green-700',
              statusMap[appointment.status]?.color === 'gray' && 'bg-gray-100 text-gray-600',
              statusMap[appointment.status]?.color === 'red' && 'bg-red-100 text-red-700'
            ]">
              {{ statusMap[appointment.status]?.text || appointment.status }}
            </span>
          </div>

          <div class="grid grid-cols-2 md:grid-cols-4 gap-4 mb-4">
            <div class="bg-gray-50 rounded-lg p-3">
              <p class="text-xs text-gray-500 mb-1">预约日期</p>
              <p class="font-medium text-slate-800">{{ formatDate(appointment.date) }}</p>
            </div>
            <div class="bg-gray-50 rounded-lg p-3">
              <p class="text-xs text-gray-500 mb-1">时间段</p>
              <p class="font-medium text-slate-800">{{ appointment.timeSlot }}</p>
            </div>
            <div class="bg-gray-50 rounded-lg p-3">
              <p class="text-xs text-gray-500 mb-1">咨询方式</p>
              <p class="font-medium text-slate-800">
                {{ appointment.type === 'offline' ? '线下咨询' : '在线咨询' }}
              </p>
            </div>
            <div v-if="appointment.location" class="bg-gray-50 rounded-lg p-3">
              <p class="text-xs text-gray-500 mb-1">咨询地点</p>
              <p class="font-medium text-slate-800">{{ appointment.location }}</p>
            </div>
          </div>

          <div v-if="appointment.description" class="mb-4">
            <p class="text-xs text-gray-500 mb-1">问题描述</p>
            <p class="text-sm text-gray-700">{{ appointment.description }}</p>
          </div>

          <div class="flex justify-end gap-2 pt-4 border-t border-gray-100">
            <button v-if="appointment.status === 'pending' || appointment.status === 'confirmed'"
                    @click="cancelAppointment(appointment.id)"
                    class="px-4 py-2 text-sm font-medium text-red-600 hover:bg-red-50 rounded-lg transition">
              取消预约
            </button>
            <button @click="router.push(`/doctor/${appointment.doctorId}`)"
                    class="px-4 py-2 text-sm font-medium text-teal-600 hover:bg-teal-50 rounded-lg transition">
              查看医生
            </button>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-else class="text-center py-12">
        <i class="fa-regular fa-calendar-xmark text-6xl text-gray-300 mb-4"></i>
        <p class="text-gray-400 mb-4">暂无预约记录</p>
        <button @click="router.push('/doctors')"
                class="px-6 py-2 bg-teal-500 text-white font-medium rounded-lg hover:bg-teal-600 transition">
          立即预约
        </button>
      </div>

    </main>
  </div>
</template>
