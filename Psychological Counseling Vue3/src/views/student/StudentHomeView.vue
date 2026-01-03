<script setup>
import { ref, onMounted, computed } from 'vue'
import { RouterLink } from 'vue-router'
import api, { getAvatarUrl } from '@/api'

// 用户信息
const userInfo = ref({
  name: '',
  avatar: '',
  college: ''
})

// 公告列表
const announcements = ref([])

// 公告弹窗
const showAnnouncementModal = ref(false)
const selectedAnnouncement = ref(null)

// 心情签到相关
const moods = [
  { emoji: '😄', label: '开心', value: 'happy' },
  { emoji: '😐', label: '一般', value: 'normal' },
  { emoji: '😔', label: '低落', value: 'sad' },
  { emoji: '😡', label: '焦虑', value: 'anxious' }
]
const todayMood = ref(null)
const checkInDays = ref(0)

// 获取今天的日期字符串（YYYY-MM-DD）
const getTodayKey = () => {
  const today = new Date()
  return `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`
}

// 加载签到数据
const loadCheckInData = () => {
  try {
    const todayKey = getTodayKey()
    const checkInData = JSON.parse(localStorage.getItem('moodCheckIn') || '{}')
    
    // 检查今天是否已签到
    if (checkInData[todayKey]) {
      todayMood.value = checkInData[todayKey]
    }
    
    // 计算连续签到天数
    calculateCheckInDays(checkInData)
  } catch (error) {
    console.error('加载签到数据失败:', error)
  }
}

// 计算连续签到天数
const calculateCheckInDays = (checkInData) => {
  const sortedDates = Object.keys(checkInData).sort().reverse()
  if (sortedDates.length === 0) {
    checkInDays.value = 0
    return
  }
  
  let days = 0
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  
  for (let i = 0; i < sortedDates.length; i++) {
    const checkDate = new Date(sortedDates[i])
    checkDate.setHours(0, 0, 0, 0)
    
    const expectedDate = new Date(today)
    expectedDate.setDate(expectedDate.getDate() - i)
    expectedDate.setHours(0, 0, 0, 0)
    
    if (checkDate.getTime() === expectedDate.getTime()) {
      days++
    } else {
      break
    }
  }
  
  checkInDays.value = days
}

// 心情签到
const checkInMood = (mood) => {
  try {
    const todayKey = getTodayKey()
    const checkInData = JSON.parse(localStorage.getItem('moodCheckIn') || '{}')
    
    // 保存今天的心情
    checkInData[todayKey] = mood
    localStorage.setItem('moodCheckIn', JSON.stringify(checkInData))
    
    // 更新当前状态
    todayMood.value = mood
    
    // 重新计算连续签到天数
    calculateCheckInDays(checkInData)
  } catch (error) {
    console.error('签到失败:', error)
  }
}

// 判断是否是当前选中的心情
const isSelectedMood = (moodValue) => {
  return todayMood.value === moodValue
}

// 打开公告详情
const openAnnouncement = (announcement) => {
  selectedAnnouncement.value = announcement
  showAnnouncementModal.value = true
}

// 关闭公告详情
const closeAnnouncement = () => {
  showAnnouncementModal.value = false
  selectedAnnouncement.value = null
}

// 计算属性：获取正确的头像URL
const avatarUrl = computed(() => {
  return getAvatarUrl(userInfo.value.avatar) || 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?ixlib=rb-4.0.3&auto=format&fit=crop&w=200&q=80'
})

// 加载用户信息
const loadUserInfo = async () => {
  try {
    const response = await api.get('/api/user/profile')
    console.log('首页用户信息响应:', response.data)
    if (response.data.success) {
      userInfo.value = response.data.body
      console.log('用户信息已加载:', userInfo.value)
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
  }
}

// 加载公告列表
const loadAnnouncements = async () => {
  try {
    const response = await api.get('/api/admin/public/announcements?limit=5')
    if (response.data.success) {
      announcements.value = response.data.body
    }
  } catch (error) {
    console.error('加载公告失败:', error)
  }
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

// 根据时间获取问候语
const getGreeting = () => {
  const hour = new Date().getHours()
  if (hour < 6) return '凌晨好'
  if (hour < 9) return '早安'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 18) return '下午好'
  if (hour < 22) return '晚上好'
  return '夜深了'
}

onMounted(() => {
  loadUserInfo()
  loadAnnouncements()
  loadCheckInData()
})
</script>

<template>
  <div class="bg-gray-50 text-slate-800 min-h-screen flex flex-col">
    <!-- 顶部导航栏 -->
    <nav class="bg-white shadow-sm border-b border-gray-100 sticky top-0 z-50">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div class="flex justify-between h-16">
                <div class="flex items-center gap-8">
                    <div class="flex-shrink-0 flex items-center gap-2">
                        <div class="w-8 h-8 bg-teal-500 rounded-lg flex items-center justify-center text-white">
                            <i class="fa-solid fa-heart-pulse"></i>
                        </div>
                        <span class="font-bold text-xl text-slate-800 tracking-tight">心语空间</span>
                    </div>
                    <div class="hidden md:flex space-x-8">
                        <RouterLink to="/" class="text-teal-600 border-b-2 border-teal-600 px-1 pt-1 text-sm font-medium h-full flex items-center">首页</RouterLink>
                        <RouterLink to="/ai-chat" class="text-gray-500 hover:text-teal-600 hover:border-teal-300 border-b-2 border-transparent px-1 pt-1 text-sm font-medium h-full flex items-center transition">AI 咨询</RouterLink>
                        <RouterLink to="/doctors" class="text-gray-500 hover:text-teal-600 hover:border-teal-300 border-b-2 border-transparent px-1 pt-1 text-sm font-medium h-full flex items-center transition">预约医生</RouterLink>
                        <RouterLink to="/assessment" class="text-gray-500 hover:text-teal-600 hover:border-teal-300 border-b-2 border-transparent px-1 pt-1 text-sm font-medium h-full flex items-center transition">心理测评</RouterLink>
                    </div>
                </div>
                <div class="flex items-center gap-4">
                    <button class="text-gray-400 hover:text-teal-600 transition relative">
                        <i class="fa-regular fa-bell text-xl"></i>
                        <span class="absolute -top-1 -right-1 w-2 h-2 bg-rose-500 rounded-full"></span>
                    </button>
                    <RouterLink to="/profile" class="flex items-center gap-2 cursor-pointer hover:bg-gray-50 p-1 rounded-lg transition">
                        <div class="h-8 w-8 rounded-full overflow-hidden">
                            <img class="w-full h-full object-cover" :src="avatarUrl" :alt="userInfo.name">
                        </div>
                        <span class="text-sm font-medium text-gray-700">{{ userInfo.name || '加载中...' }}</span>
                    </RouterLink>
                </div>
            </div>
        </div>
    </nav>

    <!-- 主要内容 -->
    <main class="flex-1 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 w-full">
        
        <!-- 欢迎区 & 情绪签到 -->
        <div class="grid grid-cols-1 lg:grid-cols-3 gap-8 mb-10">
            <!-- 左侧：欢迎语 -->
            <div class="lg:col-span-2 bg-gradient-to-r from-teal-500 to-emerald-600 rounded-2xl p-8 text-white shadow-lg relative overflow-hidden">
                <div class="relative z-10">
                    <h1 class="text-3xl font-bold mb-2">{{ getGreeting() }}，{{ userInfo.name || '同学' }} ☀️</h1>
                    <p class="text-teal-50 opacity-90 mb-6 max-w-lg">愿你今天拥有好心情。如果感到疲惫，记得这里永远有一个角落为你保留。</p>
                    <RouterLink to="/ai-chat" class="bg-white text-teal-600 px-6 py-2.5 rounded-lg font-semibold shadow-sm hover:bg-teal-50 transition inline-block">
                        开始 AI 对话
                    </RouterLink>
                </div>
                <!-- 装饰背景 -->
                <div class="absolute right-0 bottom-0 opacity-10 transform translate-x-10 translate-y-10">
                    <i class="fa-solid fa-leaf text-9xl"></i>
                </div>
            </div>

            <!-- 右侧：情绪签到 -->
            <div class="bg-white rounded-2xl p-6 shadow-sm border border-gray-100 flex flex-col justify-center">
                <h3 class="font-bold text-slate-800 mb-4">今天感觉怎么样？</h3>
                <div class="grid grid-cols-4 gap-2">
                    <button 
                        v-for="mood in moods" 
                        :key="mood.value"
                        @click="checkInMood(mood.value)"
                        :class="[
                            'flex flex-col items-center gap-2 p-2 rounded-xl transition group',
                            isSelectedMood(mood.value) 
                                ? 'bg-teal-50 border border-teal-100' 
                                : 'hover:bg-gray-50'
                        ]"
                    >
                        <div 
                            :class="[
                                'text-3xl transition',
                                isSelectedMood(mood.value) 
                                    ? 'scale-110' 
                                    : 'group-hover:scale-110'
                            ]"
                        >
                            {{ mood.emoji }}
                        </div>
                        <span 
                            :class="[
                                'text-xs font-medium',
                                isSelectedMood(mood.value) 
                                    ? 'text-teal-700 font-bold' 
                                    : 'text-gray-500'
                            ]"
                        >
                            {{ mood.label }}
                        </span>
                    </button>
                </div>
                <p class="text-xs text-gray-400 text-center mt-4">
                    {{ checkInDays > 0 ? `已连续签到 ${checkInDays} 天` : '今天还未签到' }}
                </p>
            </div>
        </div>

        <!-- 核心功能入口 -->
        <div class="mb-10">
            <h2 class="text-xl font-bold text-slate-800 mb-6">你需要什么帮助？</h2>
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                <!-- AI 心理助手 -->
                <RouterLink to="/ai-chat" class="group bg-white p-6 rounded-2xl shadow-sm border border-gray-100 hover:shadow-md hover:border-indigo-100 transition flex flex-col items-start">
                    <div class="w-12 h-12 rounded-xl bg-indigo-100 text-indigo-600 flex items-center justify-center mb-4 group-hover:scale-110 transition">
                        <i class="fa-solid fa-robot text-xl"></i>
                    </div>
                    <h3 class="font-bold text-lg text-slate-800 mb-1">AI 心理助手</h3>
                    <p class="text-sm text-gray-500 mb-4">24小时即时陪伴，随时倾听你的心声。</p>
                    <span class="text-indigo-600 text-sm font-medium group-hover:translate-x-1 transition flex items-center gap-1">
                        立即对话 <i class="fa-solid fa-arrow-right"></i>
                    </span>
                </RouterLink>

                <!-- 预约咨询师 -->
                <RouterLink to="/doctors" class="group bg-white p-6 rounded-2xl shadow-sm border border-gray-100 hover:shadow-md hover:border-teal-100 transition flex flex-col items-start">
                    <div class="w-12 h-12 rounded-xl bg-teal-100 text-teal-600 flex items-center justify-center mb-4 group-hover:scale-110 transition">
                        <i class="fa-solid fa-user-doctor text-xl"></i>
                    </div>
                    <h3 class="font-bold text-lg text-slate-800 mb-1">预约咨询师</h3>
                    <p class="text-sm text-gray-500 mb-4">专业医生线下/线上咨询，解决深层困扰。</p>
                    <span class="text-teal-600 text-sm font-medium group-hover:translate-x-1 transition flex items-center gap-1">
                        查看列表 <i class="fa-solid fa-arrow-right"></i>
                    </span>
                </RouterLink>

                <!-- 心理测评 -->
                <RouterLink to="/assessment" class="group bg-white p-6 rounded-2xl shadow-sm border border-gray-100 hover:shadow-md hover:border-purple-100 transition flex flex-col items-start">
                    <div class="w-12 h-12 rounded-xl bg-purple-100 text-purple-600 flex items-center justify-center mb-4 group-hover:scale-110 transition">
                        <i class="fa-solid fa-clipboard-list text-xl"></i>
                    </div>
                    <h3 class="font-bold text-lg text-slate-800 mb-1">心理测评</h3>
                    <p class="text-sm text-gray-500 mb-4">专业量表评估，了解自己的心理健康状况。</p>
                    <span class="text-purple-600 text-sm font-medium group-hover:translate-x-1 transition flex items-center gap-1">
                        开始测评 <i class="fa-solid fa-arrow-right"></i>
                    </span>
                </RouterLink>
            </div>
        </div>

        <!-- 心灵治愈 & 公告 -->
        <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
            <!-- 心灵治愈文案 -->
            <div class="lg:col-span-2">
                <div class="flex justify-between items-center mb-6">
                    <h2 class="text-xl font-bold text-slate-800 flex items-center gap-2">
                        <i class="fa-solid fa-heart text-teal-500"></i>
                        心灵治愈
                    </h2>
                </div>
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <!-- 治愈卡片1 -->
                    <div class="bg-gradient-to-br from-purple-50 to-pink-50 p-6 rounded-2xl shadow-sm border border-purple-100 hover:shadow-md transition">
                        <div class="flex items-start gap-3 mb-3">
                            <div class="w-10 h-10 rounded-full bg-purple-100 flex items-center justify-center flex-shrink-0">
                                <i class="fa-solid fa-quote-left text-purple-600"></i>
                            </div>
                            <div class="flex-1">
                                <h3 class="font-bold text-slate-800 mb-2">相信自己</h3>
                                <p class="text-sm text-slate-600 leading-relaxed">
                                    每一次努力都是成长的印记，每一个困难都是蜕变的契机。相信自己，你比想象中更强大。
                                </p>
                            </div>
                        </div>
                        <div class="flex justify-end">
                            <span class="text-xs text-purple-400">— 致正在努力的你</span>
                        </div>
                    </div>

                    <!-- 治愈卡片2 -->
                    <div class="bg-gradient-to-br from-blue-50 to-cyan-50 p-6 rounded-2xl shadow-sm border border-blue-100 hover:shadow-md transition">
                        <div class="flex items-start gap-3 mb-3">
                            <div class="w-10 h-10 rounded-full bg-blue-100 flex items-center justify-center flex-shrink-0">
                                <i class="fa-solid fa-sun text-blue-600"></i>
                            </div>
                            <div class="flex-1">
                                <h3 class="font-bold text-slate-800 mb-2">阳光总在风雨后</h3>
                                <p class="text-sm text-slate-600 leading-relaxed">
                                    人生没有白走的路，每一步都算数。黑夜无论多长，白昼总会到来。
                                </p>
                            </div>
                        </div>
                        <div class="flex justify-end">
                            <span class="text-xs text-blue-400">— 给迷茫中的你</span>
                        </div>
                    </div>

                    <!-- 治愈卡片3 -->
                    <div class="bg-gradient-to-br from-teal-50 to-emerald-50 p-6 rounded-2xl shadow-sm border border-teal-100 hover:shadow-md transition">
                        <div class="flex items-start gap-3 mb-3">
                            <div class="w-10 h-10 rounded-full bg-teal-100 flex items-center justify-center flex-shrink-0">
                                <i class="fa-solid fa-leaf text-teal-600"></i>
                            </div>
                            <div class="flex-1">
                                <h3 class="font-bold text-slate-800 mb-2">慢慢来比较快</h3>
                                <p class="text-sm text-slate-600 leading-relaxed">
                                    不必着急，按照自己的节奏前进。生活不是赛跑，而是一场旅行，享受沿途的风景。
                                </p>
                            </div>
                        </div>
                        <div class="flex justify-end">
                            <span class="text-xs text-teal-400">— 送给焦虑的你</span>
                        </div>
                    </div>

                    <!-- 治愈卡片4 -->
                    <div class="bg-gradient-to-br from-amber-50 to-orange-50 p-6 rounded-2xl shadow-sm border border-amber-100 hover:shadow-md transition">
                        <div class="flex items-start gap-3 mb-3">
                            <div class="w-10 h-10 rounded-full bg-amber-100 flex items-center justify-center flex-shrink-0">
                                <i class="fa-solid fa-star text-amber-600"></i>
                            </div>
                            <div class="flex-1">
                                <h3 class="font-bold text-slate-800 mb-2">你值得被爱</h3>
                                <p class="text-sm text-slate-600 leading-relaxed">
                                    请温柔对待自己，你已经足够好了。学会爱自己，是一生的必修课。
                                </p>
                            </div>
                        </div>
                        <div class="flex justify-end">
                            <span class="text-xs text-amber-400">— 写给独特的你</span>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 公告栏 -->
            <div>
                <h2 class="text-xl font-bold text-slate-800 mb-6">中心公告</h2>
                <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
                    <ul class="space-y-4" v-if="announcements.length > 0">
                        <li v-for="announcement in announcements" :key="announcement.id" class="pb-4 border-b border-gray-50 last:border-0 last:pb-0">
                            <button @click="openAnnouncement(announcement)" class="block group text-left w-full">
                                <span class="text-xs text-gray-400 mb-1 block">{{ formatDate(announcement.date) }}</span>
                                <h4 class="text-sm font-medium text-slate-700 group-hover:text-teal-600 transition line-clamp-2">{{ announcement.title }}</h4>
                            </button>
                        </li>
                    </ul>
                    <div v-else class="text-center text-gray-400 py-4">
                        暂无公告
                    </div>
                </div>
            </div>
        </div>

    </main>

    <!-- 公告详情弹窗 -->
    <div v-if="showAnnouncementModal" class="fixed inset-0 z-50 flex items-center justify-center">
      <!-- 遮罩层 -->
      <div class="absolute inset-0 bg-black/50" @click="closeAnnouncement"></div>
      
      <!-- 弹窗内容 -->
      <div class="relative bg-white rounded-2xl shadow-xl w-full max-w-lg mx-4 max-h-[80vh] overflow-hidden">
        <!-- 头部 -->
        <div class="bg-gradient-to-r from-teal-500 to-emerald-600 px-6 py-4">
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 bg-white/20 rounded-lg flex items-center justify-center">
                <i class="fa-solid fa-bullhorn text-white"></i>
              </div>
              <span class="text-white font-semibold">公告详情</span>
            </div>
            <button @click="closeAnnouncement" class="text-white/80 hover:text-white transition">
              <i class="fa-solid fa-xmark text-xl"></i>
            </button>
          </div>
        </div>
        
        <!-- 内容区 -->
        <div class="p-6 overflow-y-auto max-h-[60vh]">
          <div v-if="selectedAnnouncement">
            <!-- 日期 -->
            <div class="flex items-center gap-2 text-gray-400 text-sm mb-3">
              <i class="fa-regular fa-calendar"></i>
              <span>{{ formatDate(selectedAnnouncement.date) }}</span>
            </div>
            
            <!-- 标题 -->
            <h2 class="text-xl font-bold text-slate-800 mb-4 leading-relaxed">
              {{ selectedAnnouncement.title }}
            </h2>
            
            <!-- 分割线 -->
            <div class="border-t border-gray-100 my-4"></div>
            
            <!-- 正文内容 -->
            <div class="text-gray-600 leading-relaxed whitespace-pre-wrap">
              {{ selectedAnnouncement.content || '暂无详细内容' }}
            </div>
          </div>
        </div>
        
        <!-- 底部按钮 -->
        <div class="px-6 py-4 bg-gray-50 border-t border-gray-100">
          <button 
            @click="closeAnnouncement" 
            class="w-full py-2.5 bg-teal-500 text-white rounded-lg font-medium hover:bg-teal-600 transition">
            我知道了
          </button>
        </div>
      </div>
    </div>

    <!-- 页脚 -->
    <footer class="bg-white border-t border-gray-200 mt-12 py-8">
        <div class="max-w-7xl mx-auto px-4 text-center text-gray-400 text-sm">
            <p>&copy; 2025 校园心理咨询中心. All rights reserved.</p>
        </div>
    </footer>
  </div>
</template>
