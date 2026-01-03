<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api, { getAvatarUrl } from '@/api'

const route = useRoute()
const router = useRouter()

const doctorId = ref(route.params.id)
const doctor = ref(null)
const schedule = ref(null)
const loading = ref(true)

// 预约表单
const selectedDate = ref(null)
const selectedTimeSlot = ref(null)
const consultationType = ref('offline')
const description = ref('')
const submitting = ref(false)

// 周偏移
const weekOffset = ref(0)

// 获取医生详情
const fetchDoctorDetail = async () => {
  try {
    const response = await api.get(`/api/user/doctors/${doctorId.value}`)
    if (response.data.success) {
      doctor.value = response.data.body
    }
  } catch (error) {
    console.error('获取医生详情失败:', error)
  }
}

// 获取医生排班
const fetchDoctorSchedule = async () => {
  try {
    const response = await api.get(`/api/user/doctors/${doctorId.value}/schedule`, {
      params: { weekOffset: weekOffset.value }
    })
    if (response.data.success) {
      schedule.value = response.data.body
      // 默认选择第一个有可用时间的日期
      if (schedule.value && schedule.value.schedules) {
        // 找到第一个有可预约时间段的日期
        const today = new Date().toISOString().split('T')[0]
        const dates = Object.keys(schedule.value.schedules).sort()
        for (const date of dates) {
          // 跳过过去的日期
          if (date < today) continue
          const daySchedule = schedule.value.schedules[date]
          // 检查这个日期是否有可用且未被预约的时间段
          const hasAvailable = Object.values(daySchedule).some(slot => slot.available && !slot.booked)
          if (hasAvailable) {
            selectedDate.value = date
            break
          }
        }
      }
    }
  } catch (error) {
    console.error('获取医生排班失败:', error)
  }
}

// 可用日期列表
const availableDates = computed(() => {
  if (!schedule.value || !schedule.value.schedules) return []
  const today = new Date().toISOString().split('T')[0]
  return Object.keys(schedule.value.schedules).sort().map(date => {
    const daySchedule = schedule.value.schedules[date]
    // 检查这个日期是否有可预约的时间段（available=true且booked=false）
    const hasAvailableSlots = Object.values(daySchedule).some(slot => slot.available && !slot.booked)
    // 过去的日期不能预约
    const isPast = date < today
    return {
      date,
      dayOfWeek: new Date(date).toLocaleDateString('zh-CN', { weekday: 'short' }),
      dayOfMonth: new Date(date).getDate(),
      isToday: date === today,
      hasAvailableSlots: hasAvailableSlots && !isPast // 过去的日期标记为不可用
    }
  })
})

// 当前选中日期的可用时间段
const availableTimeSlots = computed(() => {
  if (!schedule.value || !selectedDate.value || !schedule.value.schedules[selectedDate.value]) {
    return []
  }
  // 转换后端数据结构为前端需要的格式
  const daySchedule = schedule.value.schedules[selectedDate.value]
  const today = new Date().toISOString().split('T')[0]
  const isToday = selectedDate.value === today
  const currentHour = new Date().getHours()
  const currentMinute = new Date().getMinutes()
  
  return Object.entries(daySchedule).map(([timeSlot, status]) => {
    // 检查时间段是否已过（仅对今天有效）
    let isPastTime = false
    if (isToday) {
      // 解析时间段，格式如 "09:00" 或 "14:00"
      const [slotHour, slotMinute] = timeSlot.split(':').map(Number)
      // 如果时间段的小时小于当前小时，或者小时相同但分钟已过，则标记为已过
      isPastTime = slotHour < currentHour || (slotHour === currentHour && slotMinute <= currentMinute)
    }
    
    return {
      timeSlot,
      available: status.available,
      booked: status.booked,
      isPast: isPastTime
    }
  }).filter(slot => slot.available) // 只显示可用的时间段
})

// 选择日期
const selectDate = (date) => {
  selectedDate.value = date
  selectedTimeSlot.value = null
}

// 选择时间段
const selectTimeSlot = (timeSlot) => {
  selectedTimeSlot.value = timeSlot
}

// 格式化日期
const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', { month: 'long', day: 'numeric', weekday: 'long' })
}

// 提交预约
const submitAppointment = async () => {
  if (!selectedDate.value || !selectedTimeSlot.value) {
    alert('请选择预约日期和时间')
    return
  }
  
  try {
    submitting.value = true
    const response = await api.post('/api/user/appointments', {
      doctorId: parseInt(doctorId.value),
      date: selectedDate.value,
      timeSlot: selectedTimeSlot.value,
      type: consultationType.value,
      description: description.value
    })
    
    if (response.data.success) {
      alert('预约成功！医生确认后您将收到通知。')
      router.push('/appointments')
    } else {
      alert(response.data.message || '预约失败')
    }
  } catch (error) {
    console.error('预约失败:', error)
    alert(error.response?.data?.message || '预约失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

// 页面初始化
onMounted(async () => {
  loading.value = true
  await Promise.all([fetchDoctorDetail(), fetchDoctorSchedule()])
  loading.value = false
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
              <i class="fa-solid fa-arrow-left mr-1"></i> 返回列表
            </button>
            <div class="h-6 w-px bg-gray-200"></div>
            <h1 class="font-bold text-xl text-slate-800">医生详情</h1>
          </div>
        </div>
      </div>
    </nav>

    <!-- 加载状态 -->
    <div v-if="loading" class="flex-1 flex items-center justify-center">
      <div class="text-center">
        <i class="fa-solid fa-spinner fa-spin text-4xl text-teal-500 mb-4"></i>
        <p class="text-gray-500">加载中...</p>
      </div>
    </div>

    <!-- 主内容 -->
    <main v-else-if="doctor" class="flex-1 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 w-full">
        
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        
        <!-- 左侧：医生信息 -->
        <div class="lg:col-span-1 space-y-6">
          <!-- 基本信息卡片 -->
          <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 text-center">
            <div class="relative inline-block mb-4">
              <img :src="getAvatarUrl(doctor.avatar) || 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2'" 
                   :alt="doctor.name" 
                   class="w-32 h-32 rounded-full object-cover border-4 border-teal-50">
              <div class="absolute bottom-0 right-0 bg-white px-3 py-1 rounded-full shadow-sm border border-gray-100 flex items-center gap-1">
                <i class="fa-solid fa-star text-yellow-400 text-sm"></i>
                <span class="text-sm font-bold">{{ doctor.rating || '5.0' }}</span>
              </div>
            </div>
            <h2 class="text-2xl font-bold text-slate-800 mb-1">{{ doctor.name }}</h2>
            <p class="text-sm text-gray-500 mb-4">{{ doctor.title }} | {{ doctor.yearsOfExperience }}年从业经验</p>
            
            <div class="flex justify-center gap-2 flex-wrap mb-6">
              <span v-for="tag in (doctor.tags || [])" :key="tag" 
                    class="text-xs text-teal-600 bg-teal-50 px-3 py-1 rounded-full font-medium">
                {{ tag }}
              </span>
            </div>

            <div class="grid grid-cols-3 gap-4 border-t border-gray-100 pt-6">
              <div>
                <div class="text-xl font-bold text-slate-800">{{ doctor.totalHours || 0 }}+</div>
                <div class="text-xs text-gray-400">咨询时长</div>
              </div>
              <div>
                <div class="text-xl font-bold text-slate-800">{{ doctor.patientCount || 0 }}</div>
                <div class="text-xs text-gray-400">帮助人数</div>
              </div>
              <div>
                <div class="text-xl font-bold text-slate-800">{{ doctor.satisfactionRate || 98 }}%</div>
                <div class="text-xs text-gray-400">好评率</div>
              </div>
            </div>
          </div>

          <!-- 简介卡片 -->
          <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
            <h3 class="font-bold text-lg text-slate-800 mb-4">关于我</h3>
            <p class="text-sm text-gray-500 leading-relaxed mb-4">
              {{ doctor.description || '暂无简介' }}
            </p>
            <h4 class="font-bold text-sm text-slate-800 mb-2">资质认证</h4>
            <ul v-if="doctor.certifications && doctor.certifications.length" class="space-y-2">
              <li v-for="cert in doctor.certifications" :key="cert" 
                  class="flex items-center gap-2 text-xs text-gray-500">
                <i class="fa-solid fa-certificate text-teal-500"></i> {{ cert }}
              </li>
            </ul>
            <p v-else class="text-xs text-gray-400">暂无认证信息</p>
          </div>
        </div>

            <!-- 右侧：预约与评价 -->
            <div class="lg:col-span-2 space-y-6">
                
                <!-- 预约卡片 -->
                <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-8">
                    <h3 class="font-bold text-xl text-slate-800 mb-6">预约咨询</h3>
                    
                    <!-- 日期选择 -->
                    <div class="mb-8">
                        <h4 class="text-sm font-bold text-gray-700 mb-3">选择日期</h4>
                        <div v-if="availableDates.length > 0" class="flex gap-4 overflow-x-auto pb-2">
                            <button v-for="dateItem in availableDates" :key="dateItem.date"
                                    @click="dateItem.hasAvailableSlots && selectDate(dateItem.date)"
                                    :disabled="!dateItem.hasAvailableSlots"
                                    :class="[
                                      'flex flex-col items-center justify-center w-20 h-24 rounded-xl flex-shrink-0 transition transform',
                                      !dateItem.hasAvailableSlots
                                        ? 'bg-gray-100 text-gray-400 cursor-not-allowed'
                                        : selectedDate === dateItem.date
                                          ? 'bg-teal-500 text-white shadow-md shadow-teal-200 hover:-translate-y-1'
                                          : 'bg-white text-gray-500 border border-gray-200 hover:border-teal-500 hover:text-teal-600 hover:-translate-y-1'
                                    ]">
                                <span class="text-xs font-medium" :class="selectedDate === dateItem.date ? 'opacity-80' : 'opacity-60'">
                                    {{ dateItem.isToday ? '今天' : dateItem.dayOfWeek }}
                                </span>
                                <span class="text-2xl font-bold my-1">{{ dateItem.dayOfMonth }}</span>
                                <span class="text-xs font-medium" :class="selectedDate === dateItem.date ? 'opacity-80' : 'opacity-60'">
                                    {{ dateItem.hasAvailableSlots ? dateItem.dayOfWeek : '无排班' }}
                                </span>
                            </button>
                        </div>
                        <p v-else class="text-sm text-gray-400">暂无可预约日期，请联系医生设置排班</p>
                    </div>

                    <!-- 时间段选择 -->
                    <div class="mb-8">
                        <h4 class="text-sm font-bold text-gray-700 mb-3">选择时间段</h4>
                        <div v-if="availableTimeSlots.length > 0" class="grid grid-cols-3 md:grid-cols-5 gap-3">
                            <button v-for="slot in availableTimeSlots" :key="slot.timeSlot"
                                    @click="!slot.booked && !slot.isPast && selectTimeSlot(slot.timeSlot)"
                                    :disabled="slot.booked || slot.isPast"
                                    :class="[
                                      'py-2.5 rounded-lg border text-sm transition',
                                      slot.booked || slot.isPast
                                        ? 'border-gray-200 text-gray-400 bg-gray-50 cursor-not-allowed line-through'
                                        : selectedTimeSlot === slot.timeSlot
                                          ? 'border-teal-500 text-teal-600 bg-teal-50 font-bold shadow-sm ring-2 ring-teal-100'
                                          : 'border-gray-200 text-slate-600 bg-white hover:border-teal-500 hover:text-teal-600'
                                    ]">
                                {{ slot.timeSlot }}
                            </button>
                        </div>
                        <p v-else class="text-sm text-gray-400">{{ selectedDate ? '该日期暂无可预约时间' : '请先选择日期' }}</p>
                    </div>

                    <!-- 咨询方式 -->
                    <div class="mb-8">
                        <div class="p-4 rounded-xl border border-teal-500 bg-teal-50 flex items-start gap-3">
                            <div class="w-10 h-10 rounded-full bg-blue-100 flex items-center justify-center text-blue-600 flex-shrink-0">
                                <i class="fa-solid fa-location-dot"></i>
                            </div>
                            <div>
                                <h4 class="font-bold text-sm text-slate-800">线下咨询</h4>
                                <p class="text-xs text-gray-500 mt-1">{{ doctor?.location || '未设置诊室位置' }}</p>
                            </div>
                        </div>
                    </div>

                    <!-- 问题描述 -->
                    <div class="mb-8">
                        <h4 class="text-sm font-bold text-gray-700 mb-3">问题描述（可选）</h4>
                        <textarea v-model="description"
                                  placeholder="简要描述您的困扰或咨询问题..."
                                  rows="3"
                                  class="w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-teal-500 focus:border-transparent resize-none text-sm"></textarea>
                    </div>

                    <!-- 提交按钮 -->
                    <div class="flex items-center justify-between pt-6 border-t border-gray-100">
                        <div v-if="selectedDate && selectedTimeSlot">
                            <span class="text-xs text-gray-400 block">已选时间</span>
                            <span class="text-lg font-bold text-teal-600">{{ formatDate(selectedDate) }} {{ selectedTimeSlot }}</span>
                        </div>
                        <div v-else>
                            <span class="text-sm text-gray-400">请选择预约日期和时间</span>
                        </div>
                        <button @click="submitAppointment"
                                :disabled="!selectedDate || !selectedTimeSlot || submitting"
                                :class="[
                                  'font-bold px-8 py-3 rounded-xl shadow-lg transition transform',
                                  selectedDate && selectedTimeSlot && !submitting
                                    ? 'bg-teal-500 text-white shadow-teal-200 hover:bg-teal-600 hover:-translate-y-0.5'
                                    : 'bg-gray-300 text-gray-500 cursor-not-allowed'
                                ]">
                            <i v-if="submitting" class="fa-solid fa-spinner fa-spin mr-2"></i>
                            {{ submitting ? '提交中...' : '确认预约' }}
                        </button>
                    </div>
                </div>

            </div>
        </div>

    </main>
  </div>
</template>
