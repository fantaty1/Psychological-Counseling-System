<script setup>
import { ref, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { RouterLink, useRouter, useRoute } from 'vue-router'
import api, { getAvatarUrl } from '@/api'

const router = useRouter()
const route = useRoute()

// 医生信息
const doctorInfo = ref({
  name: '',
  avatar: ''
})

// 会话列表
const sessions = ref([])
const currentSessionId = ref('')

// 当前会话的学生信息
const currentStudent = ref(null)

// 消息列表
const messages = ref([])

// 输入框
const inputMessage = ref('')
const isLoading = ref(false)

// 轮询定时器
let pollInterval = null

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

// 加载会话列表
const loadSessions = async () => {
  try {
    const response = await api.get('/api/doctor/chat/sessions')
    if (response.data.success) {
      sessions.value = response.data.body || []
    }
  } catch (error) {
    console.error('加载会话失败:', error)
  }
}

// 选择会话
const selectSession = async (sessionId) => {
  currentSessionId.value = sessionId
  const session = sessions.value.find(s => s.sessionId === sessionId)
  if (session) {
    currentStudent.value = {
      id: session.studentId,
      name: session.studentName,
      avatar: session.studentAvatar,
      college: session.studentCollege
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
    const response = await api.get(`/api/doctor/chat/sessions/${sessionId}/messages`)
    if (response.data.success) {
      const oldLength = messages.value.length
      messages.value = response.data.body.messages || []
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
    role: 'doctor',
    content: content,
    createdAt: new Date().toISOString()
  })
  await nextTick()
  scrollToBottom()
  
  try {
    await api.post('/api/doctor/chat/send', {
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

// 退出登录
const handleLogout = () => {
  removeToken()
  removeUser()
  localStorage.removeItem('role')
  router.push('/login')
}

// 快捷回复
const quickReplies = [
  '我理解你的感受',
  '可以详细说说吗？',
  '这是很正常的反应',
  '你做得很好'
]

const sendQuickReply = (text) => {
  inputMessage.value = text
  sendMessage()
}

// 监听路由参数变化
watch(() => route.params.sessionId, (newSessionId) => {
  if (newSessionId) {
    selectSession(newSessionId)
  }
}, { immediate: true })

onMounted(async () => {
  await loadDoctorInfo()
  await loadSessions()
  
  // 如果有路由参数,选择对应会话
  if (route.params.sessionId) {
    await selectSession(route.params.sessionId)
  } else if (sessions.value.length > 0) {
    await selectSession(sessions.value[0].sessionId)
  }
})

// 组件卸载时清除轮询
onUnmounted(() => {
  if (pollInterval) {
    clearInterval(pollInterval)
  }
})
</script>

<template>
  <div class="min-h-screen bg-gray-50 flex flex-col">
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
              <RouterLink to="/doctor/home" class="text-gray-500 hover:text-indigo-600 hover:border-indigo-300 border-b-2 border-transparent px-1 pt-1 text-sm font-medium h-full flex items-center transition">工作台</RouterLink>
              <RouterLink to="/doctor/appointments" class="text-gray-500 hover:text-indigo-600 hover:border-indigo-300 border-b-2 border-transparent px-1 pt-1 text-sm font-medium h-full flex items-center transition">预约管理</RouterLink>
              <RouterLink to="/doctor/chat" class="text-indigo-600 border-b-2 border-indigo-600 px-1 pt-1 text-sm font-medium h-full flex items-center">咨询对话</RouterLink>
              <RouterLink to="/doctor/schedule" class="text-gray-500 hover:text-indigo-600 hover:border-indigo-300 border-b-2 border-transparent px-1 pt-1 text-sm font-medium h-full flex items-center transition">排班设置</RouterLink>
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

    <!-- 聊天主体 -->
    <div class="flex-1 flex overflow-hidden">
      
      <!-- 左侧会话列表 -->
      <aside class="w-80 bg-white border-r border-gray-100 flex flex-col flex-shrink-0">
        <div class="p-4 border-b border-gray-100">
          <h2 class="font-bold text-lg text-slate-800">咨询对话</h2>
          <p class="text-sm text-gray-500 mt-1">共 {{ sessions.length }} 个会话</p>
        </div>
        
        <div class="flex-1 overflow-y-auto">
          <div v-if="sessions.length === 0" class="p-8 text-center">
            <i class="fa-regular fa-comments text-4xl text-gray-300 mb-3"></i>
            <p class="text-gray-400">暂无咨询会话</p>
          </div>
          
          <div v-for="session in sessions" :key="session.sessionId"
            @click="selectSession(session.sessionId)"
            :class="[
              'p-4 cursor-pointer transition border-l-4',
              currentSessionId === session.sessionId 
                ? 'bg-indigo-50 border-l-indigo-600' 
                : 'hover:bg-gray-50 border-l-transparent'
            ]">
            <div class="flex items-start gap-3">
              <div class="relative">
                <img 
                  v-if="session.studentAvatar"
                  :src="getAvatarUrl(session.studentAvatar)"
                  :alt="session.studentName"
                  class="w-12 h-12 rounded-full object-cover">
                <div v-else class="w-12 h-12 rounded-full bg-gradient-to-br from-teal-400 to-cyan-500 flex items-center justify-center text-white font-bold">
                  {{ session.studentName?.charAt(0) || 'S' }}
                </div>
                <span v-if="session.unreadCount > 0" class="absolute -top-1 -right-1 w-5 h-5 bg-rose-500 text-white text-xs rounded-full flex items-center justify-center">
                  {{ session.unreadCount }}
                </span>
              </div>
              <div class="flex-1 min-w-0">
                <div class="flex items-center justify-between mb-1">
                  <h3 class="font-medium text-slate-800 truncate">{{ session.studentName }}</h3>
                  <span class="text-xs text-gray-400">{{ formatTime(session.updatedAt) }}</span>
                </div>
                <p class="text-sm text-gray-500 truncate">{{ session.lastMessage || '暂无消息' }}</p>
              </div>
            </div>
          </div>
        </div>
      </aside>

      <!-- 右侧聊天区域 -->
      <main class="flex-1 flex flex-col min-w-0 bg-gray-50">
        
        <!-- 选中会话时显示聊天界面 -->
        <template v-if="currentSessionId && currentStudent">
          
          <!-- 学生信息栏 -->
          <header class="h-16 bg-white border-b border-gray-100 flex items-center justify-between px-6 flex-shrink-0">
            <div class="flex items-center gap-3">
              <img 
                v-if="currentStudent.avatar"
                :src="getAvatarUrl(currentStudent.avatar)"
                :alt="currentStudent.name"
                class="w-10 h-10 rounded-full object-cover">
              <div v-else class="w-10 h-10 rounded-full bg-gradient-to-br from-teal-400 to-cyan-500 flex items-center justify-center text-white font-bold">
                {{ currentStudent.name?.charAt(0) || 'S' }}
              </div>
              <div>
                <h2 class="font-bold text-slate-800">{{ currentStudent.name }}</h2>
                <p class="text-sm text-gray-500">{{ currentStudent.college }}</p>
              </div>
            </div>
            <div class="flex items-center gap-2">
              <button class="p-2 text-gray-400 hover:text-indigo-600 hover:bg-indigo-50 rounded-lg transition" title="查看学生资料">
                <i class="fa-solid fa-user"></i>
              </button>
              <button class="p-2 text-gray-400 hover:text-indigo-600 hover:bg-indigo-50 rounded-lg transition" title="结束咨询">
                <i class="fa-solid fa-flag-checkered"></i>
              </button>
            </div>
          </header>

          <!-- 消息区域 -->
          <div class="flex-1 overflow-y-auto p-6 chat-messages">
            <div class="max-w-3xl mx-auto space-y-4">
              
              <!-- 提示信息 -->
              <div class="flex justify-center mb-6">
                <div class="bg-white/80 text-slate-600 px-4 py-2 rounded-full text-sm flex items-center gap-2 border border-gray-100 shadow-sm">
                  <i class="fa-solid fa-shield-halved text-indigo-500"></i>
                  本次咨询内容将被保密处理
                </div>
              </div>

              <!-- 消息列表 -->
              <template v-for="(message, index) in messages" :key="message.id">
                
                <!-- 时间分隔 -->
                <div v-if="index === 0 || (index > 0 && new Date(message.createdAt) - new Date(messages[index-1].createdAt) > 300000)" 
                  class="flex justify-center my-4">
                  <span class="text-xs text-gray-400 bg-gray-100 px-3 py-1 rounded-full">{{ formatTime(message.createdAt) }}</span>
                </div>

                <!-- 学生消息 -->
                <div v-if="message.role === 'user' || message.role === 'student'" class="flex gap-3 max-w-2xl">
                  <img 
                    v-if="currentStudent.avatar"
                    :src="getAvatarUrl(currentStudent.avatar)"
                    :alt="currentStudent.name"
                    class="w-10 h-10 rounded-full object-cover flex-shrink-0">
                  <div v-else class="w-10 h-10 rounded-full bg-gradient-to-br from-teal-400 to-cyan-500 flex items-center justify-center text-white font-bold flex-shrink-0">
                    {{ currentStudent.name?.charAt(0) || 'S' }}
                  </div>
                  <div class="flex-1">
                    <div class="flex items-center gap-2 mb-1">
                      <span class="text-sm font-medium text-slate-700">{{ currentStudent.name }}</span>
                      <span class="text-xs text-gray-400">{{ formatMessageTime(message.createdAt) }}</span>
                    </div>
                    <div class="inline-block p-4 rounded-2xl rounded-tl-md bg-white border border-gray-100 shadow-sm max-w-[85%]">
                      <p class="text-slate-700 leading-relaxed whitespace-pre-wrap">{{ message.content }}</p>
                    </div>
                  </div>
                </div>

                <!-- 医生消息 -->
                <div v-else class="flex gap-3 justify-end max-w-2xl ml-auto">
                  <div class="flex-1 flex flex-col items-end">
                    <div class="flex items-center gap-2 mb-1">
                      <span class="text-xs text-gray-400">{{ formatMessageTime(message.createdAt) }}</span>
                      <span class="text-sm font-medium text-slate-700">{{ doctorInfo.name }}</span>
                    </div>
                    <div class="inline-block p-4 rounded-2xl rounded-tr-md bg-gradient-to-r from-indigo-500 to-purple-500 text-white shadow-md max-w-[85%]">
                      <p class="leading-relaxed whitespace-pre-wrap">{{ message.content }}</p>
                    </div>
                  </div>
                  <img 
                    v-if="doctorInfo.avatar"
                    :src="getAvatarUrl(doctorInfo.avatar)"
                    :alt="doctorInfo.name"
                    class="w-10 h-10 rounded-full object-cover flex-shrink-0">
                  <div v-else class="w-10 h-10 rounded-full bg-gradient-to-br from-indigo-400 to-purple-500 flex items-center justify-center text-white font-bold flex-shrink-0">
                    {{ doctorInfo.name?.charAt(0) || 'D' }}
                  </div>
                </div>

              </template>

              <!-- 空消息提示 -->
              <div v-if="messages.length === 0" class="text-center py-12">
                <i class="fa-regular fa-comments text-4xl text-gray-300 mb-3"></i>
                <p class="text-gray-400">暂无消息记录</p>
                <p class="text-sm text-gray-400 mt-1">开始与学生对话吧</p>
              </div>

            </div>
          </div>

          <!-- 输入区域 -->
          <footer class="bg-white border-t border-gray-100 p-4 flex-shrink-0">
            <div class="max-w-3xl mx-auto">
              
              <!-- 快捷回复 -->
              <div class="flex gap-2 mb-3 overflow-x-auto pb-2">
                <button 
                  v-for="reply in quickReplies" 
                  :key="reply"
                  @click="sendQuickReply(reply)"
                  class="px-3 py-1.5 bg-indigo-50 text-indigo-600 text-xs rounded-full hover:bg-indigo-100 transition whitespace-nowrap flex-shrink-0">
                  {{ reply }}
                </button>
              </div>

              <div class="flex items-end gap-3">
                <div class="flex-1">
                  <textarea 
                    v-model="inputMessage"
                    @keydown.enter.exact.prevent="sendMessage"
                    :disabled="isLoading"
                    placeholder="输入回复内容..." 
                    rows="1"
                    class="w-full bg-gray-50 border border-gray-200 rounded-2xl py-3 px-5 text-slate-700 outline-none focus:border-indigo-400 focus:ring-2 focus:ring-indigo-100 transition resize-none min-h-[48px] max-h-32 disabled:opacity-50"></textarea>
                </div>
                <button 
                  @click="sendMessage"
                  :disabled="isLoading || !inputMessage.trim()"
                  class="w-12 h-12 bg-gradient-to-r from-indigo-500 to-purple-500 text-white rounded-xl shadow-lg hover:shadow-xl hover:scale-105 transition-all duration-200 flex items-center justify-center disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:scale-100">
                  <i class="fa-solid fa-paper-plane"></i>
                </button>
              </div>
            </div>
          </footer>

        </template>

        <!-- 未选择会话时显示空状态 -->
        <div v-else class="flex-1 flex items-center justify-center">
          <div class="text-center">
            <div class="w-24 h-24 bg-indigo-100 rounded-full flex items-center justify-center mx-auto mb-6">
              <i class="fa-solid fa-comments text-4xl text-indigo-400"></i>
            </div>
            <h3 class="text-xl font-bold text-slate-800 mb-2">选择一个会话开始咨询</h3>
            <p class="text-gray-500">从左侧列表中选择学生会话</p>
          </div>
        </div>

      </main>
    </div>
  </div>
</template>

<style scoped>
.chat-messages::-webkit-scrollbar {
  width: 6px;
}
.chat-messages::-webkit-scrollbar-track {
  background: transparent;
}
.chat-messages::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 3px;
}
</style>
