<script setup>
import { ref, onMounted, computed } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import api from '@/api'

const router = useRouter()

// 医生信息
const doctorInfo = ref({
  name: '',
  avatar: ''
})

// 当前显示的周
const currentWeekStart = ref(new Date())

// 排班数据
const schedules = ref({})

// 可选的时间段
const timeSlots = [
  '09:00', '10:00', '11:00',
  '14:00', '15:00', '16:00', '17:00'
]

// 计算当前周的日期
const weekDates = computed(() => {
  const dates = []
  const startDate = new Date(currentWeekStart.value)
  const dayOfWeek = startDate.getDay()
  const mondayOffset = dayOfWeek === 0 ? -6 : 1 - dayOfWeek
  startDate.setDate(startDate.getDate() + mondayOffset)
  
  for (let i = 0; i < 7; i++) {
    const date = new Date(startDate)
    date.setDate(date.getDate() + i)
    dates.push({
      date: date,
      dateStr: date.toISOString().split('T')[0],
      day: date.getDate(),
      weekday: ['周日', '周一', '周二', '周三', '周四', '周五', '周六'][date.getDay()],
      isToday: date.toDateString() === new Date().toDateString(),
      isPast: date < new Date(new Date().setHours(0, 0, 0, 0))
    })
  }
  return dates
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

// 加载排班数据
const loadSchedules = async () => {
  try {
    const response = await api.get('/api/doctor/schedules', {
      params: { weekOffset: 0 }
    })
    if (response.data.success) {
      // 后端返回格式: { schedules: { "2025-12-20": { "09:00": { available, booked }, ... } } }
      // 前端需要格式: { "2025-12-20": [{ time: "09:00", available, booked }, ...] }
      const backendSchedules = response.data.body.schedules || {}
      schedules.value = {}
      
      for (const [dateStr, timeSlots] of Object.entries(backendSchedules)) {
        const slots = []
        for (const [time, status] of Object.entries(timeSlots)) {
          slots.push({
            time,
            available: status.available,
            booked: status.booked
          })
        }
        // 按时间排序
        slots.sort((a, b) => a.time.localeCompare(b.time))
        schedules.value[dateStr] = slots
      }
    }
  } catch (error) {
    console.error('加载排班失败:', error)
  }
}

// 切换时间段可用状态
const toggleSlot = async (dateStr, time) => {
  const date = weekDates.value.find(d => d.dateStr === dateStr)
  if (date?.isPast) return
  
  const currentSlots = schedules.value[dateStr] || []
  const slotIndex = currentSlots.findIndex(s => s.time === time)
  
  let newSlots
  if (slotIndex >= 0) {
    // 已存在，切换状态
    newSlots = currentSlots.map((s, i) => 
      i === slotIndex ? { ...s, available: !s.available } : s
    )
  } else {
    // 不存在，添加为可用
    newSlots = [...currentSlots, { time, available: true }]
  }
  
  // 更新本地状态
  schedules.value[dateStr] = newSlots
  
  // 同步到服务器 - 转换为后端期望的格式
  try {
    // 后端期望格式: { schedules: { "2025-12-20": { "09:00": true, ... } } }
    const schedulesData = {}
    schedulesData[dateStr] = {}
    newSlots.forEach(slot => {
      schedulesData[dateStr][slot.time] = slot.available
    })
    
    await api.put('/api/doctor/schedules', {
      schedules: schedulesData
    })
  } catch (error) {
    console.error('更新排班失败:', error)
  }
}

// 检查时间段是否可用
const isSlotAvailable = (dateStr, time) => {
  const slots = schedules.value[dateStr] || []
  const slot = slots.find(s => s.time === time)
  return slot?.available === true
}

// 检查时间段是否已被预约
const isSlotBooked = (dateStr, time) => {
  const slots = schedules.value[dateStr] || []
  const slot = slots.find(s => s.time === time)
  return slot?.booked === true
}

// 上一周
const prevWeek = () => {
  const date = new Date(currentWeekStart.value)
  date.setDate(date.getDate() - 7)
  currentWeekStart.value = date
  loadSchedules()
}

// 下一周
const nextWeek = () => {
  const date = new Date(currentWeekStart.value)
  date.setDate(date.getDate() + 7)
  currentWeekStart.value = date
  loadSchedules()
}

// 回到本周
const goToThisWeek = () => {
  currentWeekStart.value = new Date()
  loadSchedules()
}

// 批量设置
const batchSetAvailable = async (available) => {
  // 构建后端期望的格式: { schedules: { "2025-12-20": { "09:00": true, ... }, ... } }
  const schedulesData = {}
  
  for (const date of weekDates.value) {
    if (!date.isPast) {
      const slots = timeSlots.map(time => ({ time, available }))
      schedules.value[date.dateStr] = slots
      
      // 转换为后端格式
      schedulesData[date.dateStr] = {}
      slots.forEach(slot => {
        schedulesData[date.dateStr][slot.time] = slot.available
      })
    }
  }
  
  // 一次性发送所有排班更新
  try {
    await api.put('/api/doctor/schedules', {
      schedules: schedulesData
    })
  } catch (error) {
    console.error('批量更新排班失败:', error)
  }
}

onMounted(async () => {
  await loadDoctorInfo()
  await loadSchedules()
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
              <RouterLink to="/doctor/home" class="text-gray-500 hover:text-indigo-600 border-b-2 border-transparent px-1 pt-1 text-sm font-medium h-full flex items-center transition">工作台</RouterLink>
              <RouterLink to="/doctor/appointments" class="text-gray-500 hover:text-indigo-600 border-b-2 border-transparent px-1 pt-1 text-sm font-medium h-full flex items-center transition">预约管理</RouterLink>
              <RouterLink to="/doctor/chat" class="text-gray-500 hover:text-indigo-600 border-b-2 border-transparent px-1 pt-1 text-sm font-medium h-full flex items-center transition">咨询对话</RouterLink>
              <RouterLink to="/doctor/schedule" class="text-indigo-600 border-b-2 border-indigo-600 px-1 pt-1 text-sm font-medium h-full flex items-center">排班设置</RouterLink>
            </div>
          </div>
          <div class="flex items-center gap-4">
            <div class="flex items-center gap-2 cursor-pointer hover:bg-gray-50 p-1 rounded-lg transition">
              <div class="h-8 w-8 rounded-full bg-gradient-to-br from-indigo-400 to-purple-500 flex items-center justify-center text-white font-bold text-sm">
                {{ doctorInfo.name?.charAt(0) || 'D' }}
              </div>
              <span class="text-sm font-medium text-gray-700">{{ doctorInfo.name || '加载中...' }}</span>
            </div>
          </div>
        </div>
      </div>
    </nav>

    <!-- 主要内容 -->
    <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      
      <!-- 页面标题 -->
      <div class="mb-6 flex items-center justify-between">
        <div>
          <h1 class="text-2xl font-bold text-slate-800">排班设置</h1>
          <p class="text-gray-500 mt-1">设置您的可预约时间段</p>
        </div>
        <div class="flex gap-2">
          <button @click="batchSetAvailable(true)" class="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition text-sm">
            <i class="fa-solid fa-check-double mr-2"></i>全部可用
          </button>
          <button @click="batchSetAvailable(false)" class="px-4 py-2 bg-gray-600 text-white rounded-lg hover:bg-gray-700 transition text-sm">
            <i class="fa-solid fa-ban mr-2"></i>全部不可用
          </button>
        </div>
      </div>

      <!-- 周选择器 -->
      <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-4 mb-6">
        <div class="flex items-center justify-between">
          <button @click="prevWeek" class="p-2 text-gray-500 hover:text-indigo-600 hover:bg-indigo-50 rounded-lg transition">
            <i class="fa-solid fa-chevron-left"></i>
          </button>
          <div class="flex items-center gap-4">
            <h3 class="text-lg font-bold text-slate-800">
              {{ weekDates[0]?.date.toLocaleDateString('zh-CN', { month: 'long', day: 'numeric' }) }}
              -
              {{ weekDates[6]?.date.toLocaleDateString('zh-CN', { month: 'long', day: 'numeric' }) }}
            </h3>
            <button @click="goToThisWeek" class="px-3 py-1 text-sm text-indigo-600 hover:bg-indigo-50 rounded-lg transition">
              回到本周
            </button>
          </div>
          <button @click="nextWeek" class="p-2 text-gray-500 hover:text-indigo-600 hover:bg-indigo-50 rounded-lg transition">
            <i class="fa-solid fa-chevron-right"></i>
          </button>
        </div>
      </div>

      <!-- 排班表格 -->
      <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
        <div class="overflow-x-auto">
          <table class="w-full">
            <thead>
              <tr class="bg-gray-50">
                <th class="px-4 py-3 text-left text-sm font-medium text-gray-500 w-24">时间</th>
                <th v-for="date in weekDates" :key="date.dateStr" 
                  :class="[
                    'px-4 py-3 text-center text-sm font-medium',
                    date.isToday ? 'bg-indigo-50 text-indigo-700' : 'text-gray-500',
                    date.isPast ? 'opacity-50' : ''
                  ]">
                  <div>{{ date.weekday }}</div>
                  <div :class="date.isToday ? 'text-indigo-600 font-bold' : 'text-slate-800'">{{ date.day }}日</div>
                </th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-50">
              <tr v-for="time in timeSlots" :key="time">
                <td class="px-4 py-3 text-sm font-medium text-slate-700">{{ time }}</td>
                <td v-for="date in weekDates" :key="`${date.dateStr}-${time}`" 
                  class="px-2 py-2 text-center">
                  <button 
                    @click="toggleSlot(date.dateStr, time)"
                    :disabled="date.isPast || isSlotBooked(date.dateStr, time)"
                    :class="[
                      'w-full py-3 rounded-xl text-sm font-medium transition-all duration-200',
                      date.isPast 
                        ? 'bg-gray-100 text-gray-400 cursor-not-allowed'
                        : isSlotBooked(date.dateStr, time)
                          ? 'bg-amber-100 text-amber-700 cursor-not-allowed'
                          : isSlotAvailable(date.dateStr, time)
                            ? 'bg-green-100 text-green-700 hover:bg-green-200'
                            : 'bg-gray-100 text-gray-500 hover:bg-gray-200'
                    ]">
                    <span v-if="isSlotBooked(date.dateStr, time)">
                      <i class="fa-solid fa-user mr-1"></i>已预约
                    </span>
                    <span v-else-if="isSlotAvailable(date.dateStr, time)">
                      <i class="fa-solid fa-check mr-1"></i>可预约
                    </span>
                    <span v-else>
                      <i class="fa-solid fa-minus mr-1"></i>不可用
                    </span>
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- 图例说明 -->
      <div class="mt-6 flex items-center gap-6 text-sm">
        <div class="flex items-center gap-2">
          <span class="w-4 h-4 rounded bg-green-100 border border-green-200"></span>
          <span class="text-gray-600">可预约</span>
        </div>
        <div class="flex items-center gap-2">
          <span class="w-4 h-4 rounded bg-amber-100 border border-amber-200"></span>
          <span class="text-gray-600">已被预约</span>
        </div>
        <div class="flex items-center gap-2">
          <span class="w-4 h-4 rounded bg-gray-100 border border-gray-200"></span>
          <span class="text-gray-600">不可用</span>
        </div>
      </div>

    </main>
  </div>
</template>
