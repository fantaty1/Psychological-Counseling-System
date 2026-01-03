import { createRouter, createWebHistory } from 'vue-router'

// 判断当前路由是否为医生端（管理页面）
// 注意：/doctor/:id 是学生查看医生详情，不是医生端
const isDoctorRoute = (path) => {
  if (!path.startsWith('/doctor')) {
    return false
  }
  // 提取 /doctor/ 后面的部分
  const subPath = path.slice('/doctor/'.length)
  // 如果是纯数字（医生ID），则是学生端查看医生详情
  if (/^\d+$/.test(subPath)) {
    return false
  }
  // 否则是医生端管理页面
  return true
}

// 获取token的key
const getTokenKey = (path) => {
  return isDoctorRoute(path) ? 'doctor_token' : 'student_token'
}

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // ========== 通用路由 ==========
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/common/LoginView.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/common/RegisterView.vue')
    },
    {
      path: '/doctor/register',
      name: 'doctor-register',
      component: () => import('../views/common/DoctorRegisterView.vue')
    },

    // ========== 学生端路由 ==========
    {
      path: '/',
      name: 'home',
      component: () => import('../views/student/StudentHomeView.vue')
    },
    {
      path: '/ai-chat',
      name: 'ai-chat',
      component: () => import('../views/student/StudentAiChatView.vue')
    },
    {
      path: '/doctors',
      name: 'doctor-list',
      component: () => import('../views/student/StudentDoctorListView.vue')
    },
    {
      path: '/doctor/:id',
      name: 'doctor-profile',
      component: () => import('../views/student/StudentDoctorProfileView.vue')
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('../views/student/StudentProfileView.vue')
    },
    {
      path: '/appointments',
      name: 'appointments',
      component: () => import('../views/student/StudentAppointmentsView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/assessment',
      name: 'assessment',
      component: () => import('../views/student/StudentAssessmentView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/assessment/:scaleId',
      name: 'assessment-test',
      component: () => import('../views/student/StudentAssessmentTestView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/chat',
      name: 'student-chat',
      component: () => import('../views/student/StudentChatView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/chat/:sessionId',
      name: 'student-chat-session',
      component: () => import('../views/student/StudentChatView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/chat-history',
      name: 'chat-history',
      component: () => import('../views/student/StudentChatView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/assessments',
      name: 'assessments',
      component: () => import('../views/student/StudentAssessmentView.vue'),
      meta: { requiresAuth: true }
    },
    
    // ========== 咨询师端路由 ==========
    {
      path: '/doctor/home',
      name: 'doctor-home',
      component: () => import('../views/doctor/DoctorHomeView.vue'),
      meta: { requiresDoctor: true }
    },
    {
      path: '/doctor/appointments',
      name: 'doctor-appointments',
      component: () => import('../views/doctor/DoctorAppointmentsView.vue'),
      meta: { requiresDoctor: true }
    },
    {
      path: '/doctor/chat',
      name: 'doctor-chat',
      component: () => import('../views/doctor/DoctorChatView.vue'),
      meta: { requiresDoctor: true }
    },
    {
      path: '/doctor/chat/:sessionId',
      name: 'doctor-chat-session',
      component: () => import('../views/doctor/DoctorChatView.vue'),
      meta: { requiresDoctor: true }
    },
    {
      path: '/doctor/schedule',
      name: 'doctor-schedule',
      component: () => import('../views/doctor/DoctorScheduleView.vue'),
      meta: { requiresDoctor: true }
    },
    {
      path: '/doctor/profile',
      name: 'doctor-profile-settings',
      component: () => import('../views/doctor/DoctorProfileView.vue'),
      meta: { requiresDoctor: true }
    },

    // ========== 管理员端路由（隐藏入口） ==========
    {
      path: '/admin/login',
      name: 'admin-login',
      component: () => import('../views/admin/AdminLoginView.vue')
    },
    {
      path: '/admin/home',
      name: 'admin-home',
      component: () => import('../views/admin/AdminHomeView.vue'),
      meta: { requiresAdmin: true }
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 管理员路由特殊处理
  if (to.meta.requiresAdmin) {
    const adminToken = localStorage.getItem('admin_token')
    const role = localStorage.getItem('role')
    if (!adminToken) {
      next('/admin/login')
    } else if (role !== 'admin') {
      next('/admin/login')
    } else {
      next()
    }
    return
  }

  // 根据目标路由获取对应的token
  const tokenKey = getTokenKey(to.path)
  const token = localStorage.getItem(tokenKey)
  const role = localStorage.getItem('role')
  
  // 需要登录的页面
  if (to.meta.requiresAuth) {
    if (!token) {
      next('/login')
    } else {
      next()
    }
  }
  // 需要医生权限的页面
  else if (to.meta.requiresDoctor) {
    if (!token) {
      next('/login')
    } else if (role !== 'doctor') {
      next('/')
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router
