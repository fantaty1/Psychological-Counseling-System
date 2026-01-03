<script setup>
import { ref, onMounted, onUnmounted, nextTick, computed, watch } from 'vue'
import { RouterLink, useRouter, useRoute } from 'vue-router'
import api, { getAvatarUrl } from '@/api'

const router = useRouter()
const route = useRoute()

// 会话列表
const sessions = ref([])
const currentSessionId = ref('')

// 当前会话的医生信息
const currentDoctor = ref(null)

// 消息列表
const messages = ref([])

// 输入框
const inputMessage = ref('')
const isLoading = ref(false)
const sessionsLoading = ref(true)

// 轮询定时器
let pollInterval = null

// 当前学生信息
const studentInfo = ref({
  name: '',
  avatar: ''
})

// 加载学生信息
const loadStudentInfo = async () => {
  try {
    const response = await api.get('/api/user/profile')
    if (response.data.success) {
      studentInfo.value = response.data.body
    }
  } catch (error) {
    console.error('加载学生信息失败:', error)
  }
}

// 加载会话列表
const loadSessions = async () => {
  try {
    sessionsLoading.value = true
    const response = await api.get('/api/user/chat/sessions')
    if (response.data.success) {
      sessions.value = response.data.body || []
    }
  } catch (error) {
    console.error('加载会话失败:', error)
  } finally {
    sessionsLoading.value = false
  }
}

// 选择会话
const selectSession = async (sessionId) => {
  currentSessionId.value = sessionId
  const session = sessions.value.find(s => s.sessionId === sessionId)
  if (session) {
    currentDoctor.value = {
      id: session.doctorId,
      name: session.doctorName,
      avatar: session.doctorAvatar,
      title: session.doctorTitle
    }
  }
  await loadMessages(sessionId)
  
  // 清除旧的轮询
  if (pollInterval) {
    clearInterval(pollInterval)
  }
  
  // 开始轮询新消息（每3秒检查一次）
  pollInterval = setInterval(() => {
    if (currentSessionId.value === sessionId) {
      loadMessages(sessionId, true)
    }
  }, 3000)
}

// 加载消息记录
const loadMessages = async (sessionId, shouldScroll = true) => {
  try {
    const response = await api.get(`/api/user/chat/sessions/${sessionId}/messages`)
    if (response.data.success) {
      const data = response.data.body
      const oldLength = messages.value.length
      messages.value = data.messages || []
      // 更新医生信息
      if (data.doctorId) {
        currentDoctor.value = {
          id: data.doctorId,
          name: data.doctorName,
          avatar: data.doctorAvatar,
          title: data.doctorTitle
        }
      }
      // 只有新消息才滚动
      if (shouldScroll && messages.value.length > oldLength) {
        await nextTick()
        scrollToBottom()
      }
    }
  } catch (error) {
    console.error('加载消息失败:', error)
  }
}

// 发送消息
const sendMessage = async () => {
  if (!inputMessage.value.trim() || isLoading.value || !currentSessionId.value) return
  
  const content = inputMessage.value.trim()
  inputMessage.value = ''
  isLoading.value = true
  
  // 先显示发送的消息
  messages.value.push({
    id: Date.now(),
    role: 'student',
    content: content,
    createdAt: new Date().toISOString()
  })
  await nextTick()
  scrollToBottom()
  
  try {
    await api.post('/api/user/chat/send', {
      sessionId: currentSessionId.value,
      content: content
    })
    await loadMessages(currentSessionId.value)
    await loadSessions()
  } catch (error) {
    console.error('发送消息失败:', error)
  } finally {
    isLoading.value = false
  }
}

// 滚动到底部
const scrollToBottom = () => {
  const chatContainer = document.querySelector('.chat-messages')
  if (chatContainer) {
    chatContainer.scrollTop = chatContainer.scrollHeight
  }
}

// 格式化时间
const formatTime = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  return date.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
}

// 格式化消息时间
const formatMessageTime = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

// 检查路由参数
const checkRouteParams = async () => {
  const doctorId = route.query.doctorId
  if (doctorId) {
    // 创建或获取与该医生的会话
    try {
      const response = await api.post('/api/user/chat/sessions', {
        doctorId: parseInt(doctorId)
      })
      if (response.data.success) {
        const sessionId = response.data.body.sessionId
        await loadSessions()
        await selectSession(sessionId)
      }
    } catch (error) {
      console.error('创建会话失败:', error)
    }
  } else if (route.params.sessionId) {
    await selectSession(route.params.sessionId)
  }
}

onMounted(async () => {
  await loadStudentInfo()
  await loadSessions()
  await checkRouteParams()
  
  // 如果没有指定会话且有会话列表，选择第一个
  if (!currentSessionId.value && sessions.value.length > 0) {
    await selectSession(sessions.value[0].sessionId)
  }
})

// 组件卸载时清除定时器
onUnmounted(() => {
  if (pollInterval) {
    clearInterval(pollInterval)
  }
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
            <h1 class="font-bold text-xl text-slate-800">咨询师消息</h1>
          </div>
          <div class="flex items-center gap-4">
            <RouterLink to="/doctors" class="text-teal-600 hover:text-teal-700 font-medium">
              <i class="fa-solid fa-user-doctor mr-1"></i> 浏览咨询师
            </RouterLink>
          </div>
        </div>
      </div>
    </nav>

    <!-- 主内容区 -->
    <main class="flex-1 flex max-w-7xl mx-auto w-full">
      
      <!-- 左侧会话列表 -->
      <div class="w-80 bg-white border-r border-gray-100 flex flex-col">
        <div class="p-4 border-b border-gray-100">
          <h2 class="font-bold text-slate-800">消息列表</h2>
        </div>
        
        <!-- 会话列表 -->
        <div class="flex-1 overflow-y-auto">
          <div v-if="sessionsLoading" class="flex justify-center py-8">
            <i class="fa-solid fa-spinner fa-spin text-2xl text-teal-500"></i>
          </div>
          
          <div v-else-if="sessions.length === 0" class="text-center py-8 text-gray-400">
            <i class="fa-regular fa-comments text-4xl mb-2"></i>
            <p class="text-sm">暂无消息</p>
            <RouterLink to="/doctors" class="text-teal-600 text-sm hover:underline mt-2 block">
              去咨询师列表发起咨询
            </RouterLink>
          </div>
          
          <div v-else>
            <div 
              v-for="session in sessions" 
              :key="session.sessionId"
              @click="selectSession(session.sessionId)"
              :class="[
                'p-4 border-b border-gray-50 cursor-pointer transition hover:bg-gray-50',
                currentSessionId === session.sessionId && 'bg-teal-50 border-l-4 border-l-teal-500'
              ]">
              <div class="flex gap-3">
                <img 
                  :src="getAvatarUrl(session.doctorAvatar) || 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2'" 
                  :alt="session.doctorName"
                  class="w-12 h-12 rounded-full object-cover">
                <div class="flex-1 min-w-0">
                  <div class="flex justify-between items-center mb-1">
                    <h3 class="font-medium text-slate-800 truncate">{{ session.doctorName }}</h3>
                    <span class="text-xs text-gray-400">{{ formatTime(session.updatedAt) }}</span>
                  </div>
                  <p class="text-sm text-gray-500 truncate">{{ session.lastMessage || '暂无消息' }}</p>
                </div>
                <div v-if="session.unreadCount > 0" 
                     class="w-5 h-5 bg-red-500 text-white text-xs rounded-full flex items-center justify-center">
                  {{ session.unreadCount > 9 ? '9+' : session.unreadCount }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧聊天区域 -->
      <div class="flex-1 flex flex-col bg-gray-50">
        
        <!-- 未选择会话 -->
        <div v-if="!currentSessionId" class="flex-1 flex items-center justify-center text-gray-400">
          <div class="text-center">
            <i class="fa-regular fa-comment-dots text-6xl mb-4"></i>
            <p>选择一个会话开始聊天</p>
          </div>
        </div>

        <!-- 聊天界面 -->
        <template v-else>
          <!-- 医生信息头部 -->
          <div class="bg-white border-b border-gray-100 px-6 py-4 flex items-center gap-4">
            <img 
              :src="getAvatarUrl(currentDoctor?.avatar) || 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2'" 
              :alt="currentDoctor?.name"
              class="w-12 h-12 rounded-full object-cover border-2 border-teal-100">
            <div>
              <h3 class="font-bold text-slate-800">{{ currentDoctor?.name }}</h3>
              <p class="text-sm text-gray-500">{{ currentDoctor?.title || '心理咨询师' }}</p>
            </div>
            <RouterLink 
              v-if="currentDoctor?.id"
              :to="`/doctor/${currentDoctor.id}`" 
              class="ml-auto text-teal-600 hover:text-teal-700 text-sm">
              查看主页 <i class="fa-solid fa-arrow-right ml-1"></i>
            </RouterLink>
          </div>

          <!-- 消息区域 -->
          <div class="flex-1 overflow-y-auto p-6 space-y-4 chat-messages">
            <div 
              v-for="msg in messages" 
              :key="msg.id"
              :class="['flex gap-3', msg.role === 'student' ? 'flex-row-reverse' : '']">
              
              <!-- 头像 -->
              <img 
                :src="msg.role === 'student' 
                  ? (getAvatarUrl(studentInfo.avatar) || 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100') 
                  : (getAvatarUrl(currentDoctor?.avatar) || 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2')"
                class="w-10 h-10 rounded-full object-cover flex-shrink-0">
              
              <!-- 消息气泡 -->
              <div :class="['max-w-[60%]', msg.role === 'student' ? 'text-right' : '']">
                <div :class="[
                  'inline-block px-4 py-3 rounded-2xl',
                  msg.role === 'student' 
                    ? 'bg-teal-500 text-white rounded-tr-sm' 
                    : 'bg-white text-slate-800 shadow-sm rounded-tl-sm'
                ]">
                  <p class="whitespace-pre-wrap text-sm">{{ msg.content }}</p>
                </div>
                <p class="text-xs text-gray-400 mt-1">{{ formatMessageTime(msg.createdAt) }}</p>
              </div>
            </div>
            
            <!-- 空消息提示 -->
            <div v-if="messages.length === 0" class="text-center text-gray-400 py-8">
              <i class="fa-regular fa-comment-dots text-4xl mb-2"></i>
              <p>开始与咨询师的对话吧</p>
            </div>
          </div>

          <!-- 输入区域 -->
          <div class="bg-white border-t border-gray-100 p-4">
            <div class="flex gap-3">
              <input 
                v-model="inputMessage"
                @keyup.enter="sendMessage"
                type="text" 
                placeholder="输入消息..."
                class="flex-1 bg-gray-50 border border-gray-200 rounded-xl px-4 py-3 outline-none focus:border-teal-500 focus:ring-1 focus:ring-teal-500 transition"
                :disabled="isLoading">
              <button 
                @click="sendMessage"
                :disabled="!inputMessage.trim() || isLoading"
                class="bg-teal-500 text-white px-6 py-3 rounded-xl font-medium hover:bg-teal-600 transition disabled:opacity-50 disabled:cursor-not-allowed">
                <i v-if="isLoading" class="fa-solid fa-spinner fa-spin"></i>
                <span v-else>发送</span>
              </button>
            </div>
          </div>
        </template>
      </div>
    </main>
  </div>
</template>
