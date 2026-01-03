<script setup>
import { ref, onMounted, nextTick, computed } from 'vue'
import api, { getAvatarUrl } from '@/api'

// 心语医生信息
const counselor = {
  name: '林心语',
  title: '心理咨询师',
  avatar: 'https://cdn-icons-png.flaticon.com/512/3304/3304567.png',
  description: '国家二级心理咨询师 · 8年咨询经验',
  tags: ['焦虑调节', '情绪管理', '人际关系', '学业压力'],
  experience: '8年心理咨询经验',
  motto: '"每一次倾诉，都是走向内心平静的一步"'
}

// 会话列表
const sessions = ref([])
const currentSessionId = ref('')

// 消息列表
const messages = ref([])

// 输入框
const inputMessage = ref('')
const isLoading = ref(false)

// 用户信息
const userInfo = ref({
  name: '',
  college: '',
  avatar: ''
})

// 计算用户头像URL
const userAvatarUrl = computed(() => {
  return getAvatarUrl(userInfo.value.avatar) || 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?ixlib=rb-4.0.3&auto=format&fit=crop&w=200&q=80'
})

// 显示医生信息弹窗
const showDoctorInfo = ref(false)

// 快捷问题
const quickQuestions = [
  '最近感觉压力很大',
  '我有些焦虑',
  '睡眠质量不好',
  '想聊聊心事'
]

// 创建新会话
const createNewSession = async () => {
  try {
    const response = await api.post('/api/chat/sessions')
    if (response.data.code === 200) {
      currentSessionId.value = response.data.data.sessionId
      messages.value = []
      await loadSessions()
    }
  } catch (error) {
    console.error('创建会话失败:', error)
  }
}

// 加载会话列表
const loadSessions = async () => {
  try {
    const response = await api.get('/api/chat/sessions')
    if (response.data.code === 200) {
      sessions.value = response.data.data
    }
  } catch (error) {
    console.error('加载会话失败:', error)
  }
}

// 选择会话
const selectSession = async (sessionId) => {
  currentSessionId.value = sessionId
  await loadMessages(sessionId)
}

// 加载消息记录
const loadMessages = async (sessionId) => {
  try {
    const response = await api.get(`/api/chat/sessions/${sessionId}/messages`)
    if (response.data.code === 200) {
      messages.value = response.data.data
      await nextTick()
      scrollToBottom()
    }
  } catch (error) {
    console.error('加载消息失败:', error)
  }
}

// 发送消息
const sendMessage = async () => {
  if (!inputMessage.value.trim() || isLoading.value) return
  
  if (!currentSessionId.value) {
    await createNewSession()
  }
  
  const content = inputMessage.value.trim()
  inputMessage.value = ''
  isLoading.value = true
  
  // 先显示用户消息
  messages.value.push({
    id: Date.now(),
    role: 'user',
    content: content,
    createdAt: new Date().toISOString()
  })
  await nextTick()
  scrollToBottom()
  
  try {
    const response = await api.post('/api/chat/send', {
      sessionId: currentSessionId.value,
      content: content,
      type: 'text'
    })
    
    if (response.data.code === 200) {
      await loadMessages(currentSessionId.value)
      await loadSessions()
    }
  } catch (error) {
    console.error('发送消息失败:', error)
    messages.value.push({
      id: Date.now() + 1,
      role: 'assistant',
      content: '抱歉，消息发送失败了，请稍后再试。',
      createdAt: new Date().toISOString()
    })
  } finally {
    isLoading.value = false
    await nextTick()
    scrollToBottom()
  }
}

// 发送快捷问题
const sendQuickQuestion = (question) => {
  inputMessage.value = question
  sendMessage()
}

// 滚动到底部
const scrollToBottom = () => {
  const chatContainer = document.querySelector('.chat-messages')
  if (chatContainer) {
    chatContainer.scrollTop = chatContainer.scrollHeight
  }
}

// 加载用户信息
const loadUserInfo = async () => {
  try {
    const response = await api.get('/api/user/profile')
    console.log('AI聊天页用户信息响应:', response.data)
    if (response.data.success) {
      userInfo.value = response.data.body
      console.log('用户信息已加载:', userInfo.value)
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
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
  if (diff < 172800000) return '昨天 ' + date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  return date.toLocaleDateString('zh-CN', { month: 'long', day: 'numeric' })
}

// 格式化消息时间
const formatMessageTime = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

// 页面加载时
onMounted(async () => {
  await loadUserInfo()
  await loadSessions()
  if (sessions.value.length > 0) {
    await selectSession(sessions.value[0].sessionId)
  }
})
</script>

<template>
  <div class="bg-gradient-to-br from-teal-50 via-white to-purple-50 text-slate-800 h-screen flex overflow-hidden">
    
    <!-- 左侧侧边栏 -->
    <aside class="w-80 bg-white/80 backdrop-blur-sm border-r border-gray-100 flex flex-col flex-shrink-0 shadow-sm">
      
      <!-- 咨询师信息卡片 -->
      <div class="p-5 border-b border-gray-100 bg-gradient-to-r from-teal-500 to-cyan-500">
        <div class="flex items-center gap-4">
          <div class="relative">
            <img :src="counselor.avatar" :alt="counselor.name" class="w-16 h-16 rounded-full border-3 border-white shadow-lg object-cover bg-white">
            <span class="absolute bottom-0 right-0 w-4 h-4 bg-green-400 border-2 border-white rounded-full"></span>
          </div>
          <div class="text-white">
            <h2 class="font-bold text-lg">{{ counselor.name }}</h2>
            <p class="text-white/80 text-sm">{{ counselor.title }}</p>
            <div class="flex items-center gap-1 mt-1">
              <i class="fa-solid fa-circle text-green-300 text-[8px]"></i>
              <span class="text-xs text-white/70">在线咨询中</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 历史会话标题 -->
      <div class="p-4 flex items-center justify-between">
        <h3 class="font-semibold text-slate-700 text-sm">咨询记录</h3>
        <button @click="createNewSession" class="w-8 h-8 rounded-lg hover:bg-teal-50 text-teal-600 flex items-center justify-center transition" title="开始新咨询">
          <i class="fa-solid fa-plus"></i>
        </button>
      </div>
      
      <!-- 会话列表 -->
      <div class="flex-1 overflow-y-auto px-3 space-y-2 hide-scrollbar">
        <div 
          v-for="session in sessions" 
          :key="session.sessionId"
          @click="selectSession(session.sessionId)"
          :class="[
            'p-3 rounded-xl cursor-pointer transition-all duration-200',
            currentSessionId === session.sessionId 
              ? 'bg-gradient-to-r from-teal-50 to-cyan-50 border border-teal-200 shadow-sm' 
              : 'hover:bg-gray-50 border border-transparent'
          ]">
          <div class="flex justify-between items-start mb-1">
            <h4 :class="[
              'text-sm font-medium line-clamp-1',
              currentSessionId === session.sessionId ? 'text-teal-700' : 'text-slate-600'
            ]">{{ session.title || '新咨询' }}</h4>
            <span class="text-[10px] text-gray-400 whitespace-nowrap ml-2">{{ formatTime(session.updatedAt) }}</span>
          </div>
          <p class="text-xs text-gray-500 line-clamp-1">{{ session.lastMessage || '点击开始咨询' }}</p>
        </div>
        
        <div v-if="sessions.length === 0" class="text-center py-10">
          <i class="fa-regular fa-comments text-4xl text-gray-300 mb-3"></i>
          <p class="text-sm text-gray-400">暂无咨询记录</p>
          <button @click="createNewSession" class="mt-3 px-4 py-2 bg-teal-500 text-white text-sm rounded-lg hover:bg-teal-600 transition">
            开始首次咨询
          </button>
        </div>
      </div>
      
      <!-- 底部用户信息 -->
      <div class="p-4 border-t border-gray-100 bg-gray-50/50">
        <div class="flex items-center gap-3">
          <img :src="userAvatarUrl" :alt="userInfo.name" class="w-10 h-10 rounded-full object-cover border-2 border-teal-200 shadow">
          <div class="flex-1 min-w-0">
            <h4 class="text-sm font-semibold text-slate-700 truncate">{{ userInfo.name || '用户' }}</h4>
            <p class="text-xs text-gray-500 truncate">{{ userInfo.college || '点击完善资料' }}</p>
          </div>
        </div>
      </div>
    </aside>

    <!-- 右侧主聊天区域 -->
    <main class="flex-1 flex flex-col min-w-0">
      
      <!-- 顶部咨询师信息栏 -->
      <header class="h-20 bg-white/90 backdrop-blur-sm border-b border-gray-100 flex items-center justify-between px-6 flex-shrink-0 shadow-sm">
        <div class="flex items-center gap-4">
          <div class="relative cursor-pointer" @click="showDoctorInfo = true">
            <img :src="counselor.avatar" :alt="counselor.name" class="w-12 h-12 rounded-full border-2 border-teal-200 shadow object-cover bg-white">
            <span class="absolute bottom-0 right-0 w-3 h-3 bg-green-400 border-2 border-white rounded-full"></span>
          </div>
          <div>
            <div class="flex items-center gap-2">
              <h1 class="font-bold text-lg text-slate-800">{{ counselor.name }}</h1>
              <span class="px-2 py-0.5 bg-teal-100 text-teal-700 text-xs rounded-full">{{ counselor.title }}</span>
            </div>
            <p class="text-sm text-gray-500">{{ counselor.description }}</p>
          </div>
        </div>
        <div class="flex items-center gap-3">
          <button @click="showDoctorInfo = true" class="px-3 py-1.5 text-sm text-teal-600 hover:bg-teal-50 rounded-lg transition flex items-center gap-1">
            <i class="fa-solid fa-user-doctor"></i>
            <span>咨询师简介</span>
          </button>
        </div>
      </header>

      <!-- 聊天记录区域 -->
      <div class="flex-1 overflow-y-auto chat-messages" style="background: linear-gradient(180deg, #f8fafc 0%, #f1f5f9 100%);">
        <div class="max-w-4xl mx-auto p-6 space-y-4">
          
          <!-- 温馨提示 -->
          <div class="flex justify-center mb-6">
            <div class="bg-white/80 backdrop-blur-sm text-slate-600 px-5 py-3 rounded-2xl text-sm flex items-center gap-3 border border-gray-100 shadow-sm">
              <div class="w-8 h-8 rounded-full bg-amber-100 flex items-center justify-center">
                <i class="fa-solid fa-heart text-amber-500"></i>
              </div>
              <div>
                <p class="font-medium text-slate-700">温馨提示</p>
                <p class="text-xs text-gray-500">本咨询仅供参考，如有严重情况请及时就医</p>
              </div>
            </div>
          </div>

          <!-- 空状态 - 欢迎界面 -->
          <div v-if="messages.length === 0" class="py-10">
            <div class="bg-white rounded-3xl shadow-lg p-8 max-w-lg mx-auto border border-gray-100">
              <div class="text-center mb-6">
                <img :src="counselor.avatar" :alt="counselor.name" class="w-24 h-24 rounded-full border-4 border-teal-100 shadow-lg mx-auto mb-4 bg-white">
                <h2 class="text-xl font-bold text-slate-800">{{ counselor.name }}</h2>
                <p class="text-teal-600 text-sm mt-1">{{ counselor.title }} · {{ counselor.experience }}</p>
                <p class="text-gray-500 text-sm mt-2 italic">{{ counselor.motto }}</p>
              </div>
              
              <div class="flex flex-wrap justify-center gap-2 mb-6">
                <span v-for="tag in counselor.tags" :key="tag" class="px-3 py-1 bg-gradient-to-r from-teal-50 to-cyan-50 text-teal-700 text-xs rounded-full border border-teal-100">
                  {{ tag }}
                </span>
              </div>
              
              <div class="bg-gradient-to-r from-teal-50 to-cyan-50 rounded-2xl p-4 mb-6">
                <p class="text-slate-600 text-sm leading-relaxed text-center">
                  你好，我是心语。很高兴你来找我聊聊。<br>
                  这里是一个安全的空间，你可以放心地说出你的感受。<br>
                  无论是什么困扰，我都会认真倾听。
                </p>
              </div>
              
              <p class="text-center text-gray-500 text-sm mb-4">选择一个话题开始，或直接告诉我你的感受</p>
              
              <div class="grid grid-cols-2 gap-3">
                <button 
                  v-for="question in quickQuestions" 
                  :key="question"
                  @click="sendQuickQuestion(question)"
                  class="px-4 py-3 bg-white border border-gray-200 text-slate-600 text-sm rounded-xl hover:border-teal-300 hover:bg-teal-50 hover:text-teal-700 transition-all duration-200 text-left">
                  <i class="fa-regular fa-comment-dots mr-2 text-teal-500"></i>
                  {{ question }}
                </button>
              </div>
            </div>
          </div>

          <!-- 消息列表 -->
          <template v-for="(message, index) in messages" :key="message.id">
            
            <!-- 时间分隔线 -->
            <div v-if="index === 0 || (index > 0 && new Date(message.createdAt) - new Date(messages[index-1].createdAt) > 300000)" class="flex justify-center my-4">
              <span class="text-xs text-gray-400 bg-gray-100 px-3 py-1 rounded-full">{{ formatTime(message.createdAt) }}</span>
            </div>

            <!-- 咨询师消息 -->
            <div v-if="message.role === 'assistant'" class="flex gap-3 max-w-3xl">
              <img :src="counselor.avatar" :alt="counselor.name" class="w-10 h-10 rounded-full border-2 border-white shadow flex-shrink-0 mt-1 bg-white">
              <div class="flex-1">
                <div class="flex items-center gap-2 mb-1">
                  <span class="text-sm font-medium text-slate-700">{{ counselor.name }}</span>
                  <span class="text-xs text-gray-400">{{ formatMessageTime(message.createdAt) }}</span>
                </div>
                <div :class="[
                  'inline-block p-4 rounded-2xl rounded-tl-md shadow-sm max-w-[85%]',
                  message.riskLevel === 'high' 
                    ? 'bg-gradient-to-r from-rose-50 to-pink-50 border-l-4 border-rose-400' 
                    : 'bg-white border border-gray-100'
                ]">
                  <div v-if="message.riskLevel === 'high'" class="flex items-center gap-2 text-rose-600 mb-3 pb-3 border-b border-rose-200">
                    <i class="fa-solid fa-hand-holding-heart"></i>
                    <span class="text-sm font-medium">我很担心你，请让我帮助你</span>
                  </div>
                  <p class="text-slate-700 leading-relaxed whitespace-pre-wrap">{{ message.content }}</p>
                </div>
              </div>
            </div>

            <!-- 用户消息 -->
            <div v-else class="flex gap-3 justify-end max-w-3xl ml-auto">
              <div class="flex-1 flex flex-col items-end">
                <div class="flex items-center gap-2 mb-1">
                  <span class="text-xs text-gray-400">{{ formatMessageTime(message.createdAt) }}</span>
                  <span class="text-sm font-medium text-slate-700">我</span>
                </div>
                <div class="inline-block p-4 rounded-2xl rounded-tr-md bg-gradient-to-r from-teal-500 to-cyan-500 text-white shadow-md max-w-[85%]">
                  <p class="leading-relaxed whitespace-pre-wrap">{{ message.content }}</p>
                </div>
              </div>
              <img :src="userAvatarUrl" :alt="userInfo.name" class="w-10 h-10 rounded-full object-cover border-2 border-teal-200 shadow flex-shrink-0 mt-1">
            </div>

          </template>

          <!-- 加载中动画 -->
          <div v-if="isLoading" class="flex gap-3 max-w-3xl">
            <img :src="counselor.avatar" :alt="counselor.name" class="w-10 h-10 rounded-full border-2 border-white shadow flex-shrink-0 mt-1 bg-white">
            <div class="flex-1">
              <div class="flex items-center gap-2 mb-1">
                <span class="text-sm font-medium text-slate-700">{{ counselor.name }}</span>
                <span class="text-xs text-gray-400">正在输入...</span>
              </div>
              <div class="inline-block p-4 rounded-2xl rounded-tl-md bg-white border border-gray-100 shadow-sm">
                <div class="flex items-center gap-2">
                  <div class="flex gap-1">
                    <span class="w-2 h-2 bg-teal-400 rounded-full animate-bounce" style="animation-delay: 0s"></span>
                    <span class="w-2 h-2 bg-teal-400 rounded-full animate-bounce" style="animation-delay: 0.2s"></span>
                    <span class="w-2 h-2 bg-teal-400 rounded-full animate-bounce" style="animation-delay: 0.4s"></span>
                  </div>
                  <span class="text-sm text-gray-500">心语正在思考回复...</span>
                </div>
              </div>
            </div>
          </div>
          
        </div>
      </div>

      <!-- 底部输入区域 -->
      <footer class="bg-white border-t border-gray-100 p-4 flex-shrink-0 shadow-lg">
        <div class="max-w-4xl mx-auto">
          <!-- 快捷回复 -->
          <div v-if="messages.length > 0" class="flex gap-2 mb-3 overflow-x-auto hide-scrollbar pb-2">
            <button 
              v-for="question in quickQuestions" 
              :key="question"
              @click="sendQuickQuestion(question)"
              class="px-3 py-1.5 bg-gray-100 text-gray-600 text-xs rounded-full hover:bg-teal-100 hover:text-teal-700 transition whitespace-nowrap flex-shrink-0">
              {{ question }}
            </button>
          </div>
          
          <div class="relative flex items-end gap-3">
            <div class="flex-1 relative">
              <textarea 
                v-model="inputMessage"
                @keydown.enter.exact.prevent="sendMessage"
                :disabled="isLoading"
                placeholder="告诉我你的感受，我在认真倾听..." 
                rows="1"
                class="w-full bg-gray-50 border border-gray-200 rounded-2xl py-3 px-5 pr-12 text-slate-700 outline-none focus:border-teal-400 focus:ring-2 focus:ring-teal-100 transition resize-none min-h-[48px] max-h-32 disabled:opacity-50"></textarea>
            </div>
            <button 
              @click="sendMessage"
              :disabled="isLoading || !inputMessage.trim()"
              class="w-12 h-12 bg-gradient-to-r from-teal-500 to-cyan-500 text-white rounded-xl shadow-lg hover:shadow-xl hover:scale-105 transition-all duration-200 flex items-center justify-center disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:scale-100">
              <i class="fa-solid fa-paper-plane"></i>
            </button>
          </div>
          
          <p class="text-center text-xs text-gray-400 mt-3">
            <i class="fa-solid fa-shield-halved mr-1"></i>
            你的隐私受到保护，所有对话内容将被严格保密
          </p>
        </div>
      </footer>
    </main>

    <!-- 咨询师信息弹窗 -->
    <div v-if="showDoctorInfo" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4" @click.self="showDoctorInfo = false">
      <div class="bg-white rounded-3xl shadow-2xl max-w-md w-full overflow-hidden">
        <div class="bg-gradient-to-r from-teal-500 to-cyan-500 p-6 text-center text-white">
          <img :src="counselor.avatar" :alt="counselor.name" class="w-24 h-24 rounded-full border-4 border-white shadow-lg mx-auto mb-4 bg-white">
          <h2 class="text-2xl font-bold">{{ counselor.name }}</h2>
          <p class="text-white/80 mt-1">{{ counselor.title }}</p>
        </div>
        
        <div class="p-6">
          <div class="mb-4">
            <h3 class="text-sm font-semibold text-gray-500 mb-2">专业领域</h3>
            <div class="flex flex-wrap gap-2">
              <span v-for="tag in counselor.tags" :key="tag" class="px-3 py-1 bg-teal-50 text-teal-700 text-sm rounded-full">
                {{ tag }}
              </span>
            </div>
          </div>
          
          <div class="mb-4">
            <h3 class="text-sm font-semibold text-gray-500 mb-2">从业经验</h3>
            <p class="text-slate-700">{{ counselor.experience }}</p>
          </div>
          
          <div class="mb-4">
            <h3 class="text-sm font-semibold text-gray-500 mb-2">咨询理念</h3>
            <p class="text-slate-600 italic">{{ counselor.motto }}</p>
          </div>
          
          <div class="bg-amber-50 rounded-xl p-4 text-sm text-amber-700">
            <i class="fa-solid fa-circle-info mr-2"></i>
            本AI助手仅提供心理支持和倾听服务，不替代专业医疗诊断。如有严重心理问题，请及时就医。
          </div>
        </div>
        
        <div class="p-4 border-t border-gray-100">
          <button @click="showDoctorInfo = false" class="w-full py-3 bg-gradient-to-r from-teal-500 to-cyan-500 text-white rounded-xl font-medium hover:shadow-lg transition">
            开始咨询
          </button>
        </div>
      </div>
    </div>

  </div>
</template>

<style scoped>
.hide-scrollbar::-webkit-scrollbar {
  display: none;
}
.hide-scrollbar {
  -ms-overflow-style: none;
  scrollbar-width: none;
}

/* 消息气泡动画 */
.chat-messages > div > div {
  animation: fadeInUp 0.3s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
